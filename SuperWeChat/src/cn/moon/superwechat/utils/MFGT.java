package cn.moon.superwechat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.easemob.redpacketui.utils.RedPacketUtil;
import com.hyphenate.easeui.domain.User;

import cn.moon.I;
import cn.moon.superwechat.R;
import cn.moon.superwechat.domain.InviteMessage;
import cn.moon.superwechat.ui.AddContactActivity;
import cn.moon.superwechat.ui.ChatActivity;
import cn.moon.superwechat.ui.FriendDetailsActivity;
import cn.moon.superwechat.ui.GroupPickContactsActivity;
import cn.moon.superwechat.ui.GroupsActivity;
import cn.moon.superwechat.ui.GuideActivity;
import cn.moon.superwechat.ui.LoginActivity;
import cn.moon.superwechat.ui.MainActivity;
import cn.moon.superwechat.ui.NewFriendsMsgActivity;
import cn.moon.superwechat.ui.RegisterActivity;
import cn.moon.superwechat.ui.SendAddFriendActivity;
import cn.moon.superwechat.ui.SettingsActivity;
import cn.moon.superwechat.ui.UserProfileActivity;

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
        startActivity(activity,new Intent(activity,LoginActivity.class)
        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK));
    }


    public static void gotoLogin(Activity activity,int requestCode) {
        startActivityForResult(activity, new Intent(activity,LoginActivity.class),requestCode);
    }


    public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public static void gotoSetting(Activity activity) {
        startActivity(activity,SettingsActivity.class);
    }

    public static void gotoUserProfile(Activity activity) {
        startActivity(activity, UserProfileActivity.class);
    }
    public static void gotoMoney(Activity activity) {
        RedPacketUtil.startChangeActivity(activity);
    }

    public static void gotoAddContact(Activity activity) {
        startActivity(activity, AddContactActivity.class);
    }

    public static void gotoFriendDetails(Activity activity, User user) {
        startActivity(activity,new Intent(activity, FriendDetailsActivity.class)
        .putExtra(I.User.TABLE_NAME,user));
    }

    public static void gotoSendAddFriend(Activity activity, String userName) {
        startActivity(activity, new Intent(activity, SendAddFriendActivity.class)
                .putExtra(I.User.USER_NAME, userName));
    }

    public static void gotoNewFriendMsg(Activity activity) {
        startActivity(activity, NewFriendsMsgActivity.class);

    }

    public static void gotoGroups(Activity activity) {
        startActivity(activity, GroupsActivity.class);
    }

    public static void gotoFriendDetails(Context activity, InviteMessage msg) {
        startActivity((Activity) activity,new Intent(activity,FriendDetailsActivity.class)
        .putExtra(I.User.NICK,msg));
    }
    public static void gotoFriendDetails(Context activity, String username) {
        startActivity((Activity) activity,new Intent(activity,FriendDetailsActivity.class)
        .putExtra(I.User.USER_NAME,username));
    }

    public static void gotoChat(Activity activity, String userName) {
        startActivity(activity,new Intent(activity, ChatActivity.class)
                .putExtra("userId", userName));

    }

    public static void gotoMain(Activity activity, boolean isChat) {
        startActivity(activity,new Intent(activity,MainActivity.class)
        .putExtra(I.IS_FROM_CHAT,isChat));
    }

    public static void gotoGroupPickContacts(Activity activity,int i) {
        startActivityForResult(activity,new Intent(activity,GroupPickContactsActivity.class),i);
    }
}
