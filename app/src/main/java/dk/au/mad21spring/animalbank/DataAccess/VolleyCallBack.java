package dk.au.mad21spring.animalbank.DataAccess;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface VolleyCallBack {
    void onSuccess(JSONObject response);

    void onError(VolleyError error);
}
