package com.automaticparking.model.cash.staff;

import com.automaticparking.model.cash.Cash;
import com.automaticparking.model.cash.staff.dto.ApproveDto;
import com.automaticparking.model.staff.Staff;
import com.automaticparking.types.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import util.Genarate;
import response.ResponseApi;

import java.util.List;
import java.util.Map;

@Service
public class CashStaffService extends ResponseApi {
    private CashStaffRepository cashStaffRepository;

    @Autowired
    public CashStaffService(CashStaffRepository cashStaffRepository) {
        this.cashStaffRepository = cashStaffRepository;
    }

    ResponseEntity<?> getAllCashNotApprove() {
        try {
            List<Cash> cashs = cashStaffRepository.getAllCashNotApprove();
            ResponseSuccess<List<Cash>> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = cashs;
            return ResponseEntity.status(HttpStatus.OK).body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    ResponseEntity<?> approveCash(ApproveDto approve, HttpServletRequest request) {
        try {

            if (!approve.listIdCash.getClass().isArray()) {
                return badRequestApi("id not array");
            }
            Staff staffDataToken = (Staff) request.getAttribute("staffDataToken");

            Integer countUpdated = cashStaffRepository.approveListCash(approve.listIdCash, Genarate.getTimeStamp(), staffDataToken.getSid());

            if (countUpdated != approve.listIdCash.length) {
                return badRequestApi("Update failed " + countUpdated + "/" + approve.listIdCash.length);
            }

            ResponseSuccess<Integer> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = countUpdated;
            return ResponseEntity.status(HttpStatus.OK).body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

}
