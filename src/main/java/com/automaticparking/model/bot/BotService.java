package com.automaticparking.model.bot;

import com.automaticparking.model.cloudinary.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import response.ResponseApi;
import util.CustomDotENV;
import util.Genarate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

@Service
public class BotService extends ResponseApi {
    private final CloudinaryService cloudinaryService;
    private final Executor asyncExecutor;

    @Autowired
    public BotService(CloudinaryService cloudinaryService, Executor asyncExecutor) {
        this.cloudinaryService = cloudinaryService;
        this.asyncExecutor = asyncExecutor;
    }
    
    public ResponseEntity<?> uploadFileBase64(MultipartFile file, String width, String height, String qr) {
        try {

            // check qr


            //

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", file.getResource());
            body.add("width", width);
            body.add("height", height);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Gửi yêu cầu POST đến server Python
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(CustomDotENV.get("HOST_READ_PLATE") + "/read-text", HttpMethod.POST, requestEntity, String.class);

            Map<String, String> dataRes = Genarate.getMapFromJson(response.getBody());

            if (Integer.parseInt(dataRes.get("status")) == 200) {
                asyncExecutor.execute(() -> {
                    Map<String, String> options = new HashMap<>();
                    options.put("folder", "parking/plate");
                    try {
                        Map<String, String> uploadResult = cloudinaryService.uploadFile(file, options);
                        String url = uploadResult.get("secure_url");
                        String[] splitUrl = url.split("/");
                        String[] cutSplitUrl = Arrays.copyOfRange(splitUrl, splitUrl.length - 4, splitUrl.length);
                        String path = String.join("/", cutSplitUrl);
                        System.out.println(path);

                        // lưu vào data base


                        //

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        System.out.println("Lỗi upload");
                    }
                });
            }

            return ResponseEntity.ok(dataRes);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }
}
