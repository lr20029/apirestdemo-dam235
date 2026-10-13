package edu.sv.ues.dam235.apirestandroid.api;

import edu.sv.ues.dam235.apirestandroid.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    /*
     * URL base de la API. El valor se define en gradle.properties (propiedad BASE_URL)
     * y queda disponible en BuildConfig.BASE_URL.
     *
     * Emulador: 10.0.2.2 apunta al localhost de la PC.
     * Dispositivo fisico: usar la IP de la PC en la red local, ej.
     *   http://192.168.1.50:8085/api/v1/demoapirestdam235/
     */
    public static final String BASE_URL = BuildConfig.BASE_URL;

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
