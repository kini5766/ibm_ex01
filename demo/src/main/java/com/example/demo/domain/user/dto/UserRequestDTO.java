package com.example.demo.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserRequestDTO {

    public interface addGroup {
    }
    public interface passwordGroup {
    }
    public interface updateGroup {
    }
    public interface deleteGroup {
    }

    @NotBlank(groups = {addGroup.class, updateGroup.class, deleteGroup.class})
    private String username;

    @NotBlank(groups = {addGroup.class, passwordGroup.class})
    @Size(min = 4)
    private String password;

    @NotBlank(groups = {addGroup.class, updateGroup.class})
    private String email;

    @NotBlank(groups = {addGroup.class, updateGroup.class})
    private String nickname;
}
