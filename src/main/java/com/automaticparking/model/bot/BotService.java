package com.automaticparking.model.bot;

import com.automaticparking.model.cloudinary.CloudinaryService;
import com.automaticparking.model.code.customer.Code;
import com.automaticparking.model.code.customer.CodeRepository;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import response.ResponseApi;
import util.CustomDotENV;
import util.Genarate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Service
public class BotService extends ResponseApi {
    private final CloudinaryService cloudinaryService;
    private final Executor asyncExecutor;

    private CodeRepository codeRepository;

    @Autowired
    public BotService(CloudinaryService cloudinaryService, Executor asyncExecutor, CodeRepository codeRepository) {
        this.cloudinaryService = cloudinaryService;
        this.asyncExecutor = asyncExecutor;
        this.codeRepository = codeRepository;
    }


    public ResponseEntity<?> checkin(MultipartFile file, String width, String height, String qr) {
        try {
            if (qr.trim().equals("") || qr.equals("0")) {
                return badRequestApi("Invalid qr");
            }

            Code code = codeRepository.getInfo(qr);

            if (code == null) {
                return Error(HttpStatus.NOT_FOUND, "Qr not exist");
            }

            if (code.getCancleAt() != 0 || code.getExpireAt() < Genarate.getTimeStamp() || code.getCheckoutAt() != 0 || code.getCheckinAt() != 0) {
                return badRequestApi("Invalid Code");
            }

            Map<String, String> dataReadPlate = callReadPlate(file, width, height);

            if (Integer.parseInt(dataReadPlate.get("status")) == 200) {
                String plate = dataReadPlate.get("plate");

                List<Code> list = codeRepository.getInfoByPlate(plate);
                boolean inParking = false;
                for (Code item : list) {
                    if (item.getCancleAt() == 0 && item.getCheckinAt() != 0 && item.getCheckoutAt() == 0) {
                        inParking = true;
                    }
                }

                if (inParking) {
                    return badRequestApi("The vehicle is in the parking lot");
                }

                asyncExecutor.execute(() -> {
                    long now = Genarate.getTimeStamp();

                    Map<String, String> options = new HashMap<>();
                    options.put("folder", "parking/plate");
                    options.put("public_id", code.getQrid() + "_" + now + "_in");
                    try {
                        Map<String, String> uploadResult = cloudinaryService.uploadFile(file, options);
                        String url = uploadResult.get("secure_url");
                        String[] splitUrl = url.split("/");
                        String[] cutSplitUrl = Arrays.copyOfRange(splitUrl, splitUrl.length - 4, splitUrl.length);
                        String path = String.join("/", cutSplitUrl);

                        code.setImageIn(path);
                        code.setCheckinAt(now);
                        code.setPlate(plate);

                        boolean updated = codeRepository.updateCode(code);

                        if (!updated) {
                            throw new Exception("Error update");
                        } else {
                            System.out.println("ok");
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
            }

            return ResponseEntity.ok(dataReadPlate);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }


    public ResponseEntity<?> checkout(MultipartFile file, String width, String height, String qr) {
        try {
            if (qr.trim().equals("") || qr.equals("0")) {
                return badRequestApi("Invalid qr");
            }

            Code code = codeRepository.getInfo(qr);

            if (code == null) {
                return Error(HttpStatus.NOT_FOUND, "Qr not exist");
            }

            if (code.getCancleAt() != 0 || code.getExpireAt() < Genarate.getTimeStamp() || code.getCheckoutAt() != 0 || code.getCheckinAt() == 0) {
                return badRequestApi("Invalid Code");
            }

            Map<String, String> dataReadPlate = callReadPlate(file, width, height);

            if (Integer.parseInt(dataReadPlate.get("status")) == 200) {
                String plate = dataReadPlate.get("plate");
                double similarity = similarityPlate(code.getPlate(), plate);
                if (similarity < 90) {
                    return badRequestApi("Different plate " + plate + "/" + code.getPlate());
                }

                asyncExecutor.execute(() -> {
                    long now = Genarate.getTimeStamp();

                    Map<String, String> options = new HashMap<>();
                    options.put("folder", "parking/plate");
                    options.put("public_id", code.getQrid() + "_" + now + "_out");
                    try {
                        Map<String, String> uploadResult = cloudinaryService.uploadFile(file, options);
                        String url = uploadResult.get("secure_url");
                        String[] splitUrl = url.split("/");
                        String[] cutSplitUrl = Arrays.copyOfRange(splitUrl, splitUrl.length - 4, splitUrl.length);
                        String path = String.join("/", cutSplitUrl);

                        code.setImageOut(path);
                        code.setCheckoutAt(now);

                        boolean updated = codeRepository.updateCode(code);

                        if (!updated) {
                            throw new Exception("Error update");
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
            }

            return ResponseEntity.ok(dataReadPlate);
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

    private Map<String, String> callReadPlate(MultipartFile file, String width, String height) {
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

        return dataRes;
    }


    private double similarityPlate(String plate1, String plate2) {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        int distance = levenshteinDistance.apply(plate1, plate2);
        int maxLength = Math.max(plate1.length(), plate2.length());
        return ((double) (maxLength - distance) / maxLength) * 100;
    }
}
