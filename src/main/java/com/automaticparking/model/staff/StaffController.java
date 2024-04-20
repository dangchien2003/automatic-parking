package com.automaticparking.model.staff;

import com.automaticparking.model.cash.Cash;
import com.automaticparking.model.staff.dto.CreateStaffDto;
import javax.validation.Valid;

import com.automaticparking.model.staff.dto.LoginDto;
import com.automaticparking.types.ResponseSuccess;
import encrypt.JWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import util.Genarate;
import util.ResponseApi;
import validation.DateValid;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("staff")
public class StaffController extends ResponseApi {
    private final StaffService staffService = new StaffService();
    @PostMapping("create-admin")
    ResponseEntity<?> createAdmin(@Valid @RequestBody CreateStaffDto createStaff)  {
        try {
            DateValid dateValid = new DateValid();
            if(!dateValid.isValidDate(createStaff.birthday)) {
                return badRequestApi("Birthday", "Non-compliant birthday format 'yyyy-MM-dd'");
            }

            StaffService staffService = new StaffService();

            Staff staffs = staffService.getOneStaffByEmail(createStaff.getEmail());
            if(staffs != null) return Error(HttpStatus.CONFLICT,  "Email đã tồn tại");

            BigInteger countAdmins = staffService.countAdmin();
            int countAdminsInteger = countAdmins.intValue();
            if(countAdminsInteger > 0) {
                return badRequestApi("Admin already exists");
            }

            Staff staff = Util.getDefaultStaff();
            staff = Util.setStaff(staff, 1, createStaff.name, createStaff.birthday, createStaff.email);

            Boolean created = staffService.createStaff(staff);

            if(!created) {
                return internalServerError("Cannot create staff");
            }
            ResponseSuccess<Staff> responseSuccess = new ResponseSuccess<Staff>();
            responseSuccess.data = staff;
            return ResponseEntity.status(HttpStatus.CREATED).body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @PostMapping("login")
    ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto){
        try {
            // check length email
            if(loginDto.email.trim().length() < 10) {
                return badRequestApi("email", "Email must not be less than 8 characters");
            }

            // check length password
            if(loginDto.password.trim().length() < 8) {
                return badRequestApi("password", "Password must not be less than 8 characters");
            }

            Staff staff = staffService.getOneStaffByEmail(loginDto.email);

            if(staff == null) {
                return Error(HttpStatus.UNAUTHORIZED, "Account or password is incorrect");
            }

            if(staff.getBlock() == 1) {
                return Error(HttpStatus.UNAUTHORIZED, "Account access has been restricted");
            }

            JWT<Staff> jwt = new JWT<>();
            String stoken = jwt.createJWT(staff);

            ResponseSuccess<Staff> response = new ResponseSuccess<>();

            Map<String, String> cookies = new HashMap<>();
            cookies.put("stoken", stoken);
            response.cookies = cookies;
            response.data = staff;
            return ResponseEntity.ok().body(response);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @PostMapping("create")
    ResponseEntity<?> createStaff(@Valid @RequestBody CreateStaffDto createStaff, HttpServletRequest request) {
        try {
            Map<String, String> staffDataToken = (Map<String, String>) request.getAttribute("staffDataToken");

            // Kiểm tra admin
            if(!staffDataToken.get("admin").equals("1")) {
                return Error(HttpStatus.UNAUTHORIZED, "Not have access");
            }

            DateValid dateValid = new DateValid();
            if(!dateValid.isValidDate(createStaff.birthday)) {
                return badRequestApi("Birthday", "Non-compliant birthday format 'yyyy-MM-dd'");
            }

            StaffService staffService = new StaffService();

            Staff staff = staffService.getOneStaffByEmail(createStaff.getEmail());
            if(staff != null) return Error(HttpStatus.CONFLICT,  "Email đã tồn tại");

            // tạo staff
            Staff dataStaff = Util.getDefaultStaff();
            staff = Util.setStaff(dataStaff, 0, createStaff.name, createStaff.birthday, createStaff.email);

            Boolean created = staffService.createStaff(dataStaff);

            if(!created) {
                return internalServerError("Cannot create staff");
            }
            ResponseSuccess<Staff> responseSuccess = new ResponseSuccess<Staff>();
            responseSuccess.data = staff;
            return ResponseEntity.status(HttpStatus.CREATED).body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @GetMapping("get-all")
    ResponseEntity<?> getAllStaff(HttpServletRequest request) {
        try {
            Map<String, String> staffDataToken = (Map<String, String>) request.getAttribute("staffDataToken");

            // Kiểm tra admin
            if(!staffDataToken.get("admin").equals("1")) {
                return Error(HttpStatus.UNAUTHORIZED, "Not have access");
            }

            List<Staff> staffs = staffService.getAllStaff();

            ResponseSuccess<List<Staff>> responseSuccess = new ResponseSuccess<List<Staff>>();
            responseSuccess.data = staffs;
            return ResponseEntity.status(HttpStatus.OK).body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @PatchMapping("lock/{sid}")
    ResponseEntity<?> lockStaff(@PathVariable String sid, HttpServletRequest request) {
        try {
            Map<String, String> staffDataToken = (Map<String, String>) request.getAttribute("staffDataToken");

            // Kiểm tra admin
            if(!staffDataToken.get("admin").equals("1")) {
                return Error(HttpStatus.UNAUTHORIZED, "Not have access");
            }

            if(sid.length() < 20) {
                return badRequestApi("sid", "Sid is not long enough");
            }

            Staff staff = staffService.getOneStaffBySid(sid);

            if(staff == null) {{
                return Error(HttpStatus.NOT_FOUND, "Sid not exist");
            }}
            if(staff.getSid().equals(staffDataToken.get("sid")) || staff.getAdmin() == 1) {
                return Error(HttpStatus.BAD_REQUEST, "Not block your self");
            }

            if(staff.getBlock() == 1) {
                return Error(HttpStatus.CONFLICT, "The account has been locked before");
            }

            // set block
            staff.setBlock(1);
            staff.setLastLogin(Genarate.getTimeStamp());

            Boolean updated = staffService.updateStaff(staff);
            if(!updated) {
                return Error(HttpStatus.BAD_REQUEST, "Cannot block account. Please checking sid");
            }

            Map<String, String> dataResponse = new HashMap<>();
            dataResponse.put("sid", staff.getSid());
            dataResponse.put("email", staff.getEmail());

            ResponseSuccess<Map<String, String>> responseSuccess = new ResponseSuccess<Map<String, String>>();
            responseSuccess.data = dataResponse;
            return ResponseEntity.status(HttpStatus.OK).body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @PatchMapping("unlock/{sid}")
    ResponseEntity<?> UnLockStaff(@PathVariable String sid, HttpServletRequest request) {
        try {
            Map<String, String> staffDataToken = (Map<String, String>) request.getAttribute("staffDataToken");

            // Kiểm tra admin
            if(!staffDataToken.get("admin").equals("1")) {
                return Error(HttpStatus.UNAUTHORIZED, "Not have access");
            }

            if(sid.length() < 20) {
                return badRequestApi("sid", "Sid is not long enough");
            }

            Staff staff = staffService.getOneStaffBySid(sid);

            if(staff == null) {{
                return Error(HttpStatus.NOT_FOUND, "Sid not exist");
            }}

            if(staff.getSid().equals(staffDataToken.get("sid")) || staff.getAdmin() == 1) {
                return Error(HttpStatus.BAD_REQUEST, "Cannot unlock your self");
            }

            if(staff.getBlock() == 0) {
                return Error(HttpStatus.CONFLICT, "The account has been unlocked before");
            }

            // set block
            staff.setBlock(0);
            staff.setLastLogin(Genarate.getTimeStamp());

            Boolean updated = staffService.updateStaff(staff);
            if(!updated) {
                return Error(HttpStatus.BAD_REQUEST, "Cannot unlock account. Please checking sid");
            }

            Map<String, String> dataResponse = new HashMap<>();
            dataResponse.put("sid", staff.getSid());
            dataResponse.put("email", staff.getEmail());

            ResponseSuccess<Map<String, String>> responseSuccess = new ResponseSuccess<Map<String, String>>();
            responseSuccess.data = dataResponse;
            return ResponseEntity.status(HttpStatus.OK).body(responseSuccess);
        }catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

}
