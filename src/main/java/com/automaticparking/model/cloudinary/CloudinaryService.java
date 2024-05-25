package com.automaticparking.model.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map uploadFile(MultipartFile file, Map<String, String> option) throws IOException {
        byte[] bytes = file.getBytes();
        return cloudinary.uploader().upload(bytes, option);
    }


    public Map deleteFile(String publicId) throws IOException {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
