package uz.javharbek.oauth.dto.sms;

import lombok.Data;

@Data
public class SmsRequestDTO {
    Messages messages;

    public static SmsRequestDTO create(String recipient, long messageId, int originator, String text) {
        Content content = new Content();
        content.setText(text);

        Sms sms = new Sms();
        sms.setContent(content);
        sms.setOriginator(originator);

        Messages messages = new Messages();
        messages.setMessageId(messageId);
        messages.setRecipient(recipient);
        messages.setSms(sms);

        SmsRequestDTO smsRequestDTO = new SmsRequestDTO();
        smsRequestDTO.setMessages(messages);

        return smsRequestDTO;
    }
}
