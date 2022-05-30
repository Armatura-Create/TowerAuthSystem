package me.towecraft.utils.models.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.ArrayList;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServersData {

    private List<ServerModel> lobbies;
    private List<ServerModel> servers;

    public void clearAll() {
        servers.clear();
        lobbies.clear();
    }

    public void setLobbies(List<ServerModel> lobbies) {
        this.lobbies = lobbies;
    }

    public void setServers(List<ServerModel> servers) {
        this.servers = servers;
    }

    public List<ServerModel> getLobbies() {
        return lobbies;
    }

    public  List<ServerModel> getServers() {
        return servers;
    }
}
