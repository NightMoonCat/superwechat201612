package cn.moon.superwechat.ui;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.moon.I;
import cn.moon.superwechat.R;
import cn.moon.superwechat.SuperWeChatHelper;
import cn.moon.superwechat.db.IUserModel;
import cn.moon.superwechat.db.InviteMessgeDao;
import cn.moon.superwechat.db.OnCompleteListener;
import cn.moon.superwechat.db.UserModel;
import cn.moon.superwechat.domain.InviteMessage;
import cn.moon.superwechat.utils.MFGT;
import cn.moon.superwechat.utils.Result;
import cn.moon.superwechat.utils.ResultUtils;

/**
 * Created by Moon on 2017/4/5.
 */

public class FriendDetailsActivity extends BaseActivity {
    User mUser = null;
    IUserModel mModel;
    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.ivAvatar)
    ImageView mIvAvatar;
    @BindView(R.id.tvNick)
    TextView mTvNick;
    @BindView(R.id.tvUserName)
    TextView mTvUserName;
    @BindView(R.id.btn_send_msg)
    Button mBtnSendMsg;
    @BindView(R.id.btn_send_video)
    Button mBtnSendVideo;
    @BindView(R.id.btn_add_contact)
    Button mBtnAddContact;
    InviteMessage msg;

    @Override
    protected void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        setContentView(R.layout.activity_friend_details);
        ButterKnife.bind(this);
        mModel = new UserModel();
        initView();
        initData();
    }

    private void initView() {
        mTitleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MFGT.finish(FriendDetailsActivity.this);
            }
        });
    }

    private void initData() {
        mUser = (User) getIntent().getSerializableExtra(I.User.TABLE_NAME);
        if (mUser != null) {
            showUserInfo();
        } else {
            InviteMessage msg = (InviteMessage) getIntent().getSerializableExtra(I.User.NICK);
            if (msg != null) {
                mUser = new User(msg.getFrom());
                mUser.setMUserNick(msg.getNickName());
                mUser.setAvatar(msg.getAvatar());
                showUserInfo();
            } else {
                MFGT.finish(FriendDetailsActivity.this);
            }
        }

    }

    private void showUserInfo() {
        boolean isFriend = SuperWeChatHelper.getInstance().getAppContactList().containsKey(mUser.getMUserName());
        if (isFriend) {
            SuperWeChatHelper.getInstance().saveAppContact(mUser);
        }
        mTvUserName.setText(mUser.getMUserName());
        EaseUserUtils.setAppUserNick(mUser, mTvNick);
        EaseUserUtils.setAppUserAvatar(FriendDetailsActivity.this,mUser,mIvAvatar);
        showFriend(isFriend);
        syncUserInfo();

    }

    private void showFriend(boolean isFriend) {
        mBtnSendMsg.setVisibility(isFriend?View.VISIBLE:View.GONE);
        mBtnSendVideo.setVisibility(isFriend?View.VISIBLE:View.GONE);
        mBtnAddContact.setVisibility(isFriend?View.GONE:View.VISIBLE);
    }
    @OnClick(R.id.btn_add_contact)
    public void addContact() {
        boolean isConfirm = true;
        if (isConfirm) {
            //发送验证消息
            MFGT.gotoSendAddFriend(FriendDetailsActivity.this,mUser.getMUserName());
        } else {
            //直接添加为好友
        }

    }
    private void syncUserInfo() {
        mModel.loadUserInfo(FriendDetailsActivity.this, mUser.getMUserName(),
                new OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (s != null) {
                            Result result = ResultUtils.getResultFromJson(s, User.class);
                            if (result != null && result.isRetMsg()) {
                                User u = (User) result.getRetData();
                                if (u != null) {
                                    if (msg != null) {
                                        ContentValues values = new ContentValues();
                                        values.put(InviteMessgeDao.COLUMN_NAME_NICK,u.getMUserNick());
                                        values.put(InviteMessgeDao.COLUMN_NAME_AVATAR,u.getAvatar());
                                        InviteMessgeDao dao = new InviteMessgeDao(FriendDetailsActivity.this);
                                        dao.updateMessage(msg.getId(),values);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }
    @OnClick(R.id.btn_send_msg)
    public void sendMsg() {
        MFGT.gotoChat(FriendDetailsActivity.this,mUser.getMUserName());
    }
}
