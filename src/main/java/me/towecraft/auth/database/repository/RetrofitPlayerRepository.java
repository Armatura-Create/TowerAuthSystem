package me.towecraft.auth.database.repository;

import lombok.RequiredArgsConstructor;
import me.towecraft.auth.database.entity.PlayerEntity;
import me.towecraft.auth.service.retrofit.RetrofitClient;
import me.towecraft.auth.service.retrofit.dto.PlayerDTO;
import me.towecraft.auth.service.retrofit.mapper.PlayerMapper;
import me.towecraft.auth.utils.PluginLogger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class RetrofitPlayerRepository implements PlayerRepository {

    private final RetrofitClient retrofitClient;
    private final PlayerMapper playerMapper;

    private final PluginLogger logger;

    @Override
    public void findByUuid(UUID uuid, RepositoryCallback<Optional<PlayerEntity>> callback) {
        retrofitClient.api.getPlayerByUuid(new PlayerDTO().setUuid(uuid.toString())).enqueue(new Callback<PlayerDTO>() {
            @Override
            public void onResponse(Call<PlayerDTO> call, Response<PlayerDTO> response) {
                if (response.isSuccessful()) {
                    if (callback != null) {
                        callback.callback(Optional.of(playerMapper.toEntity(response.body())));
                    }
                } else {
                    if (callback != null) {
                        callback.callback(Optional.empty());
                    }
                }
            }

            @Override
            public void onFailure(Call<PlayerDTO> call, Throwable throwable) {
                if (callback != null) {
                    callback.callback(Optional.empty());
                }
            }
        });
    }

    @Override
    public void findByUsername(String username, RepositoryCallback<Optional<PlayerEntity>> callback) {
        retrofitClient.api.getPlayerByName(new PlayerDTO().setName(username)).enqueue(new Callback<PlayerDTO>() {
            @Override
            public void onResponse(Call<PlayerDTO> call, Response<PlayerDTO> response) {
                if (response.isSuccessful()) {
                    if (callback != null) {
                        callback.callback(Optional.of(playerMapper.toEntity(response.body())));
                    }
                } else {
                    if (callback != null) {
                        callback.callback(Optional.empty());
                    }
                }
            }

            @Override
            public void onFailure(Call<PlayerDTO> call, Throwable throwable) {
                if (callback != null) {
                    callback.callback(Optional.empty());
                }
            }
        });
    }

    @Override
    public void save(PlayerEntity player, RepositoryCallback<Boolean> callback) {
        retrofitClient.api.savePlayer(playerMapper.toDTO(player)).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    if (callback != null) {
                        callback.callback(true);
                    }
                } else {
                    if (callback != null) {
                        callback.callback(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                if (callback != null) {
                    callback.callback(false);
                }
            }
        });
    }

    @Override
    public void savePassword(PlayerEntity player) {
        retrofitClient.api.savePlayer(playerMapper.toDTO(player)).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (!response.isSuccessful() || Boolean.FALSE.equals(response.body()))
                    logger.log(response.message());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void findByEmail(String email, RepositoryCallback<Boolean> callback) {
        retrofitClient.api.getPlayerByEmail(new PlayerDTO().setEmail(email)).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body()) {
                    if (callback != null) {
                        callback.callback(true);
                    }
                } else {
                    if (callback != null) {
                        callback.callback(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                if (callback != null) {
                    callback.callback(false);
                }

                throwable.printStackTrace();
            }
        });
    }
}
