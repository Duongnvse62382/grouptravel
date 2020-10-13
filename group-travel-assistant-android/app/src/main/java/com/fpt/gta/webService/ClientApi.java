package com.fpt.gta.webService;


import com.fpt.gta.repository.GTAService;

public class ClientApi extends BaseApi {
    public GTAService gtaService()
    {
        return this.getService(GTAService.class, ConfigAPI.BASE_URL);
    }
}
