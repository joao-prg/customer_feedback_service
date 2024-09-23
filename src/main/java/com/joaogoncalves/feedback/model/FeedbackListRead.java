package com.joaogoncalves.feedback.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FeedbackListRead {
    @JsonProperty("total_documents")
    private long totalDocuments;
    @JsonProperty("is_first_page")
    private boolean isFirstPage;
    @JsonProperty("is_last_page")
    private boolean isLastPage;
    private List<FeedbackRead> documents;

    public FeedbackListRead() {}

    public FeedbackListRead(long totalDocuments, boolean isFirstPage, boolean isLastPage, List<FeedbackRead> documents) {
        this.totalDocuments = totalDocuments;
        this.isFirstPage = isFirstPage;
        this.isLastPage = isLastPage;
        this.documents = documents;
    }

    public long getTotalDocuments() {
        return totalDocuments;
    }

    public void setTotalDocuments(long totalDocuments) {
        this.totalDocuments = totalDocuments;
    }


    public boolean getIsFirstPage() {  // Use "getIsFirstPage" to match JSON output
        return isFirstPage;
    }

    public void setIsFirstPage(boolean isFirstPage) {
        this.isFirstPage = isFirstPage;
    }

    public boolean getIsLastPage() {  // Same for "is_last_page"
        return isLastPage;
    }

    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    public List<FeedbackRead> getDocuments() {
        return documents;
    }

    public void setDocuments(List<FeedbackRead> documents) {
        this.documents = documents;
    }
}

