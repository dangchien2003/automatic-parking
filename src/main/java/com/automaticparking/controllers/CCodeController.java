package com.automaticparking.controllers;

import com.automaticparking.database.dto.BuyCodeDto;
import com.automaticparking.services.CCodeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

@RestController
@RequestMapping("api/customer/code")
@AllArgsConstructor
public class CCodeController  {
    private CCodeService codeService;

    @PostMapping("buy")
    ResponseEntity<?> buyCode(@Valid @RequestBody BuyCodeDto buyCode, HttpServletRequest request) {
        return codeService.buyCode(buyCode, request);
    }

    @GetMapping("bought")
    ResponseEntity<?> getBoughtCode(HttpServletRequest request, @RequestParam(required = false) String quantity) {
        return codeService.getBoughtCode(request, quantity);
    }

    @GetMapping("i")
    ResponseEntity<?> getInfoCode(@RequestParam("qrid") String qrid, HttpServletRequest request) {
        return codeService.getInfoCode(qrid, request);
    }

    @GetMapping("qr/{qrid}")
    ResponseEntity<?> getContenQr(@PathVariable String qrid, HttpServletRequest request) {
        return codeService.getContenQr(qrid, request);
    }

    @GetMapping("extend/price/{qrid}")
    ResponseEntity<?> priceExtend(@RequestParam(name = "date", required = true) String date, @RequestParam(name = "time", required = true) int indexTime, @PathVariable String qrid, HttpServletRequest request) {
        return codeService.calcPriceExtendCode(qrid, date, indexTime, request);
    }

    @PatchMapping("extend/{qrid}")
    ResponseEntity<?> extend(@RequestParam(name = "date", required = true) String date, @RequestParam(name = "time", required = true) int indexTime, @PathVariable String qrid, HttpServletRequest request) {
        return codeService.extendCode(qrid, date, indexTime, request);
    }

    @PatchMapping("cancle")
    ResponseEntity<?> extend(@RequestParam(name = "id", required = true) String qrid, HttpServletRequest request) {
        return codeService.cancleCode(qrid, request);
    }
}
