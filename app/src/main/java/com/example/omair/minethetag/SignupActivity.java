package com.example.omair.minethetag;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Retrofit;

import com.example.omair.minethetag.LoginActivity;
import static android.R.attr.name;
import static android.R.attr.password;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    @InjectView(R.id.username) EditText _username;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.emeraldGreen));

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void signup() {
        if (!validate()) {
            _passwordText.setText("");
            return;
        }
        _signupButton.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Green_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();
        final String name = _username.getText().toString();
        final String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.
        /* Save data and return to login screen */
        /* For now just return to login screen */
        /* Dont remove this code for now */
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignUp(name, password);
                        progressDialog.dismiss();
                    }
                }, 3000);

    }


    public void onSignUp(String name, String password) {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        signUped(name, password);
    }

    boolean resultat = false;

    boolean signUped(final String name, final String password)
    {
        String url = "https://minethetag.cf/api/user/registration";
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", name);
        params.put("password", password);
        JSONObject jsonObj = new JSONObject(params);

        JsonObjectRequest MyStringRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                if (response.toString().contains("success"))
                {
                    //login(name, password);
                    LoginActivity.Signuped.signuped = "OK";
                    Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(i);
                } else if (response.toString().contains("existing")) {
                    _passwordText.setText("");
                    //_username.setText("");
                    Toast.makeText(getApplicationContext(), "Username " + name + " is not available", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                //MyData.put("username", name); //Add the data you'd like to send to the server.
                //MyData.put("password", password);
                return MyData;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(MyStringRequest);
        return resultat;
    }

    public boolean validate() {
        boolean valid = true;

        String name = _username.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _username.setError("at least 3 characters");
            valid = false;
        } else {
            _username.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}