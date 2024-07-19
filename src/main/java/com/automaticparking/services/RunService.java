package com.automaticparking.services;

import com.automaticparking.types.ResponseSuccess;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import response.ResponseApi;

import java.util.concurrent.Executor;

@Service
public class RunService extends ResponseApi {
    private Executor asyncExecutor;
    private boolean running = false;
    private Dotenv dotenv;

    @Autowired
    public RunService(Executor asyncExecutor, Dotenv dotenv) {
        this.asyncExecutor = asyncExecutor;
        this.dotenv = dotenv;
    }

    public ResponseSuccess run() throws BadRequestException {
        if (running == true) {
            throw new BadRequestException("runed");
        }
        System.out.println("starting");
        running = true;
        String version = dotenv.get("VERSION");
        asyncExecutor.execute(() -> {
            RestTemplate restTemplate = new RestTemplate();
            while (running) {
                try {
                    restTemplate.getForEntity("https://autoparking-be-" + version + ".onrender.com/start/hello", String.class);
                    restTemplate.getForEntity("https://autoparking-readplate-" + version + ".onrender.com", String.class);
                    restTemplate.getForEntity("https://autoparking-bot-" + version + ".onrender.com/bot/hello.html", String.class);
                    restTemplate.getForEntity("https://autoparking-" + version + ".onrender.com//helloworld", String.class);
                    System.out.println("done");
                    long time = 120000;
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        System.out.println("loi sleep");
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    running = false;
                    break;
                }
            }
        });
        return new ResponseSuccess("loading");
    }

    public ResponseSuccess stop() throws BadRequestException {
        if (running == false) {
            throw new BadRequestException("not run yet");
        }

        running = false;
        return new ResponseSuccess("stop load ok");
    }
}
