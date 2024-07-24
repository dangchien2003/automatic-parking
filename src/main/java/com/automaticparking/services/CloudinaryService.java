package com.automaticparking.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@AllArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public Map uploadFile(MultipartFile file, Map<String, String> option) throws IOException {
        byte[] bytes = file.getBytes();
        return cloudinary.uploader().upload(bytes, option);
    }


    public Map deleteFile(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
