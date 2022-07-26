package uz.javharbek.oauth.dto;

import com.google.gson.Gson;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
public class CertInfoDTO {
    @NotBlank
    String issuer;
    String notafter;
    String notbefore;
    @NotBlank
    String serialnumber;
    @NotBlank
    String thumbprint;
    @NotBlank
    String subject;
    CertInfoSubjectDTO certInfoSubjectDTO;

    public CertInfoDTO(String json){
        Load(json);
    }
    public CertInfoDTO(){
    }

    public CertInfoDTO(SignDTO signDTO){
        Load(signDTO);
    }


    public void loadSubject(){
        setCertInfoSubjectDTO(new CertInfoSubjectDTO(getSubject()));
    }

    public void Load(String json){
        Gson gson = new Gson();
        Map map = gson.fromJson(json, Map.class);
        setIssuer(map.get("issuer").toString());
        setNotafter(map.get("notafter").toString());
        setNotbefore(map.get("notbefore").toString());
        setSerialnumber(map.get("serialnumber").toString());
        setThumbprint(map.get("thumbprint").toString());
        setSubject(map.get("subject").toString());
        loadSubject();
    }

    public void Load(SignDTO signDTO){
        setIssuer(signDTO.getIssuer());
        setNotafter(signDTO.getNotafter());
        setNotbefore(signDTO.getNotbefore());
        setSerialnumber(signDTO.getSerialnumber());
        setThumbprint(signDTO.getThumbprint());
        setSubject(signDTO.getSubject());
        loadSubject();
    }

}
