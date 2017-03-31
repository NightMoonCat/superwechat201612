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
import butterknife.OnClick;
import cn.moon.superwechat.Constant;
import cn.moon.superwechat.R;
import cn.moon.superwechat.utils.MFGT;

/**
 * A simple {@link Fragment} subclass.
 */
public class MySettingFragment extends Fragment {


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
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        initData();
    }

    private void initData() {
        String userName = EMClient.getInstance().getCurrentUser();
        mTvUserName.setText(userName);
        EaseUserUtils.setAppUserNick(userName, mTvNick);
        EaseUserUtils.setAppUserAvatar(getContext(), userName, mIvAvatar);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (((MainActivity) getActivity()).isConflict) {
            outState.putBoolean("isConflict", true);
        } else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }
    }


    @OnClick({R.id.layout_info_setting, R.id.layout_money, R.id.layout_setting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_info_setting:
                MFGT.gotoUserProfile(getActivity());
                break;
            case R.id.layout_money:
                MFGT.gotoChange(getActivity());
                break;
            case R.id.layout_setting:
                MFGT.gotoSetting(getActivity());
                break;
        }
    }
}