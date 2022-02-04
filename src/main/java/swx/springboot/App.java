package swx.springboot;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@ComponentScan ({"swx.springboot.controller", "swx.springboot.service", "swx.store.dao"})
public class App
{
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
    	/*
        SpringApplication app = new SpringApplication(App.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
        app.run(args);       
        */
        SpringApplication.run(App.class, args);
    }
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource)
    {
        return new JdbcTemplate(dataSource);
    }
}
