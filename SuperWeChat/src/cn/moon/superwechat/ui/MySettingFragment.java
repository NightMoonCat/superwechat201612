package cn.moon.superwechat.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.moon.superwechat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MySettingFragment extends Fragment implements View.OnClickListener {


    @BindView(R.id.ivAvatar)
    ImageView mIvAvatar;
    @BindView(R.id.tvNick)
    TextView mTvNick;
    @BindView(R.id.tvUserName)
    TextView mTvUserName;

    public MySettingFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_setting, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        String userName = EMClient.getInstance().getCurrentUser();
        mTvUserName.setText(userName);
        EaseUserUtils.setAppUserNick(userName,mTvNick);
        EaseUserUtils.setAppUserAvatar(getContext(),userName,mIvAvatar);

    }

    @Override
    public void onClick(View view) {

    }
}
