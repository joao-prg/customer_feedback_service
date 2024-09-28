package com.joaogoncalves.feedback.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "token")
public class RefreshToken {

    @Id
    private String id;

    @DocumentReference(lazy = true)
    private User user;
}
