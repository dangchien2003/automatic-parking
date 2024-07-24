package com.automaticparking.services;


import com.automaticparking.Repositorys.QRShopRepository;
import com.automaticparking.types.ResponseSuccess;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class CQrShopService {
    private QRShopRepository qrShopRepository;

    public ResponseEntity<ResponseSuccess> getAllCodeOK() {
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(new ResponseSuccess(qrShopRepository.findAllByHide(0), status), status);
    }
}
