package com.openclassrooms.mdd.mapper;

import com.openclassrooms.mdd.dto.response.SubscriptionDto;
import com.openclassrooms.mdd.model.Subscription;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:38
 */

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SubscriptionMapper {

    @Mappings({
            @Mapping(source = "user.id", target = "userId"),
    })
    SubscriptionDto subscriptionToSubscriptionDto(Subscription subscription);

    List<SubscriptionDto> subscriptionToSubscriptionDto(List<Subscription> subscriptions);
}