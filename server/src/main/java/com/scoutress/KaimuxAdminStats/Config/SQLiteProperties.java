package com.scoutress.KaimuxAdminStats.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SQLiteProperties {

    @Value("${sqlite.url.survival}")
    private String survivalUrl;

    @Value("${sqlite.url.skyblock}")
    private String skyblockUrl;

    @Value("${sqlite.url.creative}")
    private String creativeUrl;

    @Value("${sqlite.url.boxpvp}")
    private String boxpvpUrl;

    @Value("${sqlite.url.prison}")
    private String prisonUrl;

    @Value("${sqlite.url.events}")
    private String eventsUrl;

    public String getSurvivalUrl() {
        return survivalUrl;
    }

    public String getSkyblockUrl() {
        return skyblockUrl;
    }

    public String getCreativeUrl() {
        return creativeUrl;
    }

    public String getBoxpvpUrl() {
        return boxpvpUrl;
    }

    public String getPrisonUrl() {
        return prisonUrl;
    }

    public String getEventsUrl() {
        return eventsUrl;
    }
}
