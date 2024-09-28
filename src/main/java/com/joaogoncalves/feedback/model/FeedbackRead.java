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
@ApiModel(description = "List with feedbacks information.")
public class FeedbackRead {

    @ApiModelProperty(notes = "The feedback ID")
    @NotBlank
    private String id;

    @ApiModelProperty(notes = "The feedback rating")
    @NotNull
    private int rating;

    @ApiModelProperty(notes = "The feedback")
    @NotBlank(message = "Feedback cannot be blank")
    private String feedback;

    @ApiModelProperty(notes = "The username")
    @NotBlank(message = "User cannot be blank")
    @Size(max = 30)
    private String user;

    @ApiModelProperty(notes = "The product name")
    @NotBlank(message = "Product cannot be blank")
    @Size(max = 30)
    private String product;

    @ApiModelProperty(notes = "The vendor name")
    @NotBlank(message = "Vendor cannot be blank")
    @Size(max = 30)
    private String vendor;
}
