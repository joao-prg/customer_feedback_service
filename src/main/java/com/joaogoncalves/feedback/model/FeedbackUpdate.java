package com.joaogoncalves.feedback.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Information of the feedback to be updated.")
public class FeedbackUpdate {

    @ApiModelProperty(notes = "The feedback rating")
    private int rating;

    @ApiModelProperty(notes = "The feedback")
    private String feedback;

    @ApiModelProperty(notes = "The username")
    @Size(max = 30)
    private String user;

    @ApiModelProperty(notes = "The product name")
    @Size(max = 30)
    private String product;

    @ApiModelProperty(notes = "The vendor name")
    @Size(max = 30)
    private String vendor;
}
