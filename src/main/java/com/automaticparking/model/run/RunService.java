package com.automaticparking.model.run;

import com.automaticparking.types.ResponseSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.concurrent.Executor;

@Service
public class RunService {
    private Executor asyncExecutor;

    @Autowired
    public RunService(Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }
    
    public ResponseEntity<?> run() {

        File file = new File("run.txt");
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            // Kiểm tra nếu file không tồn tại hoặc file trống
            if (!file.exists() || file.length() == 0) {
                writer = new BufferedWriter(new FileWriter(file));
                writer.write("running");
                System.out.println("starting...");
            } else {
                System.out.println("runed");
                ResponseSuccess<String> responseSuccess = new ResponseSuccess<>();
                responseSuccess.data = "runed";
                return ResponseEntity.ok().body(responseSuccess);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                ResponseSuccess<String> responseSuccess = new ResponseSuccess<>();
                responseSuccess.data = ex.getMessage();
                return ResponseEntity.ok().body(responseSuccess);
            }
        }

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
        ResponseSuccess<String> responseSuccess = new ResponseSuccess<>();
        responseSuccess.data = "ok";
        return ResponseEntity.ok().body(responseSuccess);
    }
}
