package io.github.marktony.reader.jd;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.github.marktony.reader.R;
import io.github.marktony.reader.adapter.JiandanArticleAdapter;
import io.github.marktony.reader.data.Jiandan;
import io.github.marktony.reader.interfaze.OnRecyclerViewClickListener;
import io.github.marktony.reader.interfaze.OnRecyclerViewLongClickListener;


/**
 * Created by Lizhaotailang on 2016/8/5.
 * <p>
 * 对煎蛋界面的操作，创建view，加载内容等
 */

public class JiandanFragment extends Fragment
        implements JiandanContract.View {

    private JiandanContract.Presenter presenter;
    private JiandanArticleAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;

    public JiandanFragment() {

    }

    public static JiandanFragment newInstance(int page) {
        return new JiandanFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.joke_list_fragment, container, false);

        initViews(view);

        presenter.requestArticles(true);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.requestArticles(true);
                adapter.notifyDataSetChanged();
                stopLoading();
            }
        });

        //添加滚动监听事件
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //获取管理权限
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 获取最后一个完全显示的itemposition
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();

                    // 判断是否滚动到底部并且是向下滑动
                    if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
                        presenter.loadMore();
                    }
                }

                super.onScrollStateChanged(recyclerView, newState);
            }

            //在滚动结束后调用的方法，用来判断是否发生滚动
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //若发生滚动则dy大于0，为true，没有横向滚动，所以没有dx的判断
                isSlidingToLast = dy > 0;
            }
        });

        return view;
    }

    @Override
    public void showResult(ArrayList<Jiandan.Comment> articleList) {

        //若adapter还为创建则为null，进行创建，若已经创建过了，则不要再次进行创建了，节约资源
        if (adapter == null) {
            adapter = new JiandanArticleAdapter(getActivity(), articleList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        //分享
        adapter.setItemClickListener(new OnRecyclerViewClickListener() {
            @Override
            public void OnClick(View v, int position) {
                presenter.shareTo(position);
            }

        });

        //复制
        adapter.setItemLongClickListener(new OnRecyclerViewLongClickListener() {
            @Override
            public void OnLongClick(View view, int position) {
                presenter.copyToClipboard(position);
            }
        });
    }

    @Override
    public void startLoading() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void stopLoading() {
        if (refreshLayout.isRefreshing()) {
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public void initViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.qsbk_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
    }

    //使用snackBar来提示加载失败
    @Override
    public void showLoadError() {
        Snackbar.make(recyclerView, "加载失败", Snackbar.LENGTH_SHORT)
                .setAction("重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //将原来的false修改为true
                        presenter.requestArticles(true);
                    }
                }).show();
    }

    @Override
    public void setPresenter(JiandanContract.Presenter presenter) {
        if (presenter != null) {
            this.presenter = presenter;
        }
    }

    //重新加载
    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

}
