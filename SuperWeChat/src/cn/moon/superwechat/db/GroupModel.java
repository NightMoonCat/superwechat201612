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

    @Override
    public void addMembers(Context context, String members, String hxid, OnCompleteListener<String> listener) {
        OkHttpUtils<String > utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_ADD_GROUP_MEMBERS)
                .addParam(I.Member.USER_NAME,members)
                .addParam(I.Member.GROUP_HX_ID,hxid)
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void deleteGroupMember(Context context, String groupId, String userName, OnCompleteListener<String> listener) {
        OkHttpUtils<String > utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DELETE_GROUP_MEMBER)
                .addParam(I.Member.GROUP_ID,groupId)
                .addParam(I.Member.USER_NAME,userName)
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void findGroupByHxId(Context context, String hxid, OnCompleteListener<String> listener) {
        OkHttpUtils<String > utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_GROUP_BY_HXID)
                .addParam(I.Group.HX_ID,hxid)
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void updateGroupNameByHxId(Context context, String hxid, String newName, OnCompleteListener<String> listener) {
        OkHttpUtils<String > utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_GROUP_NAME_BY_HXID)
                .addParam(I.Group.HX_ID,hxid)
                .addParam(I.Group.NAME,newName)
                .targetClass(String.class)
                .execute(listener);
    }
}
