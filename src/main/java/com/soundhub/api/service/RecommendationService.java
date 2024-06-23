package com.soundhub.api.service;

import java.util.List;
import java.util.UUID;

public interface RecommendationService {
    List<UUID> getUsers(UUID user);

    List<UUID> recommendUsers(UUID targetUser);
}
