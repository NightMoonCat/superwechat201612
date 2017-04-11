package cn.moon.superwechat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.domain.Group;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.moon.superwechat.R;
import cn.moon.superwechat.db.GroupModel;
import cn.moon.superwechat.db.IGroupModel;
import cn.moon.superwechat.db.OnCompleteListener;
import cn.moon.superwechat.utils.CommonUtils;
import cn.moon.superwechat.utils.Result;
import cn.moon.superwechat.utils.ResultUtils;

public class PublicGroupsSeachActivity extends BaseActivity {
    @BindView(R.id.avatar)
    ImageView mAvatar;
    private RelativeLayout containerLayout;
    private EditText idET;
    private TextView nameText;
    public static EMGroup searchedGroup;
    IGroupModel mModel;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_public_groups_search);
        ButterKnife.bind(this);
        mModel = new GroupModel();


        containerLayout = (RelativeLayout) findViewById(R.id.rl_searched_group);
        idET = (EditText) findViewById(R.id.et_search_id);
        nameText = (TextView) findViewById(R.id.name);

        searchedGroup = null;
    }

    /**
     * search group with group id
     *
     * @param v
     */
    public void searchGroup(View v) {
        if (TextUtils.isEmpty(idET.getText())) {
            return;
        }

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getResources().getString(R.string.searching));
        pd.setCancelable(false);
        pd.show();

        searchEMGroup(pd);

    }

    private void searchAppGroup(EMGroup emGroup) {
        mModel.findPublicGroupByHxId(PublicGroupsSeachActivity.this, emGroup.getGroupId(),
                new OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (s != null) {
                            Result result = ResultUtils.getResultFromJson(s, Group.class);
                            if (result != null && result.isRetMsg()) {
                                Group group = (Group) result.getRetData();
                                containerLayout.setVisibility(View.VISIBLE);
                                nameText.setText(group.getMGroupName());
                                EaseUserUtils.setGroupAvatar(PublicGroupsSeachActivity.this,
                                        group.getAvatar(), mAvatar);
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        CommonUtils.showShortToast(R.string.group_search_failed);
                    }
                });
    }

    public void searchEMGroup(final ProgressDialog pd) {
        new Thread(new Runnable() {

            public void run() {
                try {
                    searchedGroup = EMClient.getInstance().groupManager().getGroupFromServer(idET.getText().toString());
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();

                            searchAppGroup(searchedGroup);

//                            containerLayout.setVisibility(View.VISIBLE);
//                            nameText.setText(searchedGroup.getGroupName());
                        }
                    });

                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            searchedGroup = null;
                            containerLayout.setVisibility(View.GONE);
                            if (e.getErrorCode() == EMError.GROUP_INVALID_ID) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.group_not_existed), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.group_search_failed) + " : " + getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }


    /**
     * enter the detail screen of group
     *
     * @param view
     */
    public void enterToDetails(View view) {
        startActivity(new Intent(this, GroupSimpleDetailActivity.class));
    }
}
