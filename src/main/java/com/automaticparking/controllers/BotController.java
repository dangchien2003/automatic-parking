package com.automaticparking.controllers;


import com.automaticparking.services.BotService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/bot")
@AllArgsConstructor
public class BotController  {
    private BotService botService;


    @PostMapping("checkin")
    public ResponseEntity<?> checkin(@RequestPart("image") MultipartFile file,
                                     @RequestPart(value = "width", required = false) String width,
                                     @RequestPart(value = "height", required = false) String height,
                                     @RequestPart(value = "qr") String qr,
                                     @RequestPart(value = "bot") String bot) {
        return botService.checkin(file, width, height, qr, bot);
    }

    @PostMapping("checkout")
    public ResponseEntity<?> checkout(@RequestPart("image") MultipartFile file,
                                      @RequestPart(value = "width", required = false) String width,
                                      @RequestPart(value = "height", required = false) String height,
                                      @RequestPart(value = "qr") String qr,
                                      @RequestPart(value = "bot") String bot) {
        return botService.checkout(file, width, height, qr, bot);
    }
}
