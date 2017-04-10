package cn.moon.superwechat.db;

import android.content.Context;

import java.io.File;

import cn.moon.I;
import cn.moon.superwechat.utils.OkHttpUtils;

/**
 * Created by Moon on 2017/4/10.
 */

public class GroupModel implements IGroupModel {
    @Override
    public void newGroup(Context context, String hxid, String groupName, String desc, String owner, boolean isPublic, boolean isInvites, File file, OnCompleteListener<String> listener) {
        OkHttpUtils<String > utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_CREATE_GROUP)
                .addParam(I.Group.HX_ID,hxid)
                .addParam(I.Group.NAME,groupName)
                .addParam(I.Group.DESCRIPTION,desc)
                .addParam(I.Group.OWNER,owner)
                .addParam(I.Group.IS_PUBLIC,String.valueOf(isPublic))
                .addParam(I.Group.ALLOW_INVITES,String.valueOf(isInvites))
                .addFile2(file)
                .targetClass(String.class)
                .post()
                .execute(listener);
    }
}
