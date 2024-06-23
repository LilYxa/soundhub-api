package com.soundhub.api.dto.response;

import com.soundhub.api.dto.UserCompatibilityDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CompatibleUsersResponse {
    private List<UserCompatibilityDto> userCompatibilities;
}
