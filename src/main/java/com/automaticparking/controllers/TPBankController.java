package com.automaticparking.controllers;

import com.automaticparking.services.TPBankService;
import exception.ConflictException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import response.ResponseApi;

import javax.security.sasl.AuthenticationException;

@RestController
@RequestMapping("tpbank")
public class TPBankController extends ResponseApi {
    private TPBankService tpBankService;

    @Autowired
    public TPBankController(TPBankService tpBankService) {
        this.tpBankService = tpBankService;
    }

    @GetMapping("run")
    ResponseEntity<?> autoApprove(@RequestParam(name = "author", required = true) String author) {
        try {
            return ResponseEntity.ok(tpBankService.autoTpbank(author));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ConflictException e) {
            return error(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthenticationException e) {
            return error(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    @GetMapping("stop")
    ResponseEntity<?> stopAuto(@RequestParam(name = "author", required = true) String author) {
        try {
            return ResponseEntity.ok(tpBankService.stopTpbank(author));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ConflictException e) {
            return error(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthenticationException e) {
            return error(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }
}
