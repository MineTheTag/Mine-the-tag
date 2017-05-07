package com.example.omair.minethetag;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.util.Base64;

public class HttpBasicAuth {

    public void authenticate(String uname, String paswd, String uri) {
        String encoded = Base64.encodeToString((uname + ":" + paswd).getBytes(), 1);

        try {
            URL url = new URL(uri);
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Authorization", "Basic " + encoded);
                System.out.println("response " + conn.getResponseCode());

            } catch (IOException e) {
                System.out.println("HttpBasicAuth IOException");
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            System.out.println("HttpBasicAuth MalformedURLException");
            e.printStackTrace();
        }
    }
}