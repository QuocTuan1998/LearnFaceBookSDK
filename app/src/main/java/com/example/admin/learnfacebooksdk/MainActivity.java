package com.example.admin.learnfacebooksdk;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    ProfilePictureView imgAvatar;
    LoginButton btnSignIn;
    Button btnFunc, btnSignOut;
    TextView txtName, txtFirstName, txtEmail;
    String name, email, firstName;

    CallbackManager callbackManager;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode,data);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);

//        getKeyHash();
        addControls();

        btnFunc.setVisibility(View.INVISIBLE);
        imgAvatar.setVisibility(View.INVISIBLE);
        btnSignOut.setVisibility(View.INVISIBLE);
        txtEmail.setVisibility(View.INVISIBLE);
        txtFirstName.setVisibility(View.INVISIBLE);
        txtName.setVisibility(View.INVISIBLE);

        callbackManager = CallbackManager.Factory.create();
        btnSignIn.setReadPermissions(Arrays.asList("public_profile","email"));
        handleLogin();
        handleLogout();

        btnFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FuncActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handleLogout() {
        btnSignOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                btnFunc.setVisibility(View.INVISIBLE);
                imgAvatar.setVisibility(View.INVISIBLE);
                btnSignOut.setVisibility(View.INVISIBLE);
                txtEmail.setVisibility(View.INVISIBLE);
                txtFirstName.setVisibility(View.INVISIBLE);
                txtName.setVisibility(View.INVISIBLE);
                txtEmail.setText("");
                txtFirstName.setText("");
                txtName.setText("");
                imgAvatar.setProfileId(null);
                btnSignIn.setVisibility(View.VISIBLE);
            }
        });
    }

    private void handleLogin() {

        btnSignIn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                btnSignIn.setVisibility(View.INVISIBLE);
                btnFunc.setVisibility(View.VISIBLE);
                imgAvatar.setVisibility(View.VISIBLE);
                btnSignOut.setVisibility(View.VISIBLE);
                txtEmail.setVisibility(View.VISIBLE);
                txtFirstName.setVisibility(View.VISIBLE);
                txtName.setVisibility(View.VISIBLE);
                result();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void result() {

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d("JSON",response.getJSONObject().toString());

                try {
                    email = object.getString("email");
                    name = object.getString("name");
                    firstName = object.getString("first_name");

                    imgAvatar.setProfileId(Profile.getCurrentProfile().getId());
                    txtEmail.setText(email);
                    txtName.setText(name);
                    txtFirstName.setText(firstName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle para = new Bundle();
        para.putString("fields","name,email,first_name");
        request.setParameters(para);
        request.executeAsync();
    }

    private void addControls() {

        imgAvatar = findViewById(R.id.imgAvatar);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnFunc = findViewById(R.id.btnFunc);
        btnSignOut = findViewById(R.id.btnSignOut);
        txtEmail = findViewById(R.id.txtEmail);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtName = findViewById(R.id.txtName);

    }

    @Override
    protected void onStart() {
        super.onStart();

        LoginManager.getInstance().logOut();
    }

    private void getKeyHash() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.admin.learnfacebooksdk",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}
