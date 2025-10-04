package com.ma.ma_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FriendRequestDto {
    private Long id;
    private UserDto sender;
    private UserDto receiver;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
}
