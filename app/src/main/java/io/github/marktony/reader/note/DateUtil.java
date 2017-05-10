package io.github.marktony.reader.note;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User：He Jianfeng (hjfstory@foxmail.com)
 * Data: 2017/5/10
 */
public class DateUtil {

    public static String formatDateTime() {
        return new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }

}

