/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.moon.superwechat.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager.EMGroupOptions;
import com.hyphenate.chat.EMGroupManager.EMGroupStyle;
import com.hyphenate.easeui.domain.Group;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.moon.I;
import cn.moon.superwechat.R;
import cn.moon.superwechat.db.GroupModel;
import cn.moon.superwechat.db.IGroupModel;
import cn.moon.superwechat.db.OnCompleteListener;
import cn.moon.superwechat.utils.L;
import cn.moon.superwechat.utils.MFGT;
import cn.moon.superwechat.utils.Result;
import cn.moon.superwechat.utils.ResultUtils;

import static com.alimama.mobile.csdk.umupdate.a.f.bs;

public class NewGroupActivity extends BaseActivity {
    private static final String TAG = "NewGroupActivity";
    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.iv_group_avatar)
    ImageView mIvGroupAvatar;
    private EditText groupNameEditText;
    private ProgressDialog progressDialog;
    private EditText introductionEditText;
    private CheckBox publibCheckBox;
    private CheckBox memberCheckbox;
    private TextView secondTextView;
    IGroupModel mModel;
    File avatarFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_new_group);
        ButterKnife.bind(this);
        initView();
        groupNameEditText = (EditText) findViewById(R.id.edit_group_name);
        introductionEditText = (EditText) findViewById(R.id.edit_group_introduction);
        publibCheckBox = (CheckBox) findViewById(R.id.cb_public);
        memberCheckbox = (CheckBox) findViewById(R.id.cb_member_inviter);
        secondTextView = (TextView) findViewById(R.id.second_desc);

        mModel = new GroupModel();

        publibCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    secondTextView.setText(R.string.join_need_owner_approval);
                } else {
                    secondTextView.setText(R.string.Open_group_members_invited);
                }
            }
        });
    }

    private void initView() {
        mTitleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MFGT.finish(NewGroupActivity.this);
            }
        });
    }

    @OnClick(R.id.btnGroupSave)
    public void save() {
        String name = groupNameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            new EaseAlertDialog(this, R.string.Group_name_cannot_be_empty).show();
        } else {
            // select from contact list
            MFGT.gotoGroupPickContacts(NewGroupActivity.this, I.REQUEST_CODE_PIC_CONTACT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case I.REQUEST_CODE_PICK_PIC:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            case I.REQUEST_CODE_CUTTING:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            case I.REQUEST_CODE_PIC_CONTACT:
                if (resultCode == RESULT_OK) {
                    showDialog();
                    createEMGroup(data);
                }
                break;
        }
    }

    private void createEMGroup(final Intent data) {
        final String st2 = getResources().getString(R.string.Failed_to_create_groups);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String groupName = groupNameEditText.getText().toString().trim();
                String desc = introductionEditText.getText().toString();
                String[] members = data.getStringArrayExtra("newmembers");
                try {
                    EMGroupOptions option = new EMGroupOptions();
                    option.maxUsers = 200;
                    option.inviteNeedConfirm = true;

                    String reason = NewGroupActivity.this.getString(R.string.invite_join_group);
                    reason = EMClient.getInstance().getCurrentUser() + reason + groupName;

                    if (publibCheckBox.isChecked()) {
                        option.style = memberCheckbox.isChecked() ? EMGroupStyle.EMGroupStylePublicJoinNeedApproval : EMGroupStyle.EMGroupStylePublicOpenJoin;
                    } else {
                        option.style = memberCheckbox.isChecked() ? EMGroupStyle.EMGroupStylePrivateMemberCanInvite : EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                    //在环信服务器上新建群组
                    EMGroup emGroup = EMClient.getInstance().groupManager().createGroup(groupName, desc, members, reason, option);
                    //在本地服务器新建群组
                    createAppGroup(emGroup,members);

                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }).start();
    }

    private void createSuccess(final boolean success) {
        runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                if (success) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(NewGroupActivity.this, R.string.Failed_to_create_groups, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createAppGroup(final EMGroup emGroup, final String[] members) {
        if (emGroup != null) {
            mModel.newGroup(NewGroupActivity.this, emGroup.getGroupId(), emGroup.getGroupName(), emGroup.getDescription(),
                    emGroup.getOwner(), emGroup.isPublic(), emGroup.isAllowInvites(), avatarFile, new OnCompleteListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            L.e(TAG, "createAppGroup,s"+bs);
                            boolean success = false;
                            if (s != null) {
                                Result result = ResultUtils.getResultFromJson(s, Group.class);
                                if (result != null && result.isRetMsg()) {
                                    Group group = (Group) result.getRetData();
                                    if (group != null) {
                                        if (members.length > 0) {
                                            addMembers(emGroup.getGroupId(), getMembersString(members));
                                        } else {
                                            success = true;
                                        }

                                    }
                                }
                            }
                            if (members.length <= 0) {
                                createSuccess(success);
                            }
                        }

                        @Override
                        public void onError(String error) {
                            createSuccess(false);
                        }
                    });
        }

    }

    private String getMembersString(String[] members) {
        StringBuilder str = new StringBuilder();
        for (String member : members) {
            str.append(member).append(",");
        }
        L.e(TAG,"getMembersString,str = "+str);
        return str.toString();
    }

    private void addMembers(String hxid,String members) {
        mModel.addMembers(NewGroupActivity.this, members, hxid, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                L.e(TAG,"addMembers,onSuccess,s = "+s);
                boolean success = false;
                if (s != null) {
                    Result result = ResultUtils.getResultFromJson(s, Group.class);
                    if (result != null && result.isRetMsg()) {
                        success = true;
                    }
                }
                createSuccess(success);
            }

            @Override
            public void onError(String error) {
                createSuccess(false);
            }
        });
    }

    private void showDialog() {
        String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(st1);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, I.REQUEST_CODE_CUTTING);
    }

    /**
     * save the picture data
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(getResources(), photo);
            mIvGroupAvatar.setImageDrawable(drawable);
//            uploadUserAvatar(Bitmap2Bytes(photo));
            saveBitmapFile(photo);
        }

    }

    public void saveBitmapFile(Bitmap bitmap) {
        if (bitmap != null) {
            String imagePath = getAvatarPath(NewGroupActivity.this, I.AVATAR_TYPE) + "/" +
                    getAvatarName() + ".jpg";
            File file = new File(imagePath);

            try {
                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            avatarFile = file;
        }

    }

    private String getAvatarName() {
        String avatarName = I.AVATAR_TYPE_GROUP_PATH + System.currentTimeMillis();
        return avatarName;
    }

    private String getAvatarPath(Context context, String path) {
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File folder = new File(dir, path);
        if (!folder.exists()) {
            folder.mkdir();
        }

        return folder.getAbsolutePath();
    }

    @OnClick(R.id.selectGroupIcon)
    public void onClick() {
        selectGroupIcon();
    }

    private void selectGroupIcon() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dl_title_upload_photo);
        builder.setItems(new String[]{getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload)},
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Toast.makeText(NewGroupActivity.this, getString(R.string.toast_no_support),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(pickIntent, I.REQUEST_CODE_PICK_PIC);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }
}
