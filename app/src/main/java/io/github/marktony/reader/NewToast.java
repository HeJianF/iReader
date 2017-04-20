package io.github.marktony.reader;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * User：He Jianfeng (hjfstory@foxmail.com)
 * Data: 2017/4/20
 */
public class NewToast {
    public static void toast(Context context,int ResId, String string){
        Toast toast = android.widget.Toast.makeText(context,string, Toast.LENGTH_SHORT);
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(ResId);
        LinearLayout toastView = (LinearLayout) toast.getView();
        toastView.setOrientation(LinearLayout.HORIZONTAL);
        toastView.addView(imageView,1);
        toast.show();
    }
}
