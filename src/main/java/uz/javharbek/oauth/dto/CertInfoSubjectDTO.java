package uz.javharbek.oauth.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.HashMap;

@Data
public class CertInfoSubjectDTO {
    String CN;
    String SN;
    String L;
    String S;
    String C;
    String STREET;
    String E;
    String O;
    String OU;
    String uzINN;

    public CertInfoSubjectDTO (String subject){
        Load(subject);
    }
    public CertInfoSubjectDTO (){
    }

    /**
     * @example CN=Абсаломов ткир Абдусамад кли, SN=0, L=Ташкент, S=ТОШКЕНТ ШАХРИ, C=UZ, STREET=Ташкент, E=xb.uz, O=Xalq Banki boshqaruvi, OU=UZC006900610079, uzINN=544539328
     * @param subject
     */
    public void Load(String subject){
        HashMap<String,String> infoSubject = new HashMap<>();
        String[] strs = subject.split(",");
        for (String str: strs) {
            String[] data = str.split("=");
            String name = data[0].trim();
            String value = data[1].trim();
            infoSubject.put(name,value);
            if(name.equals("CN")) setCN(value);
            if(name.equals("SN")) setSN(value);
            if(name.equals("L")) setL(value);
            if(name.equals("S")) setS(value);
            if(name.equals("C")) setC(value);
            if(name.equals("STREET")) setSTREET(value);
            if(name.equals("E")) setE(value);
            if(name.equals("O")) setO(value);
            if(name.equals("OU")) setOU(value);
            if(name.equals("uzINN")) setUzINN(value);
        }
    }
}
