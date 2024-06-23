package com.soundhub.api.dto;

import com.soundhub.api.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserCompatibilityDto {
    private User user;
    private float compatibility;
}
