package com.doppler.app;

import com.doppler.app.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {
    @Autowired
    private Environment env;
    @Autowired
    private AppConfig appConfig;

    @RequestMapping("/")
    public String home() {
        return String.format("%s", appConfig);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("""
                    ______                  _             _____                    _      \s
                    |  _  \\                | |           /  ___|                  | |     \s
                    | | | |___  _ __  _ __ | | ___ _ __  \\ `--.  ___  ___ _ __ ___| |_ ___\s
                    | | | / _ \\| '_ \\| '_ \\| |/ _ \\ '__|  `--. \\/ _ \\/ __| '__/ _ \\ __/ __|
                    | |/ / (_) | |_) | |_) | |  __/ |    /\\__/ /  __/ (__| | |  __/ |_\\__ \\
                    |___/ \\___/| .__/| .__/|_|\\___|_|    \\____/ \\___|\\___|_|  \\___|\\__|___/
                               | |   | |                                                  \s
                               |_|   |_|                                                  \s
                                                                                          \s""");
            System.out.println(appConfig);

            System.out.println("Spring Config {\n" +
                    "  spring.application.name: '" + env.getProperty("spring.application.name") + '\'' +
                    ",\n  server.address: '" + env.getProperty("server.address") + '\'' +
                    ",\n  server.port: " + env.getProperty("server.port") +
                    ",\n  debug: " + env.getProperty("debug") +
                    "\n}");

            System.out.format("\nServer running at http://%s:%s/\n", env.getProperty("server.address"), env.getProperty("server.port"));
        };
    }
}
