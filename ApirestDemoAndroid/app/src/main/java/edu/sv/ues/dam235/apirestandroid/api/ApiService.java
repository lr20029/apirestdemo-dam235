package edu.sv.ues.dam235.apirestandroid.api;

import java.util.List;

import edu.sv.ues.dam235.apirestandroid.model.LoginRequest;
import edu.sv.ues.dam235.apirestandroid.model.LoginResponse;
import edu.sv.ues.dam235.apirestandroid.model.Product;
import edu.sv.ues.dam235.apirestandroid.model.UserRequest;
import edu.sv.ues.dam235.apirestandroid.model.UserResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/logout")
    Call<Void> logout(@Header("Authorization") String bearerToken);

    @POST("users/register")
    Call<UserResponse> register(@Body UserRequest request);

    @GET("products")
    Call<List<Product>> getProducts(@Header("Authorization") String bearerToken);

    @GET("products/{code}")
    Call<Product> getProduct(@Header("Authorization") String bearerToken,
                             @Path("code") int code);

    @POST("products")
    Call<Product> createProduct(@Header("Authorization") String bearerToken,
                                @Body Product product);

    @PUT("products/{code}")
    Call<Product> updateProduct(@Header("Authorization") String bearerToken,
                                @Path("code") int code,
                                @Body Product product);

    @DELETE("products/{code}")
    Call<Void> deleteProduct(@Header("Authorization") String bearerToken,
                             @Path("code") int code);
}
