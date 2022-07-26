package uz.javharbek.oauth.config;

import uz.javharbek.oauth.dto.ServerCdnDTO;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
@ConfigurationProperties("cdn")
@Data
public class CdnProperties {
    HashMap<String, ServerCdnDTO> servers;
    ArrayList<String> inRotation;
}
