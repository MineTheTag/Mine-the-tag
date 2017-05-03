package com.example.omair.minethetag;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

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
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {

            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = _nameText.getText().toString();
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
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);

    }


    public void onSignUp(String name, String password) {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);

        signUped(name, password);
        /*
        String signup = "YES";
        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
        i.putExtra("FromSignUp", signup);
        startActivity(i);*/
        //finish();
    }

    void signUped(final String name, final String password)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String call = "https://minethetag.cf/api/user/registration";
        URL url = null;
        try {
            url = new URL(call);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        final String Response = "null";
        StringRequest postRequest = new StringRequest(Request.Method.POST, call, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "response " + response, Toast.LENGTH_SHORT).show();
                Log.d("Response", response);
            }
        },
            new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.d("Error.Response", Response);
                }
            }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("username", name);
                params.put("password", password);
                return params;
            }
        };
        queue.add(postRequest);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
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