package edu.sv.ues.dam235.apirestandroid.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    /*
     * URL base para el EMULADOR de Android.
     * 10.0.2.2 es la forma en que el emulador ve al "localhost" de la PC.
     *
     * Si se usa un DISPOSITIVO FÍSICO, reemplazar por la IP de la PC en la red
     * local, por ejemplo: http://192.168.1.50:8085/api/v1/demoapirestdam235/
     */
    public static final String BASE_URL =
            "http://10.0.2.2:8085/api/v1/demoapirestdam235/";

    private static Retrofit retrofit;

    private ApiClient() {
    }

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApi() {
        return getRetrofit().create(ApiService.class);
    }
}
