package com.automaticparking.services;

import com.automaticparking.database.entity.Cash;
import com.automaticparking.repositorys.SCashRepository;
import com.automaticparking.database.dto.ApproveDto;
import com.automaticparking.database.entity.Staff;
import com.automaticparking.types.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import util.Generate;
import response.ResponseApi;

import java.sql.SQLException;
import java.util.List;

@Service
public class SCashService extends ResponseApi {
    private SCashRepository cashStaffRepository;

    @Autowired
    public SCashService(SCashRepository cashStaffRepository) {
        this.cashStaffRepository = cashStaffRepository;
    }

    public ResponseSuccess getAllCashNotApprove() throws SQLException {
        List<Cash> cashs = cashStaffRepository.getAllCashNotApprove();
        return new ResponseSuccess(cashs);
    }

    public ResponseSuccess approveCash(ApproveDto approve, HttpServletRequest request) throws BadRequestException, SQLException {
        if (!approve.listIdCash.getClass().isArray()) {
            throw new BadRequestException("id not array");
        }
        Staff staffDataToken = (Staff) request.getAttribute("staffDataToken");

        Integer countUpdated = cashStaffRepository.approveListCash(approve.listIdCash, Generate.getTimeStamp(), staffDataToken.getSid());

        if (countUpdated != approve.listIdCash.length) {
            throw new BadRequestException("Update failed " + countUpdated + "/" + approve.listIdCash.length);
        }
        return new ResponseSuccess(countUpdated);
    }

}
