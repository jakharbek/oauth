package uz.javharbek.oauth.dto;

import lombok.Data;

@Data
public class FidoIdentityCustomerRequestDTO {
    Integer agreement = 1;
    String series;
    String number;
    String dateBirth;

    public static String pinflToDateBirth(String pinfl) {
        String day = (pinfl.substring(1, 3));
        String month = (pinfl.substring(3, 5));
        int year = Integer.parseInt(pinfl.substring(5, 7));
        if (year == 20) {
            year = Integer.parseInt("20" + Integer.toString(year));
        } else {
            year = Integer.parseInt("19" + Integer.toString(year));
        }
        return (day) + "." + (month) + "." + Integer.toString(year);
    }
}
