package com.joaogoncalves.feedback.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "List with feedbacks information.")
public class FeedbackListRead {

    @ApiModelProperty(notes = "Total documents found with a given criteria")
    @JsonProperty("total_documents")
    private long totalDocuments;

    @ApiModelProperty(notes = "Flags if the current page is the first page")
    @JsonProperty("is_first_page")
    private boolean isFirstPage;

    @ApiModelProperty(notes = "Flags if the current page is the last page")
    @JsonProperty("is_last_page")
    private boolean isLastPage;

    @ApiModelProperty(notes = "List of documents found with a given criteria")
    private List<FeedbackRead> documents;
}
