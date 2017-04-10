package cn.moon.superwechat.db;

import android.content.Context;

import java.io.File;

/**
 * Created by Moon on 2017/4/10.
 */

public interface IGroupModel {
    void newGroup(Context context, String hxid, String groupName, String desc, String owner
            , boolean isPublic, boolean isInvites, File file, OnCompleteListener<String> listener);
}