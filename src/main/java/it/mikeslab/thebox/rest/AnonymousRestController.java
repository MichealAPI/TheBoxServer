package it.mikeslab.thebox.rest;

import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.chat.Chat;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import lombok.RequiredArgsConstructor;
import org.cloudinary.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class AnonymousRestController {

    private final Environment environment;

    @PostMapping("/anonymize")
    public ResponseEntity<String> rewrite(@RequestBody String payload) {

        // Transform the payload to JSON
        JSONObject json = new JSONObject(payload);
        String comment = json.getString("comment");

        String prompt = environment.getProperty("anonymous.rewrite.prompt") + comment;

        SimpleOpenAI simpleOpenAI = SimpleOpenAI
                .builder()
                .apiKey(environment.getProperty("openai.api.key"))
                .build();

        ChatRequest chatRequest = ChatRequest
                .builder()
                .model("gpt-3.5-turbo")
                .message(ChatMessage.SystemMessage.of(prompt))
                .message(ChatMessage.UserMessage.of(comment))
                .temperature(0.0)
                .maxCompletionTokens(100)
                .build();

        CompletableFuture<Chat> futureChat = simpleOpenAI
                .chatCompletions()
                .create(chatRequest);

        Chat response = futureChat.join();
        String rewrittenText = response.firstContent();

        return ResponseEntity.ok(rewrittenText);
    }

}
