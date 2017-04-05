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

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseTitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.moon.superwechat.R;
import cn.moon.superwechat.db.IUserModel;
import cn.moon.superwechat.db.OnCompleteListener;
import cn.moon.superwechat.db.UserModel;
import cn.moon.superwechat.utils.L;
import cn.moon.superwechat.utils.MFGT;
import cn.moon.superwechat.utils.Result;
import cn.moon.superwechat.utils.ResultUtils;

public class AddContactActivity extends BaseActivity {
    private static final String TAG = AddContactActivity.class.getSimpleName();
    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.ll_user)
    RelativeLayout mLlUser;
    @BindView(R.id.edit_note)
    EditText mEditNote;

    private ProgressDialog progressDialog;
    IUserModel mModel ;

    private String toAddUsername;

    User mUser  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_add_contact);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(AddContactActivity.this);
        mModel = new UserModel();
        initView();

    }

    private void initView() {
        String strUserName = getResources().getString(R.string.user_name);
        mEditNote.setHint(strUserName);

        mTitleBar.setLeftLayoutClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MFGT.finish(AddContactActivity.this);
            }
        });
    }


    /**
     * search contact
     *
     * @param v
     */
    public void searchContact(View v) {
        progressDialog = new ProgressDialog(AddContactActivity.this);

        String name = mEditNote.getText().toString();
        toAddUsername = name;
        if (TextUtils.isEmpty(name)) {
            new EaseAlertDialog(this, R.string.Please_enter_a_username).show();
            return;
        }
        searchUser();
        showDialog();
    }

    private void showDialog() {
        progressDialog.setMessage(getString(R.string.search));
        progressDialog.show();
    }

    private void searchUser() {
        mModel.loadUserInfo(AddContactActivity.this, toAddUsername, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                L.e(TAG,s);
                boolean success = false;
                if (s != null) {
                    Result result = ResultUtils.getResultFromJson(s, User.class);
                    if (result != null && result.isRetMsg()) {
                        success = true;
                        mUser = (User) result.getRetData();
                    }
                }
                showResult(success);
            }

            @Override
            public void onError(String error) {
                L.e(TAG,"error = " + error);
                showResult(false);
            }
        });
    }

    private void showResult(boolean success) {
        progressDialog.dismiss();
        mLlUser.setVisibility(success ? View.GONE : View.VISIBLE);
        if (success) {
            //跳转到用户详情界面
            MFGT.gotoFriendDetails(AddContactActivity.this,mUser);
        }
    }

}
