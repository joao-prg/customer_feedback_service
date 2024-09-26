package com.joaogoncalves.feedback.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "feedback")
public class Feedback {

    @Id
    private UUID id;

    @NotNull
    private int rating;

    @NotBlank(message = "Feedback cannot be blank")
    private String feedback;

    @NotBlank(message = "Customer cannot be blank")
    @Size(max = 50)
    private String customer;

    @NotBlank(message = "Product cannot be blank")
    @Size(max = 50)
    private String product;

    @NotBlank(message = "Vendor cannot be blank")
    @Size(max = 50)
    private String vendor;
}
