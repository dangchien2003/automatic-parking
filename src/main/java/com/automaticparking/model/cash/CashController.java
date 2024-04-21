package com.automaticparking.model.cash;

import com.automaticparking.model.cash.dto.ApproveDto;
import com.automaticparking.types.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import util.Genarate;
import util.ResponseApi;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("cash")
public class CashController extends ResponseApi {
    private final CashService cashService = new CashService();
    @GetMapping("not-approve/get-all")
    ResponseEntity<?> getAllCash() {
        try {
            List<Cash> cashs = cashService.getAllCashNotApprove();
            ResponseSuccess<List<Cash>> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = cashs;
            return ResponseEntity.status(HttpStatus.OK).body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @PatchMapping("approve")
    ResponseEntity<?> getAllCash(@Valid @RequestBody ApproveDto approve, HttpServletRequest request) {
        try {

            if(!approve.listIdCash.getClass().isArray()) {
                return badRequestApi("id not array");
            }
            Map<String, String> staffDataToken = (Map<String, String>) request.getAttribute("staffDataToken");

            Integer countUpdated = cashService.approveListCash(approve.listIdCash, Genarate.getTimeStamp(), staffDataToken.get("sid"));

            if(countUpdated != approve.listIdCash.length) {
                return badRequestApi("Update failed " + countUpdated + "/" + approve.listIdCash.length);
            }

            ResponseSuccess<Integer> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = countUpdated;
            return ResponseEntity.status(HttpStatus.OK).body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

}
