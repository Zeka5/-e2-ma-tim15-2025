package com.ma.ma_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GuildDto {
    private Long id;
    private String name;
    private UserDto leader;
    private List<UserDto> members;
    private Boolean hasActiveMission;
    private LocalDateTime createdAt;
}
