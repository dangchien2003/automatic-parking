package com.automaticparking.model.cash.staff;

import com.automaticparking.model.cash.Cash;
import com.automaticparking.model.cash.staff.dto.ApproveDto;
import com.automaticparking.model.staff.Staff;
import com.automaticparking.types.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import util.Genarate;
import response.ResponseApi;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class CashStaffService extends ResponseApi {
    private CashStaffRepository cashStaffRepository;

    @Autowired
    public CashStaffService(CashStaffRepository cashStaffRepository) {
        this.cashStaffRepository = cashStaffRepository;
    }

    ResponseSuccess getAllCashNotApprove() throws SQLException {
        List<Cash> cashs = cashStaffRepository.getAllCashNotApprove();
        return new ResponseSuccess(cashs);
    }

    ResponseSuccess approveCash(ApproveDto approve, HttpServletRequest request) throws BadRequestException, SQLException {
        if (!approve.listIdCash.getClass().isArray()) {
            throw new BadRequestException("id not array");
        }
        Staff staffDataToken = (Staff) request.getAttribute("staffDataToken");

        Integer countUpdated = cashStaffRepository.approveListCash(approve.listIdCash, Genarate.getTimeStamp(), staffDataToken.getSid());

        if (countUpdated != approve.listIdCash.length) {
            throw new BadRequestException("Update failed " + countUpdated + "/" + approve.listIdCash.length);
        }
        return new ResponseSuccess(countUpdated);
    }

}
