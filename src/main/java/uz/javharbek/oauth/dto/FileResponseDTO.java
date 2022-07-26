package uz.javharbek.oauth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileResponseDTO {
    Long id;
    String title;
    String description;
    Long size;
    String file;
    String extension;
    int status;
    LocalDateTime createdDatetime;
    LocalDateTime updatedDatetime;
    LocalDateTime deletedDatetime;
    Boolean isDeleted = false;
    String host;
    String absoluteUrl;
}
