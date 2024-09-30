package com.joaogoncalves.feedback.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "feedbacks")
public class Feedback {

    @Id
    private String id;

    @NotNull
    private int rating;

    @NotNull
    @NotBlank(message = "Feedback cannot be blank")
    private String feedback;

    @DocumentReference(lookup = "{ 'username' : ?#{#target} }")
    private User user;

    @NotNull
    @NotBlank(message = "Product cannot be blank")
    @Size(max = 30)
    private String product;

    @NotNull
    @NotBlank(message = "Vendor cannot be blank")
    @Size(max = 30)
    private String vendor;
}
