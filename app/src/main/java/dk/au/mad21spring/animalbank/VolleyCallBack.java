package dk.au.mad21spring.animalbank;

import org.json.JSONObject;

public interface VolleyCallBack {
    void onSuccess(JSONObject response);
    void onError();
}
