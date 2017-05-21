package com.example.omair.minethetag;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.omair.minethetag.LoginActivity.TOKEN;
import static com.example.omair.minethetag.LoginActivity.gpassword;
import static com.example.omair.minethetag.LoginActivity.gusername;
import static com.example.omair.minethetag.LoginActivity.latitude;
import static com.example.omair.minethetag.LoginActivity.longitude;

public class NewTagActivity extends AppCompatActivity {

    public static final String TAG = "NfcDemo";

    private TextView mTextView;
    private NfcAdapter mNfcAdapter;
    private PendingIntent pendingIntent;
    public Long idl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tag);

        mTextView = (TextView) findViewById(R.id.textView_explanation);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            mTextView.setText("NFC is disabled.");
        } else {
            mTextView.setText("Explanation");
        }

        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        // TODO: handle Intent
    }

    public void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    public void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    public void onNewIntent(Intent intent) {
        //Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.d("NFC", "INTENT RECV");
        Log.d("Intent action", intent.getAction().toString());
        if (intent != null && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            idl = toDec(id);
            alta_tag(latitude, longitude);
        }
    }

    void pinta_tag()
    {
        MapView map = (MapView) findViewById(R.id.mapview);
        ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();
        OverlayItem mina = new OverlayItem("New", "TAG", new GeoPoint(latitude, longitude));
        Drawable newMarker = getResources().getDrawable(R.drawable.tag_propi);
        mina.setMarker(newMarker);
        overlayItemArray.add(mina);

        MyOwnItemizedOverlay overlay = new MyOwnItemizedOverlay(getApplicationContext(), overlayItemArray);
        map.getOverlays().add(overlay);
        map.invalidate();
    }

    private long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    void alta_tag(Double latitude, Double longitude)
    {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "https://minethetag.cf/api/tags/new";
        Map<String, Double> params = new HashMap<String, Double>();
        params.put("x_pos", latitude);
        params.put("y_pos", longitude);
        JSONObject jsonObj = new JSONObject(params);
        boolean result = false;
        try {
            jsonObj.put("tag_id", idl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest MyStringRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                if (response.toString().contains("already"))
                {
                    mTextView.setText("TAG already exists");
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "New TAG created", Toast.LENGTH_LONG).show();
                    mTextView.setText("New Tag created");
                    pinta_tag();
                }
                Handler a = new Handler();
                a.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(NewTagActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, 4000);
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Toast.makeText(getApplicationContext(), "ERROR = " + error.toString(), Toast.LENGTH_SHORT).show();
                Log.d("NEW: ", "MISSATGES = " + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = gusername + ":" + gpassword;
                String tok = TOKEN + ":NONE";
                String auth = "Basic "
                        + Base64.encodeToString(tok.getBytes(), Base64.NO_WRAP);
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        MyStringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        MyRequestQueue.add(MyStringRequest);
    }
}