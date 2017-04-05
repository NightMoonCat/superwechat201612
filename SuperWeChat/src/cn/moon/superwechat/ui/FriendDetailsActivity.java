package cn.moon.superwechat.ui;

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
import cn.moon.I;
import cn.moon.superwechat.R;
import cn.moon.superwechat.utils.MFGT;

/**
 * Created by Moon on 2017/4/5.
 */

public class FriendDetailsActivity extends BaseActivity {
    User mUser = null;
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

    @Override
    protected void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        setContentView(R.layout.activity_friend_details);
        ButterKnife.bind(this);
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
            MFGT.finish(FriendDetailsActivity.this);
        }

    }

    private void showUserInfo() {
        mTvUserName.setText(mUser.getMUserName());
        EaseUserUtils.setAppUserNick(mUser.getMUserName(), mTvNick);
        EaseUserUtils.setAppUserAvatar(FriendDetailsActivity.this, mUser.getMUserName(), mIvAvatar);
    }

}
