package app.databoxqmulandroidapp.rest;


import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by MateusFelipe on 16/06/2015.
 */
public interface RetrofitInterface {

    // Asynchronously
    @Headers({
            "X-Mashape-Key : PUT YOUT MASHAPE KEY HERE",
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: application/json"
    })
    @POST("/text/")
    @FormUrlEncoded
    void getSentiment(@Field("txt")String  tweetText, Callback<JsonElement> callback);
}
