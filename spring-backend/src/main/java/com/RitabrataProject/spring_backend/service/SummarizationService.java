package com.RitabrataProject.spring_backend.service;

import com.RitabrataProject.spring_backend.model.SummarizationHistory;
import com.RitabrataProject.spring_backend.repository.SummarizationHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.RitabrataProject.spring_backend.Summarizer$;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class SummarizationService {

    @Autowired
    private SummarizationHistoryRepository summarizationHistoryRepository;

    // WebClient to interact with FastAPI for summarization
    private final WebClient webClient;

    public SummarizationService(@Value("${python.api.url}") String apiUrl) {
        this.webClient = WebClient.create(apiUrl);
    }

    // Method that calls the Python FastAPI for summarizing content
    public CompletableFuture<String> getSummary(String url) {
        // Call the FastAPI and return a summary asynchronously
        return CompletableFuture.supplyAsync(() -> {
            try {
                String summary = webClient.post()
                        .uri("/summarize/")
                        .bodyValue(new SummarizationRequest(url))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();  // This waits for the result

                return summary;
            } catch (Exception e) {
                e.printStackTrace();
                return "Error summarizing content";
            }
        });
    }

    // Save the summary and URL into the database
//    public void saveSummaryToDb(String summary, String url) {
//        SummarizationHistory history = new SummarizationHistory();
//        history.setSummary(summary);
//        history.setUrl(url);
//        history.setTimestamp(LocalDateTime.now());
//        summarizationHistoryRepository.save(history);
//    }


    // Define SummarizationRequest inner class
    public static class SummarizationRequest {
        private String url;

        public SummarizationRequest(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
