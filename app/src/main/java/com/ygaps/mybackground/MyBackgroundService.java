package com.ygaps.mybackground;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by tpl on 6/20/17.
 */

public interface MyBackgroundService {
    @GET("/yourbackground.php")
    Call<MBGResponse> getX(@Query("deviceID") String deviceID,
                           @Query("curDate") String curDate,
                           @Query("isDate") int isDate);
}
