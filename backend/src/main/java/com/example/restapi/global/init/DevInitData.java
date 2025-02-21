package com.example.restapi.global.init;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Profile("dev")
@Configuration
public class DevInitData {

    @Bean
    public ApplicationRunner devApplicationRunner() {
        return args -> {
            genApiJsonFile("http://localhost:8080/v3/api-docs/apiV1", "apiV1.json");
            runCmd(List.of("/Users/hwayoung/.nvm/versions/node/v18.17.1/bin/npx",
                    "--package", "typescript", "--package", "openapi-typescript", "--package", "punycode",
                    "openapi-typescript", "apiV1.json", "-o", "schema.d.ts"));
        };
    }

    public void genApiJsonFile(String url, String filename) {

        Path filePath = Path.of(filename);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Files.writeString(filePath, response.body(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                System.out.println("JSON 데이터가 " + filePath.toAbsolutePath() + "에 저장되었습니다.");
            } else {
                System.err.println("오류: HTTP 상태 코드 " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void runCmd(List<String> command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println("프로세스 종료 코드: " + exitCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}