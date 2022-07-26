package uz.javharbek.oauth.dto;

import lombok.Data;

@Data
public class FidoIdentityCustomerResponseRequestBodyDTO {
    String physicalState;
    String inn;
    String pnfl;
    String firstName;
    String lastName;
    String middleName;
    String firstNameInter;
    String lastNameInter;
    String middleNameInter;
    String birthDate;
    String birthPlace;
    String birthCountry;
    String gender;
    String series;
    String number;
    String docIssueDate;
    String docExpireDate;
    String docIssuePlace;
    String nationalityId;
    String nationalityName;
    String citizenship;
    String residenceCountry;
    String residenceRegion;
    String residenceDistrict;
    String residenceAddress;
}
