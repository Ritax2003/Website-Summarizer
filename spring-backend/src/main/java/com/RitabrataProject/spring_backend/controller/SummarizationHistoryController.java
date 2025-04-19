package com.RitabrataProject.spring_backend.controller;

import com.RitabrataProject.spring_backend.model.SummarizationHistory;
import com.RitabrataProject.spring_backend.service.SummarizationService;
import com.RitabrataProject.spring_backend.repository.SummarizationHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.RitabrataProject.spring_backend.Summarizer$;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/summarization")
public class SummarizationHistoryController {

    @Autowired
    private SummarizationService summarizationService;
    @Autowired
    private SummarizationHistoryRepository summarizationHistoryRepository;

    @PostMapping("/add")
    public ResponseEntity<String> addSummary(@RequestBody SummarizationRequest request) {
        try {
            // Fetch the summary from Python FastAPI asynchronously
            String summary = summarizationService.getSummary(request.getUrl()).get();  // Blocking for simplicity

            if (summary == null || summary.isEmpty()) {
                return ResponseEntity.status(500).body("No summary returned from FastAPI.");
            }

            // Save the summary to the database
            //summarizationService.saveSummaryToDb(summary, request.getUrl());
            //using the scala object to save
            Summarizer$.MODULE$.saveSummaryToDb(summary,request.getUrl());
            return ResponseEntity.ok(summary);

        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<SummarizationHistory>> getAllSummaries() {
        List<SummarizationHistory> allSummaries = summarizationHistoryRepository.findAll();
        return ResponseEntity.ok(allSummaries);
    }

    public static class SummarizationRequest {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
