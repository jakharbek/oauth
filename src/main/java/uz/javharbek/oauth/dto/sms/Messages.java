package uz.javharbek.oauth.dto.sms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
class Messages {
    public String recipient;
    @JsonProperty("message-id")
    public long messageId;
    public Sms sms;
}
