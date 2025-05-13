package dev.interview.server.ai;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @PostMapping("/generate-questions")
    public ResponseEntity<Map<String, String>> generateQuestions(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        String userId = request.get("userId");

        Map<String, String> response = new HashMap<>();
        response.put("message", "Test API called successfully");
        response.put("content", content);
        response.put("userId", userId);

        return ResponseEntity.ok(response);
    }
}

