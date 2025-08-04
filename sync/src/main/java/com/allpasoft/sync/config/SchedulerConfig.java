package com.allpasoft.sync.config;

import com.allpasoft.sync.services.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig
{
    @Autowired
    private SyncService syncService;

    @Scheduled(fixedRate = 10000)
    public void ejectSync(){
        syncService.synchronizeClocks();
        System.out.println("Ejecutando Sync");
    }
}
