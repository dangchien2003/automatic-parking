package com.automaticparking.model.bot;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/bot")
public class BotController {
    private BotService botService;

    @Autowired
    public BotController(BotService botService) {
        this.botService = botService;
    }

    @PostMapping("checkin")
    public ResponseEntity<?> uploadFileBase64(@RequestPart("image") MultipartFile file,
                                              @RequestPart(value = "width", required = false) String width,
                                              @RequestPart(value = "height", required = false) String height,
                                              @RequestPart(value = "qr") String qr) {
        return botService.uploadFileBase64(file, width, height, qr);
    }
}
