package com.soundhub.api.service.invite.strategy;

import com.soundhub.api.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Factory class for managing and retrieving implementations of {@link InviteStrategy}.
 * This class uses a map-based approach to store and retrieve strategies by their class name.
 */
@Component
public class InviteStrategyFactory {

    private final Map<String, InviteStrategy> strategies;

    /**
     * Constructs an {@code InviteStrategyFactory} by collecting all available {@link InviteStrategy}
     * implementations and mapping them by their class names.
     *
     * @param strategyList A list of available {@link InviteStrategy} implementations, automatically
     *                     injected by Spring's dependency injection framework.
     */
    @Autowired
    public InviteStrategyFactory(List<InviteStrategy> strategyList) {
        strategies = strategyList.stream()
                .collect(Collectors.toMap(strategy -> strategy.getClass().getSimpleName(), strategy -> strategy));
    }

    public InviteStrategy getStrategy(String strategyName) {
        InviteStrategy strategy = strategies.get(strategyName);
        if (strategy == null) {
            throw new IllegalArgumentException(String.format(Constants.NO_INVITE_STRATEGY_MESSAGE, strategyName));
        }
        return strategy;
    }
}

