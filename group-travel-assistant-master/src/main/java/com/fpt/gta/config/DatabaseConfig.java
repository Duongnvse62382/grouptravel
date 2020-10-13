package com.fpt.gta.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.TimeZone;

@Configuration
@ConditionalOnMissingBean(DataSource.class)
public class DatabaseConfig {
    @Value("${config.db.DRIVER_CLASS_NAME}")
    public String DRIVER_CLASS_NAME;
    @Value("${config.db.url}")
    private String URL;
    @Value("${config.db.username}")
    private String username;
    @Value("${config.db.password}")
    private String password;


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(DRIVER_CLASS_NAME);
        dataSource.setUrl(
                URL + TimeZone.getDefault().getID()
        );
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }
}
