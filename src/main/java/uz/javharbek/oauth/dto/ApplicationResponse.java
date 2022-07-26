package uz.javharbek.oauth.dto;

import lombok.Data;

import java.util.Collections;

@Data
public class ApplicationResponse {
    private boolean success;
    private String message;
    private Object data;

    public ApplicationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = Collections.emptyMap();
    }

    public ApplicationResponse(Object data) {
        this.data = data;
        this.message = "SUCCESS";
        this.success = true;
    }

    public ApplicationResponse() {
        this.data = Collections.emptyMap();
        this.message = "SUCCESS";
        this.success = true;
    }

    public ApplicationResponse(String message) {
        this.data = Collections.emptyMap();
        this.message = message;
        this.success = false;
    }
}