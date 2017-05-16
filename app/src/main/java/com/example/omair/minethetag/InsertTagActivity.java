package com.example.omair.minethetag;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class InsertTagActivity extends AppCompatActivity {

    public static final String TAG = "NfcDemo";

    private TextView mTextView;
    private NfcAdapter mNfcAdapter;
    private PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_tag);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        mTextView = (TextView) findViewById(R.id.textView_explanation);

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


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
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.d("NFC", "INTENT RECV");
        Log.d("Intent action", intent.getAction().toString());
        if (intent != null && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            mTextView.setText(tagFromIntent.getId().toString());
        }
    }

}
