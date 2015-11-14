package com.example.kelok_000.recruit.server;


import com.example.kelok_000.recruit.utils.Caller;
import com.example.kelok_000.recruit.utils.CallerAdapterFactory;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by someguy233 on 08-Nov-15.
 */
public class CirclesServer {

    public static class UserModel {
        public String email;
        public String firstname;
        public String lastname;
        public String profile_picture_url;
    }

    public static class TokenModel {
        public String access_token;
        public int expires_in;
        public String token_type;
        public String scope;
        public String refresh_token;
    }

    public interface ApiV1 {

        @FormUrlEncoded
        @POST("token.php")
        Caller<TokenModel> getTokenUsingPassword(
                @Field("grant_type") String grantType,
                @Field("username") String username,
                @Field("password") String password,
                @Field("client_id") String clientId,
                @Field("client_secret") String clientSecret,
                @Field("scope") String scope
        );


        @FormUrlEncoded
        @POST("create-guest.php")
        Caller<UserModel> createGuest(
                @Field("firstname") String firstname,
                @Field("lastname") String lastname,
                @Field("password") String password
        );


    }

    public static final ApiV1 api;

    static {
        // Initialize server link
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://wemari.com/")
                .addCallAdapterFactory(new CallerAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(ApiV1.class);
    }

}
