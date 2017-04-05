package cn.moon.superwechat.ui;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.moon.I;
import cn.moon.superwechat.R;
import cn.moon.superwechat.SuperWeChatHelper;
import cn.moon.superwechat.utils.MFGT;

public class UserProfileActivity extends BaseActivity {

    private static final int REQUESTCODE_PICK = 1;
    private static final int REQUESTCODE_CUTTING = 2;
    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.user_head_avatar)
    ImageView headAvatar;
    @BindView(R.id.user_nickname)
    TextView tvNickName;
    @BindView(R.id.user_username)
    TextView tvUsername;

    UpdateNickReceiver mReceiver;
    UpdateAvatarReceiver mUpdateAvatarReceiver;

    private ProgressDialog dialog;

    User user;
    private String avatarName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_user_profile);
        ButterKnife.bind(this);

        initView();
        initData();
        initListener();

    }

    private void initListener() {
        mReceiver = new UpdateNickReceiver();
        IntentFilter filter = new IntentFilter(I.REQUEST_UPDATE_USER_NICK);
        registerReceiver(mReceiver, filter);
        mUpdateAvatarReceiver = new UpdateAvatarReceiver();
        IntentFilter filter2 = new IntentFilter(I.REQUEST_UPDATE_AVATAR);
        registerReceiver(mUpdateAvatarReceiver, filter2);
    }

    private void initData() {
        user = SuperWeChatHelper.getInstance().getUserProfileManager().getCurrentAppUserInfo();
        if (user == null) {
            finish();
        } else {
            showUserInfo();
        }
    }

    private void initView() {
        mTitleBar.setLeftImageResource(R.drawable.em_mm_title_back);
        mTitleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MFGT.finish(UserProfileActivity.this);
            }
        });
    }

    private void showUserInfo() {
        tvUsername.setText(user.getMUserName());
        EaseUserUtils.setUserNick(user.getMUserName(), tvNickName);
        EaseUserUtils.setUserAvatar(this, user.getMUserName(), headAvatar);
    }


    @OnClick(R.id.rl_nickname)
    public void updateNick() {
        final EditText editText = new EditText(this);
        editText.setText(user.getMUserNick());
        editText.setSelectAllOnFocus(true);
        new Builder(this).setTitle(R.string.setting_nickname).setIcon(android.R.drawable.ic_dialog_info).setView(editText)
                .setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nickString = editText.getText().toString();
                        if (TextUtils.isEmpty(nickString)) {
                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        updateRemoteNick(nickString);
                    }
                }).setNegativeButton(R.string.dl_cancel, null).show();
    }

//    public void asyncFetchUserInfo(String username) {
//        SuperWeChatHelper.getInstance().getUserProfileManager().asyncGetUserInfo(username, new EMValueCallBack<EaseUser>() {
//
//            @Override
//            public void onSuccess(EaseUser user) {
//                if (user != null) {
//                    SuperWeChatHelper.getInstance().saveContact(user);
//                    if (isFinishing()) {
//                        return;
//                    }
//                    tvNickName.setText(user.getNick());
//                    if (!TextUtils.isEmpty(user.getAvatar())) {
//                        Glide.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(R.drawable.em_default_avatar).into(headAvatar);
//                    } else {
//                        Glide.with(UserProfileActivity.this).load(R.drawable.em_default_avatar).into(headAvatar);
//                    }
//                }
//            }
//
//            @Override
//            public void onError(int error, String errorMsg) {
//            }
//        });
//    }

    @OnClick(R.id.user_head_avatar)
    public void uploadHeadPhoto() {
        Builder builder = new Builder(this);
        builder.setTitle(R.string.dl_title_upload_photo);
        builder.setItems(new String[]{getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload)},
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Toast.makeText(UserProfileActivity.this, getString(R.string.toast_no_support),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        startActivityForResult(intent, REQUESTCODE_CUTTING);
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
            headAvatar.setImageDrawable(drawable);
//            uploadUserAvatar(Bitmap2Bytes(photo));
            uploadAppUserAvatar(saveBitmapFile(photo));
        }

    }

    private void uploadAppUserAvatar(File file) {
        dialog = ProgressDialog.show(this, getString(R.string.dl_update_nick), getString(R.string.dl_waiting));
        SuperWeChatHelper.getInstance().getUserProfileManager().uploadUserAvatar(file);
    }

    public File saveBitmapFile(Bitmap bitmap) {
        if (bitmap != null) {
            String imagePath = getAvatarPath(UserProfileActivity.this, I.AVATAR_TYPE) + "/" +
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
            return file;
        }
        return null;

    }

    private String getAvatarName() {
        avatarName = user.getMUserName() + System.currentTimeMillis();
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

    private void updateRemoteNick(final String nickName) {
        SuperWeChatHelper.getInstance().getUserProfileManager().updateCurrentUserNickName(nickName);
    }


//    private void uploadUserAvatar(final byte[] data) {
//        dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                final String avatarUrl = SuperWeChatHelper.getInstance().getUserProfileManager().uploadUserAvatar(data);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialog.dismiss();
//                        if (avatarUrl != null) {
//                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success),
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_fail),
//                                    Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });
//
//            }
//        }).start();
//
//        dialog.show();
//    }


    private void updateNickView(boolean success) {
        if (!success) {
            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
                    .show();
            dialog.dismiss();
        } else {
            dialog.dismiss();
            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_success), Toast.LENGTH_SHORT)
                    .show();
            user = SuperWeChatHelper.getInstance().getUserProfileManager().getCurrentAppUserInfo();
            tvNickName.setText(user.getMUserNick());
        }
    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    class UpdateNickReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(I.User.NICK, false);
            updateNickView(success);
        }
    }

    class UpdateAvatarReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(I.Avatar.UPDATE_TIME, false);
            updateAvatarView(success);
        }


    }

    private void updateAvatarView(boolean success) {
        dialog.dismiss();
        if (success) {
            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success),
                    Toast.LENGTH_SHORT).show();
            user = SuperWeChatHelper.getInstance().getUserProfileManager().getCurrentAppUserInfo();
            EaseUserUtils.setAppUserAvatar(UserProfileActivity.this, user.getMUserName(), headAvatar);
        } else {
            Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_fail),
                    Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        if (mUpdateAvatarReceiver != null) {
            unregisterReceiver(mUpdateAvatarReceiver);
        }
    }
}
