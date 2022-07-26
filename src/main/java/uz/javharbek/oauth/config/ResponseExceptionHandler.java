package uz.javharbek.oauth.config;

import uz.javharbek.oauth.dto.ApplicationResponse;
import uz.javharbek.oauth.dto.ErrorDTO;
import uz.javharbek.oauth.exceptions.AppException;
import uz.javharbek.oauth.exceptions.UserExistException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<ApplicationResponse> handleConflict(RuntimeException e) {
        return new ResponseEntity<>(new ApplicationResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AppException.class})
    protected ResponseEntity<ApplicationResponse> handleConflict(AppException e) {
        return new ResponseEntity<>(new ApplicationResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {UserExistException.class})
    protected ResponseEntity<ApplicationResponse> UserExistException(UserExistException e) {
        return new ResponseEntity<>(new ApplicationResponse(false, e.getMessage()), HttpStatus.GONE);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());

        //Get all errors
        List<ErrorDTO> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> (new ErrorDTO(x.getField(),x.getDefaultMessage())))
                .collect(Collectors.toList());

        body.put("errors", errors);

        return new ResponseEntity<>(body, headers, status);

    }

}
