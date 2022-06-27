package me.towecraft.auth.service.retrofit;

import me.towecraft.auth.service.retrofit.dto.PlayerDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitApi {
    @POST("/api/v1/player/")
    Call<PlayerDTO> getPlayerByUuid(@Body PlayerDTO body);

    @POST("/api/v1/player/name")
    Call<PlayerDTO> getPlayerByName(@Body PlayerDTO body);

    @POST("/api/v1/player/email")
    Call<Boolean> getPlayerByEmail(@Body PlayerDTO body);

    @POST("/api/v1/player/add")
    Call<Boolean> savePlayer(@Body PlayerDTO body);

    @POST("/api/v1/player/changePassword")
    Call<Boolean> changePassword(@Body PlayerDTO body);
}
