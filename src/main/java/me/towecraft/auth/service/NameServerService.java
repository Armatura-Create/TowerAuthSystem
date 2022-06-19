package me.towecraft.auth.service;

import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.wrapper.Wrapper;
import unsave.plugin.context.annotations.Service;

@Service
public class NameServerService {

    public String getNameServer() {
        ServiceInfoSnapshot currentServiceInfoSnapshot = Wrapper.getInstance().getCurrentServiceInfoSnapshot();
        return currentServiceInfoSnapshot.getName();
    }

}
