package com.automaticparking.services;

import com.automaticparking.Repositorys.BotRepository;
import com.automaticparking.Repositorys.CodeRepository;
import com.automaticparking.database.entity.Bot;
import com.automaticparking.database.entity.Code;
import com.automaticparking.exception.AuthorizedException;
import com.automaticparking.exception.ConflictException;
import com.automaticparking.exception.InvalidException;
import com.automaticparking.exception.NotFoundException;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.automaticparking.util.Generate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Service
@AllArgsConstructor
public class BotService {
    private final CloudinaryService cloudinaryService;
    private final Executor asyncExecutor;
    private CodeRepository codeRepository;
    private CacheService cacheService;
    private BotRepository botRepository;
    private Dotenv dotenv;

    public ResponseEntity<Map<String, String>> checkin(MultipartFile file, String width, String height, String qr, String id) {

        if (qr.trim().equals("") || qr.equals("0")) {
            throw new InvalidException("Invalid qr");
        }

        Bot bot = getBot(id);
        if (bot == null) {
            throw new NotFoundException("Not found bot");
        }

        Integer dataCache = cacheService.getCache("plate_" + qr);
        if (dataCache != null) {
            if (dataCache > 0) {
                throw new ConflictException("QR is processing");
            }
        }

        Code code = codeRepository.findById(qr).orElseThrow(() -> new NotFoundException());

        if (code == null) {
            throw new NotFoundException("Qr not exist");
        }

        if (code.getCancleAt() != 0 || code.getExpireAt() < Generate.getTimeStamp() || code.getCheckoutAt() != 0 || code.getCheckinAt() != 0) {
            throw new AuthorizedException("Invalid Code");
        }

        Map<String, String> dataReadPlate = callReadPlate(file, width, height);

        if (Integer.parseInt(dataReadPlate.get("status")) == 200) {
            String plate = dataReadPlate.get("plate");

            List<Code> list = codeRepository.findAllByPlate(plate);
            boolean inParking = false;
            for (Code item : list) {
                if (item.getCancleAt() == 0 && item.getCheckinAt() != 0 && item.getCheckoutAt() == 0) {
                    inParking = true;
                }
            }

            if (inParking) {
                throw new ConflictException("The vehicle is in the parking lot");
            }

            asyncExecutor.execute(() -> {
                cacheService.setCache("plate_" + qr, 1);
                long now = Generate.getTimeStamp();

                Map<String, String> options = new HashMap<>();
                options.put("folder", "parking/plate");
                options.put("public_id", code.getQrid() + "_" + now + "_in");
                try {
                    String path = "";
                    try {
                        Map<String, String> uploadResult = cloudinaryService.uploadFile(file, options);
                        String url = uploadResult.get("secure_url");
                        String[] splitUrl = url.split("/");
                        String[] cutSplitUrl = Arrays.copyOfRange(splitUrl, splitUrl.length - 4, splitUrl.length);
                        path = String.join("/", cutSplitUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(now + " _ " + plate);
                    }

                    code.setImageIn(path);
                    code.setCheckinAt(now);
                    code.setPlate(plate);
                    code.setExpireAt(now + 86400 * 1000);
                    code.setBotId(bot.getId());

                    codeRepository.save(code);

                    cacheService.setCache("plate_" + qr, -1);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
        }

        return new ResponseEntity<>(dataReadPlate, HttpStatus.OK);
    }


    public ResponseEntity<Map<String, String>> checkout(MultipartFile file, String width, String height, String qr, String botId) {
        if (qr.trim().equals("") || qr.equals("0")) {
            throw new InvalidException("Invalid qr");
        }

        Bot bot = getBot(botId);
        if (bot == null) {
            throw new NotFoundException("Not found bot");
        }

        Integer dataCache = cacheService.getCache("plate_" + qr);
        if (dataCache != null) {
            if (dataCache > 0) {
                throw new ConflictException("QR is processing");
            }
        }

        Code code = codeRepository.findById(qr).orElseThrow(() -> new NotFoundException());

        if (code == null) {
            throw new NotFoundException("Qr not exist");
        }

        if (code.getCancleAt() != 0 || code.getExpireAt() < Generate.getTimeStamp() || code.getCheckoutAt() != 0 || code.getCheckinAt() == 0) {
            throw new AuthorizedException("Invalid Code");
        }

        Map<String, String> dataReadPlate = callReadPlate(file, width, height);

        if (Integer.parseInt(dataReadPlate.get("status")) == 200) {
            String plate = dataReadPlate.get("plate");
            double similarity = similarityPlate(code.getPlate(), plate);
            if (similarity < 85) {
                throw new ConflictException("Different plate " + plate + "/" + code.getPlate());
            }

            asyncExecutor.execute(() -> {
                cacheService.setCache("plate_" + qr, 1);
                long now = Generate.getTimeStamp();

                Map<String, String> options = new HashMap<>();
                options.put("folder", "parking/plate");
                options.put("public_id", code.getQrid() + "_" + now + "_out");
                try {
                    String path = "";
                    try {
                        Map<String, String> uploadResult = cloudinaryService.uploadFile(file, options);
                        String url = uploadResult.get("secure_url");
                        String[] splitUrl = url.split("/");
                        String[] cutSplitUrl = Arrays.copyOfRange(splitUrl, splitUrl.length - 4, splitUrl.length);
                        path = String.join("/", cutSplitUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(now + " _ " + plate);
                    }

                    code.setImageOut(path);
                    code.setCheckoutAt(now);

                    codeRepository.save(code);

                    cacheService.setCache("plate_" + qr, -1);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
        }

        return new ResponseEntity<>(dataReadPlate, HttpStatus.OK);
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
        ResponseEntity<String> response = restTemplate.exchange(dotenv.get("HOST_READ_PLATE") + "/read-text", HttpMethod.POST, requestEntity, String.class);

        Map<String, String> dataRes = Generate.getMapFromJson(response.getBody());

        return dataRes;
    }


    private double similarityPlate(String plate1, String plate2) {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        int distance = levenshteinDistance.apply(plate1, plate2);
        int maxLength = Math.max(plate1.length(), plate2.length());
        return ((double) (maxLength - distance) / maxLength) * 100;
    }

    private Bot getBot(String id) {
        String key = "bot_cache_" + id;
        Bot bot = cacheService.getCache(key);
        if (bot == null) {
            bot = botRepository.findById(id).orElse(null);
            cacheService.setCache(key, bot);
        }
        return bot;
    }
}
