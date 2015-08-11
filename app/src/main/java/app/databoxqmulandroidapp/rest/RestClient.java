package app.databoxqmulandroidapp.rest;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by MateusFelipe on 17/06/2015.
 */
public class RestClient {

    private static RetrofitInterface REST_CLIENT;
    private static String ROOT;

    private RestClient() {}

    public static RetrofitInterface get( String URL ) {
        ROOT = URL;
        setupRestClient();
        return REST_CLIENT;
    }

    private static void setupRestClient() {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(RestAdapter.LogLevel.FULL);

        RestAdapter restAdapter = builder.build();
        REST_CLIENT = restAdapter.create(RetrofitInterface.class);
    }
}
