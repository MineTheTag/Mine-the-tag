package com.example.omair.minethetag;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.HashMap;
import java.util.Map;

import static com.example.omair.minethetag.LoginActivity.TOKEN;
import static com.example.omair.minethetag.LoginActivity.gpassword;
import static com.example.omair.minethetag.LoginActivity.gusername;
import static com.example.omair.minethetag.LoginActivity.latitude;
import static com.example.omair.minethetag.LoginActivity.longitude;

/**
 * Created by dpini on 18/05/17.
 */

public class Tag {

    Long id;
    Double x;
    Double y;
    Integer owner_id;
    String owner_name;
    OverlayItem map_item;

    public Tag(Long id, Double x, Double y){
        this.id = id;
        this.x = x;
        this.y = y;
        this.owner_id  = 0;
        this.owner_name = "";
        map_item = new OverlayItem("Mina",null, new GeoPoint(x, y));
        Drawable newMarker = Resources.getSystem().getDrawable(R.drawable.tag_propi);
        map_item.setMarker(newMarker);
    }

    public Tag(Long id, Double x, Double y, Integer owner_id, String owner_name){
        this.id = id;
        this.x = x;
        this.y = y;
        this.owner_id  = owner_id;
        this.owner_name = owner_name;

        map_item = new OverlayItem("Mina",null, new GeoPoint(x, y));
        Drawable newMarker = Resources.getSystem().getDrawable(R.drawable.tag_extern);
        map_item.setMarker(newMarker);

    }

    public boolean equals(Object obj) {
        if ( obj.getClass() == Tag.class ){
            Tag o = (Tag) obj;
            return this.id.equals( o.id );
        }
        else return false;
    }

    public static Tag new_tag(final Context ctxt, Long id, Double x, Double y){
        RequestQueue MyRequestQueue = Volley.newRequestQueue(ctxt);
        String url = "https://minethetag.cf/api/tags/new";
        Map<String, Double> params = new HashMap<String, Double>();
        params.put("x_pos", x);
        params.put("y_pos", y);
        JSONObject jsonObj = new JSONObject(params);
        try {
            jsonObj.put("tag_id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest MyStringRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                if (response.toString().contains("OK"))
                {
                    Toast.makeText(ctxt, "Tag donat d'alta", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ctxt, "Error alta Tag", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Log.d("CHECK: ", "MISSATGE = " + error.toString());
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
        MyRequestQueue.add(MyStringRequest);

        return null;
    }







}

