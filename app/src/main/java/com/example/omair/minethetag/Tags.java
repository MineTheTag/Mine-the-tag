package com.example.omair.minethetag;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.omair.minethetag.LoginActivity.TOKEN;
import static com.example.omair.minethetag.LoginActivity.gpassword;
import static com.example.omair.minethetag.LoginActivity.gusername;

/**
 * Created by dpini on 18/05/17.
 */

public class Tags {
    ItemizedIconOverlay<OverlayItem> osm_tags;
    // La idea es utilitzar un set o similar
    HashMap<Long,Tag> tags;
    // Crec que hauríem de fer servir un SingleTon per a la cua de requests de Volley
    Context ctxt;

    Tags(Context ctxt){
        Log.d("TAGS", "Instantiated");
        osm_tags = new ItemizedIconOverlay<OverlayItem>(ctxt, new ArrayList<OverlayItem>(), null);
        tags = new HashMap<>();
        this.ctxt = ctxt;
    }

    public void add(Tag t){
        tags.put(t.id,t);
        osm_tags.addItem(t.map_item);
    }

    public void remove(Long id){
        Tag t = tags.get(id);
        if ( t != null ) {
            tags.remove(id);
            osm_tags.removeItem(t.map_item);
        }
    }

    public void update_tags(){
        RequestQueue MyRequestQueue = Volley.newRequestQueue(ctxt);
        String url = "https://minethetag.cf/api/tags/get";

        JsonObjectRequest MyStringRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                Log.d("Tags get resp", response.toString());
                JSONArray tags_propis = new JSONArray();
                JSONArray tags_aliens = new JSONArray();
                try {
                    tags_propis = response.getJSONArray("tags propis");
                    tags_aliens = response.getJSONArray("tags aliens");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                update_tags_propis(tags_propis);
                update_tags_propis(tags_aliens);


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
    }

    private void update_tags_propis(JSONArray tags){
        for (int i = 0 ; i < tags.length(); ++i ){
            JSONObject obj = null;
            try {
                obj = tags.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Tag t = null;
            try {
                t = new Tag(0l,obj.getDouble("x_pos"),obj.getDouble("y_pos"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if ( t != null ) {
                Log.d("Adding tag", t.toString());
                add(t);
            }
        }
    }

    private void update_tags_aliens(JSONArray tags){

    }

}
