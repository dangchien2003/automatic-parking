package com.automaticparking.model.staff;

import com.automaticparking.model.staff.dto.CreateStaffDto;
import javax.validation.Valid;

import com.automaticparking.types.ResponseException;
import com.automaticparking.types.ResponseSuccess;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import util.ResponseApi;
import validation.DateValid;

import java.math.BigInteger;
import java.util.List;


@RestController
@RequestMapping("staff")
public class StaffController extends ResponseApi {
    @PostMapping("create-admin")
    ResponseEntity<?> createAdmin(@Valid @RequestBody CreateStaffDto createStaff)  {
        try {
            DateValid dateValid = new DateValid();
            if(!dateValid.isValidDate(createStaff.birthday)) {
                return badRequestApi("Birthday", "Non-compliant birthday format 'yyyy-MM-dd'");
            }

            StaffService staffService = new StaffService();

            List<Staff> staffs = staffService.getStaffByEmail(createStaff.getEmail());
            if(!staffs.isEmpty()) return Error(HttpStatus.CONFLICT,  "Email đã tồn tại");

            BigInteger countAdmins = staffService.countAdmin();
            int countAdminsInteger = countAdmins.intValue();
            if(countAdminsInteger > 0) {
                return badRequestApi("Admin already exists");
            }

            Staff staff = Util.getDefaultStaff();
            staff = Util.setStaff(staff, 1, "Admin", createStaff.birthday, createStaff.email);

            Boolean created = staffService.createStaff(staff);

            if(!created) {
                return internalServerError("Cannot create staff");
            }
            ResponseSuccess<Staff> responseSuccess = new ResponseSuccess<Staff>();
            responseSuccess.data = staff;
            return ResponseEntity.status(HttpStatus.CREATED).body(responseSuccess);
        }catch (ResponseException e) {
            return internalServerError(e.getMessage());
        }
    }

}
