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
@ApiModel(description = "Information of the user to login.")
public class UserLogin {

    @ApiModelProperty(notes = "User's username")
    @NotNull
    @NotBlank
    @Size(min = 1, max = 30)
    private String username;

    @ApiModelProperty(notes = "User's password")
    @NotNull
    @NotBlank
    @Size(min = 8, max=30)
    private String password;
}
