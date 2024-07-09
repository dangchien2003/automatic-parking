package com.automaticparking.model.run;

import com.automaticparking.types.ResponseSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import response.ResponseApi;

import java.util.concurrent.Executor;

@Service
public class RunService extends ResponseApi {
    private Executor asyncExecutor;
    private boolean running = false;

    @Autowired
    public RunService(Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    public ResponseEntity<?> run() {
        if (running == true) {
            return badRequestApi("runed");
        }
        System.out.println("starting");
        running = true;
        asyncExecutor.execute(() -> {
            RestTemplate restTemplate = new RestTemplate();
            while (running) {
                restTemplate.getForEntity("https://automatic-parking.onrender.com/start/hello", String.class);
                restTemplate.getForEntity("https://doc-bien-so.onrender.com", String.class);
                restTemplate.getForEntity("https://bot-app-3wke.onrender.com/bot/hello.html", String.class);
                restTemplate.getForEntity("https://fe-parking.onrender.com/helloworld", String.class);
                System.out.println("done");
                long time = 120000;
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    System.out.println("loi sleep");
                }
            }
        });
        ResponseSuccess<String> responseSuccess = new ResponseSuccess<>();
        responseSuccess.data = "ok";
        return ResponseEntity.ok().body(responseSuccess);
    }

    public ResponseEntity<?> stop() {
        if (running == false) {
            return badRequestApi("not run yet");
        }

        running = false;
        ResponseSuccess<String> responseSuccess = new ResponseSuccess<>();
        responseSuccess.data = "stop ok";
        return ResponseEntity.ok().body(responseSuccess);
    }
}
