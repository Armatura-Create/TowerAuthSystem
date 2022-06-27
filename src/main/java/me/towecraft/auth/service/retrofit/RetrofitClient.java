package me.towecraft.auth.service.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.towecraft.auth.TAS;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
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

            Gson gson = new GsonBuilder().create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            api = retrofit.create(RetrofitApi.class);
        }
    }
}
