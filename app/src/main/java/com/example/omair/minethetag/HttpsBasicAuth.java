package com.example.omair.minethetag;

import android.view.Display;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import android.util.Base64;
import android.widget.Toast;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsBasicAuth {

    public void doWork(TrustManager[] trusteds) {
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trusteds, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            System.out.println("oops");
            e.printStackTrace();
        }
    }

    public void authenticate(String uname, String paswd, String uri) {

        String encoded = Base64.encodeToString((uname + ":" + paswd).getBytes(), 1);

        try {
            URL url = new URL(uri);
            try {
                doWork(disableCertificateValidation());
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestProperty("Authorization", "Basic " + encoded);
                System.out.println("response " + conn.getResponseCode());

            } catch (IOException e) {
                System.out.println("HttpsBasicAuth IOException");
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            System.out.println("HttpsBasicAuth MalformedURLException");
            e.printStackTrace();
        }
    }

    //this is the only part that gets run
    public TrustManager[] disableCertificateValidation() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}}};
        System.out.println("disabled");
        return trustAllCerts;
    }
}
