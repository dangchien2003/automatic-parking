package com.automaticparking.model.run;

import com.automaticparking.types.ResponseSuccess;
import org.apache.coyote.BadRequestException;
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

    public ResponseSuccess run() throws BadRequestException {
        if (running == true) {
            throw new BadRequestException("runed");
        }
        System.out.println("starting");
        running = true;
        asyncExecutor.execute(() -> {
            RestTemplate restTemplate = new RestTemplate();
            while (running) {
                try {
                    restTemplate.getForEntity("https://automatic-parking-x4o1.onrender.com/start/hello", String.class);
                    restTemplate.getForEntity("https://read-plate-parking.onrender.com", String.class);
                    restTemplate.getForEntity("https://parking-bot.onrender.com/bot/hello.html", String.class);
                    restTemplate.getForEntity("https://fe-parking-u1g6.onrender.com/helloworld", String.class);
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
