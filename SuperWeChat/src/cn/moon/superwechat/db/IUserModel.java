package cn.moon.superwechat.db;

import android.content.Context;

/**
 * Created by Moon on 2017/3/29.
 */

public interface IUserModel {
    void register(Context context,String userName,String nickName,
                  String password,OnCompleteListener<String> listener);
    void login(Context context,String userName,
                  String password,OnCompleteListener<String> listener);
    void unRegister(Context context,String userName,
                    OnCompleteListener<String> listener);
    void loadUserInfo(Context context,String userName,
                    OnCompleteListener<String> listener);

    void updateNick(Context context,String userName,
                    String newNickName,OnCompleteListener<String> listener);

}
