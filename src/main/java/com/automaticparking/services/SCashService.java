package com.automaticparking.services;

import com.automaticparking.Repositorys.CashRepository;
import com.automaticparking.database.dto.ApproveDto;
import com.automaticparking.database.entity.Cash;
import com.automaticparking.database.entity.Staff;
import com.automaticparking.exception.BadRequestException;
import com.automaticparking.exception.InvalidException;
import com.automaticparking.types.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.automaticparking.util.Generate;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class SCashService {
    private CashRepository cashRepository;

    public ResponseEntity<ResponseSuccess> getAllCashNotApprove() {
        List<Cash> cashs = cashRepository.findAllCashNotApprove();
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(cashs, status), status);
    }

    public ResponseEntity<ResponseSuccess> approveCash(ApproveDto approve, HttpServletRequest request) {
        if (!approve.getListIdCash().getClass().isArray()) {
            throw new InvalidException("id not array");
        }
        Staff staffDataToken = (Staff) request.getAttribute("staffDataToken");

        Integer countUpdated = cashRepository.approveListCash(Generate.getTimeStamp(), staffDataToken.getSid(), Arrays.stream(approve.getListIdCash()).toList());

        if (countUpdated != approve.getListIdCash().length) {
            throw new BadRequestException("Update failed " + countUpdated + "/" + approve.getListIdCash().length);
        }
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(countUpdated, status), status);
    }

}
