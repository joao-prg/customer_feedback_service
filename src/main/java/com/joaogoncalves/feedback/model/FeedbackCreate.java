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
@ApiModel(description = "Information of the feedback to be created.")
public class FeedbackCreate {

    @ApiModelProperty(notes = "The feedback rating")
    @NotNull
    private int rating;

    @ApiModelProperty(notes = "The feedback")
    @NotNull
    @NotBlank
    private String feedback;

    @ApiModelProperty(notes = "The username")
    @NotNull
    @NotBlank
    @Size(min = 1, max = 30)
    private String user;

    @ApiModelProperty(notes = "The product name")
    @NotNull
    @NotBlank(message = "Product cannot be blank")
    @Size(min = 1, max = 30)
    private String product;

    @ApiModelProperty(notes = "The vendor name")
    @NotNull
    @NotBlank
    @Size(min = 1, max = 30)
    private String vendor;
}
