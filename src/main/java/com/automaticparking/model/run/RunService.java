package com.automaticparking.model.run;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;

@Service
public class RunService {
    private Executor asyncExecutor;

    @Autowired
    public RunService(Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    @Async
    public void run() {
        System.out.println("acs");
        asyncExecutor.execute(() -> {
            while (true) {
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response = null;
                response = restTemplate.getForEntity("https://automatic-parking.onrender.com/start/hello", String.class);
                response = restTemplate.getForEntity("https://doc-bien-so.onrender.com", String.class);
                response = restTemplate.getForEntity("https://bot-app-3wke.onrender.com/bot/hello.html", String.class);
                response = restTemplate.getForEntity("https://fe-parking.onrender.com/helloworld", String.class);
                System.out.println("done");
                long time = 120000;
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    System.out.println("loi sleep");
                    break;
                }
            }
        });
    }
}
