package com.automaticparking.controllers;

import com.automaticparking.services.CBotService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import response.ResponseApi;

import java.sql.SQLException;

@RestController
@RequestMapping("customer/bot")
public class CBotController extends ResponseApi {
    private CBotService cBotService;

    @Autowired
    public CBotController(CBotService cBotService) {
        this.cBotService = cBotService;
    }

    @GetMapping("i")
    ResponseEntity<?> getInfoBot(@RequestParam(required = false, name = "bot") String id) {
        try {
            return ResponseEntity.ok(cBotService.geInfoBot(id));
        } catch (NotFoundException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Can not action");
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }
}
