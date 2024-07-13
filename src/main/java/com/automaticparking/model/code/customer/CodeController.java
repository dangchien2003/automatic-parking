package com.automaticparking.model.code.customer;

import com.automaticparking.model.code.customer.dto.BuyCodeDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import javassist.NotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import response.ResponseApi;

import javax.validation.Valid;
import java.sql.SQLException;

@RestController
@RequestMapping("customer/code")
public class CodeController extends ResponseApi {
    private CodeService codeService;

    @Autowired
    public CodeController(CodeService codeService) {
        this.codeService = codeService;
    }

    @PostMapping("buy")
    ResponseEntity<?> buyCode(@Valid @RequestBody BuyCodeDto buyCode, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(codeService.buyCode(buyCode, request));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể thực hiện");
        } catch (Exception e) {
            return serverError();
        }
    }

    @GetMapping("bought")
    ResponseEntity<?> getBoughtCode(HttpServletRequest request, @RequestParam(required = false) String quantity) {
        try {
            return ResponseEntity.ok(codeService.getBoughtCode(request, quantity));
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể thực hiện");
        } catch (Exception e) {
            return serverError();
        }
    }

    @GetMapping("i")
    ResponseEntity<?> getInfoCode(@RequestParam("qrid") String qrid, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(codeService.getInfoCode(qrid, request));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NotFoundException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể thực hiện");
        } catch (Exception e) {
            return serverError();
        }
    }

    @GetMapping("qr/{qrid}")
    ResponseEntity<?> getContenQr(@PathVariable String qrid, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(codeService.getContenQr(qrid, request));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NotFoundException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể thực hiện");
        } catch (Exception e) {
            return serverError();
        }
    }

    @GetMapping("extend/price/{qrid}")
    ResponseEntity<?> priceExtend(@RequestParam(name = "date", required = true) String date, @RequestParam(name = "time", required = true) int indexTime, @PathVariable String qrid, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(codeService.calcPriceExtendCode(qrid, date, indexTime, request));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NotFoundException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể thực hiện");
        } catch (Exception e) {
            return serverError();
        }
    }

    @PatchMapping("extend/{qrid}")
    ResponseEntity<?> extend(@RequestParam(name = "date", required = true) String date, @RequestParam(name = "time", required = true) int indexTime, @PathVariable String qrid, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(codeService.extendCode(qrid, date, indexTime, request));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể thực hiện");
        } catch (NotFoundException e) {
            return error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return serverError();
        }
    }

    @PatchMapping("cancle")
    ResponseEntity<?> extend(@RequestParam(name = "id", required = true) String qrid, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(codeService.cancleCode(qrid, request));
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể thực hiện");
        } catch (Exception e) {
            return serverError();
        }
    }
}
