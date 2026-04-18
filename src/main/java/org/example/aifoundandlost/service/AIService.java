package org.example.aifoundandlost.service;

public interface AIService {
    String generateDescription(String itemName);
    String analyzeData();
    String searchItems(String description);
}