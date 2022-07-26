package uz.javharbek.oauth.dto.sms;

import lombok.Data;

@Data
public class Sms {
    int originator;
    Content content;
}
