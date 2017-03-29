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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.moon.I;
import cn.moon.superwechat.R;
import cn.moon.superwechat.SuperWeChatHelper;
import cn.moon.superwechat.db.IUserModel;
import cn.moon.superwechat.db.OnCompleteListener;
import cn.moon.superwechat.db.UserModel;
import cn.moon.superwechat.utils.CommonUtils;
import cn.moon.superwechat.utils.L;
import cn.moon.superwechat.utils.MD5;
import cn.moon.superwechat.utils.MFGT;
import cn.moon.superwechat.utils.Result;
import cn.moon.superwechat.utils.ResultUtils;

/**
 * register screen
 */
public class RegisterActivity extends BaseActivity {
    private static final String TAG = "RegisterActivity";
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.username)
    EditText mUsername;
    @BindView(R.id.userNick)
    EditText mUserNick;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.confirm_password)
    EditText mConfirmPassword;
    @BindView(R.id.ivBack)
    ImageView mIvBack;

    String username, nickName, password;

    ProgressDialog pd;

    IUserModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_register);
        ButterKnife.bind(this);
        initView();
        mModel = new UserModel();
    }

    private void initView() {
        mIvBack.setVisibility(View.VISIBLE);
        mTvTitle.setText(R.string.register);
        pd = new ProgressDialog(RegisterActivity.this);
    }

    private void showDialog() {

        pd.setMessage(getResources().getString(R.string.Is_the_registered));
        pd.show();
    }

    public boolean checkInput() {
        username = mUsername.getText().toString().trim();
        nickName = mUserNick.getText().toString().trim();
        password = mPassword.getText().toString().trim();
        String confirm_pwd = mConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
            mUsername.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(nickName)) {
            Toast.makeText(this, getResources().getString(R.string.UserNick_cannot_be_empty), Toast.LENGTH_SHORT).show();
            mUserNick.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            mPassword.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            mConfirmPassword.requestFocus();
            return false;
        } else if (!password.equals(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    private void registerEMServer() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // call method in SDK
                    EMClient.getInstance().createAccount(username, MD5.getMessageDigest(password));
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();
                            // save current user
                            SuperWeChatHelper.getInstance().setCurrentUserName(username);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
                            MFGT.gotoLogin(RegisterActivity.this);
                        }
                    });
                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            unRegister();

                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();
                            int errorCode = e.getErrorCode();
                            if (errorCode == EMError.NETWORK_ERROR) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    public void back(View view) {
        finish();
    }

    @OnClick(R.id.ivBack)
    public void back() {
        MFGT.finish(RegisterActivity.this);
    }

    @OnClick(R.id.btnRegister)
    public void register() {
        if (checkInput()) {
            showDialog();
            mModel.register(RegisterActivity.this, username, nickName, MD5.getMessageDigest(password),
                    new OnCompleteListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            if (s != null) {
                                Result result = ResultUtils.getResultFromJson(s, String.class);
                                if (result != null) {
                                    if (result.isRetMsg()) {
                                        registerEMServer();
                                    } else if (result.getRetCode() == I.MSG_REGISTER_USERNAME_EXISTS) {
                                        CommonUtils.showShortToast(R.string.User_already_exists);
                                    } else {
                                        CommonUtils.showShortToast(R.string.Registration_failed);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(String error) {
                            pd.dismiss();
                            CommonUtils.showShortToast(R.string.Registration_failed);
                        }
                    });
        }
    }
    private void unRegister() {
        mModel.unRegister(RegisterActivity.this, username, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String result) {
                L.e(TAG,"result="+result);
            }

            @Override
            public void onError(String error) {

            }
        });
    }
}
