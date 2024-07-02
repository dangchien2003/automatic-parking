package com.automaticparking.model.code.customer;

import com.automaticparking.model.code.customer.dto.BuyCodeDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("customer/code")
public class CodeController {
    private CodeService codeService;

    @Autowired
    public CodeController(CodeService codeService) {
        this.codeService = codeService;
    }

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
}
