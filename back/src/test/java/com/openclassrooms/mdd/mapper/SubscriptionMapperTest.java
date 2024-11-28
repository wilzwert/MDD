package com.openclassrooms.mdd.mapper;

import com.openclassrooms.mdd.dto.response.SubscriptionDto;
import com.openclassrooms.mdd.model.Subscription;
import com.openclassrooms.mdd.model.Topic;
import com.openclassrooms.mdd.model.User;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/11/2024
 * Time:14:39
 */

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Tag("Mapper")
public class SubscriptionMapperTest {

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @Test
    public void testNullSubscriptionToDto() {
        Subscription subscription = null;

        SubscriptionDto subscriptionDto = subscriptionMapper.subscriptionToSubscriptionDto(subscription);

        assertThat(subscriptionDto).isNull();
    }

    @Test
    public void testSubscriptionToDtoWithoutUser() {
        LocalDateTime now = LocalDateTime.now();
        Subscription subscription = new Subscription()
                .setTopic(new Topic().setId(1).setTitle("test topic").setDescription("test description"))
                .setCreatedAt(now)
                .setUpdatedAt(now);

        SubscriptionDto subscriptionDto = subscriptionMapper.subscriptionToSubscriptionDto(subscription);

        assertThat(subscriptionDto).isNotNull();
        assertThat(subscriptionDto.getUserId()).isEqualTo(0);
        assertThat(subscriptionDto.getTopic().getTitle()).isEqualTo("test topic");
        assertThat(subscriptionDto.getTopic().getDescription()).isEqualTo("test description");
        assertThat(subscriptionDto.getCreatedAt()).isEqualTo(now);
    }

    @Test
    public void testSubscriptionToDtoWithoutTopic() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User().setId(1);
        Subscription subscription = new Subscription()
                .setUser(user)
                .setCreatedAt(now)
                .setUpdatedAt(now);

        SubscriptionDto subscriptionDto = subscriptionMapper.subscriptionToSubscriptionDto(subscription);

        assertThat(subscriptionDto).isNotNull();
        assertThat(subscriptionDto.getCreatedAt()).isEqualTo(now);
        assertThat(subscriptionDto.getUserId()).isEqualTo(1);
        assertThat(subscriptionDto.getTopic()).isNull();

    }

    @Test
    public void testSubscriptionToDto() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User().setId(1);
        Subscription subscription = new Subscription()
                .setUser(user)
                .setTopic(new Topic().setId(1).setTitle("test topic").setDescription("test description"))
                .setCreatedAt(now)
                .setUpdatedAt(now);

        SubscriptionDto subscriptionDto = subscriptionMapper.subscriptionToSubscriptionDto(subscription);

        assertThat(subscriptionDto).isNotNull();
        assertThat(subscriptionDto.getUserId()).isEqualTo(1);
        assertThat(subscriptionDto.getTopic().getTitle()).isEqualTo("test topic");
        assertThat(subscriptionDto.getTopic().getDescription()).isEqualTo("test description");
        assertThat(subscriptionDto.getCreatedAt()).isEqualTo(now);
    }

    @Test
    public void testNullSubscriptionListToDto() {
        List<Subscription> subscriptions = null;

        List<SubscriptionDto> subscriptionDtos = subscriptionMapper.subscriptionToSubscriptionDto(subscriptions);

        assertThat(subscriptionDtos).isNull();
    }

    @Test
    public void testSubscriptionListToDtoList() {
        LocalDateTime now = LocalDateTime.now();
        User user1 = new User().setId(1).setUsername("testuser1");
        User user2 = new User().setId(2).setUsername("testuser2");
        Topic topic1 = new Topic().setId(1).setTitle("test topic 1");
        Topic topic2 = new Topic().setId(2).setTitle("test topic 2");

        Subscription subscription1 = new Subscription().setUser(user1).setTopic(topic1).setCreatedAt(now).setUpdatedAt(now);
        Subscription subscription2 = new Subscription().setUser(user2).setTopic(topic2).setCreatedAt(now).setUpdatedAt(now);

        List<SubscriptionDto> subscriptionDtos = subscriptionMapper.subscriptionToSubscriptionDto(Arrays.asList(subscription1, subscription2));

        assertThat(subscriptionDtos).isNotNull();
        assertThat(subscriptionDtos.size()).isEqualTo(2);

        assertThat(subscriptionDtos).extracting(SubscriptionDto::getUserId).containsExactly(1, 2);
        assertThat(subscriptionDtos).extracting(SubscriptionDto::getCreatedAt).containsExactly(now, now);
        assertThat(subscriptionDtos.getFirst().getTopic().getId()).isEqualTo(1);
        assertThat(subscriptionDtos.getFirst().getTopic().getTitle()).isEqualTo("test topic 1");
        assertThat(subscriptionDtos.get(1).getTopic().getId()).isEqualTo(2);
        assertThat(subscriptionDtos.get(1).getTopic().getTitle()).isEqualTo("test topic 2");

    }
}
