package uz.javharbek.oauth.config.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class CoreDBConfig {
    @Bean(name = "coreDbSource")
    @ConfigurationProperties("spring.coredb")
    @Primary
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "jdbcCoreDb")
    @Primary
    @Autowired
    public JdbcTemplate mainJdbcTemplate(@Qualifier("coreDbSource") DataSource dsMaster) {
        return new JdbcTemplate(dsMaster);
    }


}
