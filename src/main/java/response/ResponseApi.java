package response;

import com.automaticparking.types.ResponseError;
import com.automaticparking.types.ResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class ResponseApi {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseError handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map <String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error)-> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseError(errors);
    }


    protected ResponseError resError(Map <String, String> MapErrors) {
        return new ResponseError(MapErrors);
    }

    protected Map<String, String> getMap(String[] arrKey, String[] arrValue) {
        Map <String, String> map = new HashMap<>();
        for (int i = 0; i < arrKey.length; i++){
            map.put(arrKey[i], arrValue[i]);
        }
        return map;
    }

    protected Map<String, String> getMap(String value) {
        Map <String, String> map = new HashMap<>();
            map.put("error", value);
        return map;
    }

    protected ResponseEntity<?> internalServerError(String messageError) {
        Map <String, String> error = getMap(messageError);
        return ResponseEntity.internalServerError().body(resError(error));
    }

    // ERROR 400
    protected ResponseEntity<?> badRequestApi(String[] arrKey, String[] arrValue) {
        if(arrKey.length != arrValue.length) {
            return internalServerError("Length array key not same length array value");
        }
        Map<String, String> errors =  getMap(arrKey, arrValue);
        return ResponseEntity.badRequest().body(resError(errors));
    }

    protected ResponseEntity<?> badRequestApi(String key, String value) {
        String[] arrKey = {key};
        String [] arrValue = {value};
        return badRequestApi(arrKey, arrValue);
    }
    protected ResponseEntity<?> badRequestApi(String value) {
        String[] arrKey = {"error"};
        String [] arrValue = {value};
        return badRequestApi(arrKey, arrValue);
    }

    // error any status
    protected ResponseEntity<?> Error(HttpStatus status, String key, String value) {
        Map<String, String> errors =new HashMap<>();
        errors.put(key, value);

        return ResponseEntity.status(status.value()).body(resError(errors));
    }
    protected ResponseEntity<?> Error(HttpStatus status, String value) {
        Map<String, String> errors =new HashMap<>();
        errors.put("error", value);

        return ResponseEntity.status(status.value()).body(resError(errors));
    }

    @ExceptionHandler(ResponseException.class)
    public ResponseEntity<?> handleInterceptorException(ResponseException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
    }
}
