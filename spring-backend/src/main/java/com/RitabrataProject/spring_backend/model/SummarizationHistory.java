package com.RitabrataProject.spring_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "summarization_history")
public class SummarizationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private LocalDateTime timestamp ;

    public SummarizationHistory(String error) {
        this.summary = error ;
    }
    public SummarizationHistory(String url, String summary) {
        this.url = url;
        this.summary = summary;
        this.timestamp = LocalDateTime.now();
    }

    public SummarizationHistory() {

    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime createdAt) {
        this.timestamp = createdAt;
    }
}
