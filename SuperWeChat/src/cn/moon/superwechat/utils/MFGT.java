package cn.moon.superwechat.utils;

import android.app.Activity;
import android.content.Intent;

import cn.moon.superwechat.R;
import cn.moon.superwechat.ui.GuideActivity;
import cn.moon.superwechat.ui.LoginActivity;
import cn.moon.superwechat.ui.MainActivity;
import cn.moon.superwechat.ui.RegisterActivity;

/**
 * Created by Moon on 2017/3/16.
 */

public class MFGT {
    public static void startActivity(Activity activity, Class cls) {
        activity.startActivity(new Intent(activity, cls));
        activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public static void startActivity(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public static void finish(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public static void gotoMain(Activity activity) {
        startActivity(activity, MainActivity.class);
    }
    public static void gotoRegister(Activity activity) {
        startActivity(activity, RegisterActivity.class);
    }
    public static void gotoGuide(Activity activity) {
        startActivity(activity, GuideActivity.class);
    }
    public static void gotoLogin(Activity activity) {
        startActivity(activity, LoginActivity.class);
    }


    public static void gotoLogin(Activity activity,int requestCode) {
        startActivityForResult(activity, new Intent(activity,LoginActivity.class),requestCode);
    }


    public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

}
