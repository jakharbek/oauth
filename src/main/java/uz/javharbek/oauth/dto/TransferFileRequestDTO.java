package uz.javharbek.oauth.dto;

import lombok.Data;

@Data
public class TransferFileRequestDTO {
    String fileLocal;
    String fileRemote;
}
