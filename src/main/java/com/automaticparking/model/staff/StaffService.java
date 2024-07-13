package com.automaticparking.model.staff;

import com.automaticparking.model.staff.dto.ChangePasswordDto;
import com.automaticparking.model.staff.dto.CreateStaffDto;
import com.automaticparking.model.staff.dto.LoginDto;
import com.automaticparking.model.staff.dto.UpdateStaffDto;
import com.automaticparking.types.ResponseSuccess;
import com.fasterxml.jackson.core.JsonProcessingException;
import encrypt.Hash;
import encrypt.JWT;
import exception.ConflictException;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javassist.NotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import util.Genarate;
import response.ResponseApi;
import validation.DateValid;

import javax.naming.AuthenticationException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class StaffService extends ResponseApi {
    private StaffRepository staffRepository;
    private Dotenv dotenv;

    @Autowired
    public StaffService(StaffRepository staffRepository, Dotenv dotenv) {
        this.staffRepository = staffRepository;
        this.dotenv = dotenv;
    }

    ResponseSuccess createAdmin(CreateStaffDto createStaff) throws ConflictException, BadRequestException, SQLException, NoSuchAlgorithmException {
        DateValid dateValid = new DateValid();
        if (!dateValid.isValidDate(createStaff.birthday)) {
            throw new BadRequestException("Birthday non-compliant birthday format 'yyyy-MM-dd'");
        }

        StaffRepository staffRepository = new StaffRepository();

        Staff staffs = staffRepository.getOneStaffByEmail(createStaff.getEmail());
        if (staffs != null) throw new ConflictException("Email already exists");

        BigInteger countAdmins = staffRepository.countAdmin();
        int countAdminsInteger = countAdmins.intValue();
        if (countAdminsInteger > 0) {
            throw new BadRequestException("Admin already exists");
        }

        Staff staff = Util.getDefaultStaff();
        staff = Util.setStaff(staff, 1, createStaff.name, createStaff.birthday, createStaff.email);

        staffRepository.createStaff(staff);

        return new ResponseSuccess(staff);
    }

    ResponseSuccess login(LoginDto loginDto, HttpServletResponse response) throws BadRequestException, AuthenticationException, SQLException, NoSuchAlgorithmException, JsonProcessingException {
        // check length email
        if (loginDto.email.trim().length() < 10) {
            throw new BadRequestException("Email must not be less than 8 characters");
        }

        // check length password
        if (loginDto.password.trim().length() < 8) {
            throw new BadRequestException("Password must not be less than 8 characters");
        }

        Staff staff = staffRepository.getOneStaffByEmail(loginDto.email);

        if (staff == null) {
            throw new AuthenticationException("Account or password is incorrect");
        }
        Hash hash = new Hash();

        if (!hash.compareHash(loginDto.password, staff.getPassword())) {
            throw new BadRequestException("Incorrect password");
        }

        if (staff.getBlock() == 1) {
            throw new AuthenticationException("Account access has been restricted");
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
        return new ResponseSuccess(cookies, staff);
    }

    ResponseSuccess createStaff(CreateStaffDto createStaff, HttpServletRequest request) throws BadRequestException, SQLException, NoSuchAlgorithmException {
        DateValid dateValid = new DateValid();
        if (!dateValid.isValidDate(createStaff.birthday)) {
            throw new BadRequestException("Birthday non-compliant birthday format 'yyyy-MM-dd'");
        }

        StaffRepository staffRepository = new StaffRepository();

        Staff staff = staffRepository.getOneStaffByEmail(createStaff.getEmail());
        if (staff != null) throw new ConflictException("Email already exists");

        // tạo staff
        Staff dataStaff = Util.getDefaultStaff();
        staff = Util.setStaff(dataStaff, 0, createStaff.name, createStaff.birthday, createStaff.email);

        staffRepository.createStaff(dataStaff);
        return new ResponseSuccess(staff);
    }

    ResponseSuccess getAllStaff(HttpServletRequest request) throws SQLException {
        List<Staff> staffs = staffRepository.getAllStaff();
        return new ResponseSuccess(staffs);
    }

    ResponseSuccess lockStaff(String sid, HttpServletRequest request) throws NotFoundException, BadRequestException, SQLException {
        Staff staffDataToken = (Staff) request.getAttribute("staffDataToken");

        if (sid.length() < 20) {
            throw new BadRequestException("Sid is not long enough");
        }

        Staff staff = staffRepository.getOneStaffBySid(sid);

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
        staff.setLastLogin(Genarate.getTimeStamp());

        staffRepository.updateStaff(staff);

        Map<String, String> dataResponse = new HashMap<>();
        dataResponse.put("sid", staff.getSid());
        dataResponse.put("email", staff.getEmail());

        return new ResponseSuccess(dataResponse);
    }

    ResponseSuccess unLockStaff(String sid, HttpServletRequest request) throws BadRequestException, NotFoundException, SQLException {
        Staff staffDataToken = (Staff) request.getAttribute("staffDataToken");

        if (sid.length() < 20) {
            throw new BadRequestException("Sid is not long enough");
        }

        Staff staff = staffRepository.getOneStaffBySid(sid);

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
        staff.setLastLogin(Genarate.getTimeStamp());

        Boolean updated = staffRepository.updateStaff(staff);
        if (!updated) {
            throw new BadRequestException("Cannot unlock account. Please checking sid");
        }

        Map<String, String> dataResponse = new HashMap<>();
        dataResponse.put("sid", staff.getSid());
        dataResponse.put("email", staff.getEmail());
        return new ResponseSuccess(dataResponse);
    }

    ResponseSuccess changePassword(ChangePasswordDto changePassword, HttpServletRequest request) throws BadRequestException, NoSuchAlgorithmException, SQLException {
        if (!changePassword.confirmPassword.equals(changePassword.newPassword)) {
            throw new BadRequestException("Confirm password and new password must be the same");
        }

        if (changePassword.oldPassword.equals(changePassword.newPassword)) {
            throw new BadRequestException("Old password and new password cannot be the same");
        }

        Staff staffDataToken = (Staff) request.getAttribute("staffDataToken");
        String sid = staffDataToken.getSid();
        Staff staff = staffRepository.getOneStaffBySid(sid);
        Hash hash = new Hash();
        if (!hash.compareHash(changePassword.oldPassword, staff.getPassword())) {
            throw new BadRequestException("Incorrect password");
        }

        String hashNewPassword = hash.hash(changePassword.newPassword);

        staff.setPassword(hashNewPassword);
        staff.setLastLogin(Genarate.getTimeStamp());

        staffRepository.updateStaff(staff);

        return new ResponseSuccess();
    }

    ResponseSuccess updateStaff(UpdateStaffDto updateStaff, String sid) throws BadRequestException, SQLException, NotFoundException {
        if (sid.length() < 20) {
            throw new BadRequestException("Sid is not long enough");
        }

        DateValid dateValid = new DateValid();
        if (!dateValid.isValidDate(updateStaff.birthday)) {
            throw new BadRequestException("Non-compliant birthday format 'yyyy-MM-dd'");
        }

        List<Staff> staffs = staffRepository.getListStaffByEmailAndSid(sid, updateStaff.email);

        if (staffs.size() > 2) {
            throw new ConflictException("An unknown error. Please contact us");
        }

        if (staffs.size() == 2) {
            throw new ConflictException("Email already exists");
        }

        if (staffs.isEmpty() || !staffs.get(0).getSid().equals(sid)) {
            throw new NotFoundException("Not found staff");
        }

        if (staffs.get(0).getEmail().equals(updateStaff.email) && staffs.get(0).getBirthday().equals(updateStaff.birthday) && staffs.get(0).getName().equals(updateStaff.name)) {
            throw new BadRequestException("The information has not changed");
        }

        Staff dataUpdate = staffs.get(0);

        dataUpdate.setName(updateStaff.name);
        dataUpdate.setEmail(updateStaff.email);
        dataUpdate.setBirthday(updateStaff.birthday);
        dataUpdate.setLastLogin(Genarate.getTimeStamp());
        staffRepository.updateStaff(dataUpdate);

        return new ResponseSuccess(dataUpdate);
    }

}
