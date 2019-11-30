package com.example.zone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

public class LoginActivity extends Activity {
    private static final String TAG = "";
    SessionCallback callback;
    String token = "";
    String name = "";
    String image_url="";
    private LoginButton btn_kakao_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getHashKey();
        System.out.println(loadShared() + "dasdas");
        if (loadShared().equals("")) {
            setContentView(R.layout.activity_login);
            btn_kakao_login = findViewById(R.id.com_kakao_login);
            btn_kakao_login.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    btn_kakao_login.performClick();
                }
            });
            btn_kakao_login = (LoginButton) findViewById(R.id.com_kakao_login);
            callback = new SessionCallback();
            Session.getCurrentSession().addCallback(callback);
            requestMe();
        }

        else if (!loadShared().equals("")) {
            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
            intent.putExtra("user_name", name);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {

            UserManagement.requestMe(new MeResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        //에러로 인한 로그인 실패
                        // finish();
                    } else {
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }

                @Override
                public void onNotSignedUp() {
                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                    //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.

                    Log.e("UserProfile", userProfile.toString());
                    // Log.e("UserProfile", userProfile.getId() + "");
                    System.out.println("dmstjr");
                    long number = userProfile.getId();
                    Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                    intent.putExtra("user_name", name);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }

        // 세션 실패시
        @Override
        public void onSessionOpenFailed(KakaoException exception) {


        }
    }

    public void requestMe() {
        //유저의 정보를 받아오는 함수
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.e(TAG, "error message=" + errorResult);
//                super.onFailure(errorResult);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d(TAG, "onSessionClosed1 =" + errorResult);
            }

            @Override
            public void onNotSignedUp() {
                //카카오톡 회원이 아닐시
                Log.d(TAG, "onNotSignedUp ");
            }

            @Override
            public void onSuccess(UserProfile result) {
                Log.e("UserProfile", result.toString());
                Log.e("UserProfile", result.getId() + "");

                System.out.println("dmstjr3");
                saveShared(result.getId() + "", result.getNickname(),result.getThumbnailImagePath());
            }
        });
    }

    private void saveShared(String id, String name,String profile_url) {
        SharedPreferences pref = getSharedPreferences("profile", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", id);
        editor.putString("name", name);
        editor.putString("image_url",profile_url);
        editor.apply();
    }

    /*쉐어드값 불러오기*/
    private String loadShared() {
        SharedPreferences pref = getSharedPreferences("profile", MODE_PRIVATE);
        token = pref.getString("token", "");
        name = pref.getString("name", "");
        image_url=pref.getString("image_url","");
        System.out.println("xhzms"+token+name+image_url);
        return token;
    }
}
