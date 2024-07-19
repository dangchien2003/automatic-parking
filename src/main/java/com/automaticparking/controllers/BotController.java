package com.automaticparking.controllers;


import com.automaticparking.services.BotService;
import javassist.NotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import response.ResponseApi;

import java.sql.SQLException;

@RestController
@RequestMapping("api/bot")
public class BotController extends ResponseApi {
    private BotService botService;

    @Autowired
    public BotController(BotService botService) {
        this.botService = botService;
    }

    @PostMapping("checkin")
    public ResponseEntity<?> checkin(@RequestPart("image") MultipartFile file,
                                     @RequestPart(value = "width", required = false) String width,
                                     @RequestPart(value = "height", required = false) String height,
                                     @RequestPart(value = "qr") String qr,
                                     @RequestPart(value = "bot") String bot) {
        try {
            return ResponseEntity.ok(botService.checkin(file, width, height, qr, bot));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NotFoundException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Can not action");
        } catch (Exception e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    @PostMapping("checkout")
    public ResponseEntity<?> checkout(@RequestPart("image") MultipartFile file,
                                      @RequestPart(value = "width", required = false) String width,
                                      @RequestPart(value = "height", required = false) String height,
                                      @RequestPart(value = "qr") String qr,
                                      @RequestPart(value = "bot") String bot) {
        try {
            return ResponseEntity.ok(botService.checkout(file, width, height, qr, bot));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NotFoundException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Can not action");
        } catch (Exception e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
