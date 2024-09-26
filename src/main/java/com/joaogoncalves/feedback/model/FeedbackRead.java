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
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "List with feedbacks information.")
public class FeedbackRead {

    @ApiModelProperty(notes = "The feedback ID")
    @NotBlank
    private UUID id;

    @ApiModelProperty(notes = "The feedback rating")
    @NotNull
    private int rating;

    @ApiModelProperty(notes = "The feedback")
    @NotBlank(message = "Feedback cannot be blank")
    private String feedback;

    @ApiModelProperty(notes = "The customer name")
    @NotBlank(message = "Customer cannot be blank")
    @Size(max = 50)
    private String customer;

    @ApiModelProperty(notes = "The product name")
    @NotBlank(message = "Product cannot be blank")
    @Size(max = 50)
    private String product;

    @ApiModelProperty(notes = "The vendor name")
    @NotBlank(message = "Vendor cannot be blank")
    @Size(max = 50)
    private String vendor;
}
