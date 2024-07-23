package com.automaticparking.services;

import com.automaticparking.Repositorys.StaffRepository;
import com.automaticparking.database.dto.ChangePasswordDto;
import com.automaticparking.database.dto.CreateStaffDto;
import com.automaticparking.database.dto.LoginDto;
import com.automaticparking.database.dto.UpdateStaffDto;
import com.automaticparking.database.entity.Staff;
import com.automaticparking.exception.AuthorizedException;
import com.automaticparking.exception.BadRequestException;
import com.automaticparking.exception.ConflictException;
import com.automaticparking.exception.NotFoundException;
import com.automaticparking.types.ResponseSuccess;
import encrypt.Hash;
import encrypt.JWT;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import util.Generate;
import validation.DateValid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@AllArgsConstructor
public class StaffService {
    private StaffRepository staffRepository;
    private Dotenv dotenv;

    public ResponseEntity<ResponseSuccess> createAdmin(CreateStaffDto createStaff) {
        DateValid dateValid = new DateValid();
        if (!dateValid.isValidDate(createStaff.getBirthday())) {
            throw new BadRequestException("Birthday non-compliant birthday format 'yyyy-MM-dd'");
        }

        Staff staffs = staffRepository.findByEmail(createStaff.getEmail()).orElseThrow(() -> new RuntimeException("Not found"));
        ;
        if (staffs != null) throw new ConflictException("Email already exists");

        int countAdmin = staffRepository.countByAdmin(1);
        if (countAdmin > 0) {
            throw new BadRequestException("Admin already exists");
        }
        String password = new Hash().hash(dotenv.get("PASSWORD_STAFF"));
        Staff staff = new Staff(Generate.generateId("STAFF_", 3), 1, createStaff.getEmail(), password, createStaff.getName(), Generate.getTimeStamp(), createStaff.getBirthday(), 0, null);


        staffRepository.save(staff);
        HttpStatus status = HttpStatus.CREATED;
        return new ResponseEntity<>(new ResponseSuccess(staff, status), status);
    }

    public ResponseEntity<ResponseSuccess> login(LoginDto loginDto, HttpServletResponse response) {
        // check length email
        if (loginDto.getEmail().trim().length() < 10) {
            throw new BadRequestException("Email must not be less than 8 characters");
        }

        // check length password
        if (loginDto.getPassword().trim().length() < 8) {
            throw new BadRequestException("Password must not be less than 8 characters");
        }

        Staff staff = staffRepository.findByEmail(loginDto.getEmail()).orElseThrow(() -> new RuntimeException("Not found"));

        if (staff == null) {
            throw new AuthorizedException("Account or password is incorrect");
        }
        Hash hash = new Hash();


        if (!hash.compareHash(loginDto.getPassword(), staff.getPassword())) {
            throw new BadRequestException("Incorrect password");
        }

        if (staff.getBlock() == 1) {
            throw new AuthorizedException("Account access has been restricted");
        }


        JWT<Staff> jwt = new JWT<>();
        String stoken = jwt.createJWT(staff, Long.parseLong(dotenv.get("TIME_SECOND_TOKEN")));

        Map<String, String> cookies = new HashMap<>();
        cookies.put("SToken", stoken);

        Cookie cookie = new Cookie("SToken", stoken);
        cookie.setAttribute("Path", "/staff");
        cookie.setAttribute("HttpOnly", "True");
        cookie.setAttribute("SameSite", "None");
        cookie.setAttribute("Partitioned", "True");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(null, staff, status), status);
    }

    public ResponseEntity<ResponseSuccess> createStaff(CreateStaffDto createStaff, HttpServletRequest request) {
        DateValid dateValid = new DateValid();
        if (!dateValid.isValidDate(createStaff.getBirthday())) {
            throw new BadRequestException("Birthday non-compliant birthday format 'yyyy-MM-dd'");
        }

        Staff staff = staffRepository.findByEmail(createStaff.getEmail()).orElseThrow(() -> new RuntimeException("Not found"));
        ;
        if (staff != null) throw new ConflictException("Email already exists");

        // tạo staff
        String password = new Hash().hash(dotenv.get("PASSWORD_STAFF"));
        Staff dataStaff = new Staff(Generate.generateId("STAFF_", 3), 0, createStaff.getEmail(), password, createStaff.getName(), Generate.getTimeStamp(), createStaff.getBirthday(), 0, null);
        staffRepository.save(dataStaff);
        HttpStatus status = HttpStatus.CREATED;
        return new ResponseEntity<>(new ResponseSuccess(staff, status), status);
    }

    public ResponseEntity<ResponseSuccess> getAllStaff(HttpServletRequest request) {
        List<Staff> staffs = staffRepository.findByAdminNot(1);
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(staffs, status), status);
    }

    public ResponseEntity<ResponseSuccess> lockStaff(String sid, HttpServletRequest request) {
        Staff staffDataToken = (Staff) request.getAttribute("staffDataToken");

        if (sid.length() < 20) {
            throw new BadRequestException("Sid is not long enough");
        }

        Staff staff = staffRepository.findById(sid).orElseThrow(() -> new RuntimeException("Not found"));

        if (staff == null) {
            throw new NotFoundException("Sid not exist");
        }

        if (staff.getSid().equals(staffDataToken.getSid()) || staff.getAdmin() == 1) {
            throw new BadRequestException("Not block your self");
        }

        if (staff.getBlock() == 1) {
            throw new ConflictException("The account has been locked before");
        }

        // set block
        staff.setBlock(1);
        staff.setLastLogin(Generate.getTimeStamp());

        staffRepository.save(staff);

        Map<String, String> dataResponse = new HashMap<>();
        dataResponse.put("sid", staff.getSid());
        dataResponse.put("email", staff.getEmail());
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(dataResponse, status), status);
    }

    public ResponseEntity<ResponseSuccess> unLockStaff(String sid, HttpServletRequest request) {
        Staff staffDataToken = (Staff) request.getAttribute("staffDataToken");

        if (sid.length() < 20) {
            throw new BadRequestException("Sid is not long enough");
        }

        Staff staff = staffRepository.findById(sid).orElseThrow(() -> new RuntimeException("Not found"));

        if (staff == null) {
            throw new NotFoundException("Sid not exist");
        }

        if (staff.getSid().equals(staffDataToken.getSid()) || staff.getAdmin() == 1) {
            throw new BadRequestException("Cannot unlock your self");
        }

        if (staff.getBlock() == 0) {
            throw new ConflictException("The account has been unlocked before");
        }

        // set block
        staff.setBlock(0);
        staff.setLastLogin(Generate.getTimeStamp());

        Staff staffLastUpdate = staffRepository.save(staff);
        if (!staffLastUpdate.equals(staff)) {
            throw new RuntimeException("Error update");
        }

        Map<String, String> dataResponse = new HashMap<>();
        dataResponse.put("sid", staff.getSid());
        dataResponse.put("email", staff.getEmail());
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(dataResponse, status), status);
    }

    public ResponseEntity<ResponseSuccess> changePassword(ChangePasswordDto changePassword, HttpServletRequest request) {
        if (!changePassword.getConfirmPassword().equals(changePassword.getNewPassword())) {
            throw new BadRequestException("Confirm password and new password must be the same");
        }

        if (changePassword.getOldPassword().equals(changePassword.getNewPassword())) {
            throw new BadRequestException("Old password and new password cannot be the same");
        }

        Staff staffDataToken = (Staff) request.getAttribute("staffDataToken");
        String sid = staffDataToken.getSid();
        Staff staff = staffRepository.findById(sid).orElseThrow(() -> new RuntimeException("Not found"));

        Hash hash = new Hash();
        if (!hash.compareHash(changePassword.getOldPassword(), staff.getPassword())) {
            throw new BadRequestException("Incorrect password");
        }

        String hashNewPassword = hash.hash(changePassword.getNewPassword());

        staff.setPassword(hashNewPassword);
        staff.setLastLogin(Generate.getTimeStamp());

        staffRepository.save(staff);
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(status), status);
    }

    public ResponseEntity<ResponseSuccess> updateStaff(UpdateStaffDto updateStaff, String sid) {
        if (sid.length() < 20) {
            throw new BadRequestException("Sid is not long enough");
        }

        DateValid dateValid = new DateValid();
        if (!dateValid.isValidDate(updateStaff.birthday)) {
            throw new BadRequestException("Non-compliant birthday format 'yyyy-MM-dd'");
        }

        List<Staff> staffs = staffRepository.findAllBySidOrEmail(sid, updateStaff.email);

        if (staffs.size() >= 2) {
            throw new ConflictException("Email already exists");
        }

        if (staffs.isEmpty() || !staffs.get(0).getSid().equals(sid)) {
            throw new NotFoundException("Not found");
        }

        if (staffs.get(0).getEmail().equals(updateStaff.email) && staffs.get(0).getBirthday().equals(updateStaff.birthday) && staffs.get(0).getName().equals(updateStaff.name)) {
            throw new BadRequestException("The information has not changed");
        }

        Staff dataUpdate = staffs.get(0);

        dataUpdate.setName(updateStaff.name);
        dataUpdate.setEmail(updateStaff.email);
        dataUpdate.setBirthday(updateStaff.birthday);
        dataUpdate.setLastLogin(Generate.getTimeStamp());

        Staff staffLastUpdate = staffRepository.save(dataUpdate);
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(staffLastUpdate, status), status);
    }

}
