package me.towecraft.auth.service.retrofit;

import me.towecraft.auth.TAS;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.PostConstruct;
import unsave.plugin.context.annotations.Service;

@Service
public class RetrofitClient {

    @Autowire
    private TAS plugin;
    public RetrofitApi api;

    @PostConstruct
    private void init() {
        if (plugin.getConfig().getBoolean("Retrofit.enable", false)) {

            String url = plugin.getConfig().getString("Retrofit.baseUrl");

            if (url == null)
                throw new RuntimeException(new Exception("Not found url [Retrofit.baseUrl] in config.yml"));

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();

            api = retrofit.create(RetrofitApi.class);
        }
    }
}
