package com.openclassrooms.mdd.security.service;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Wilhelm Zwertvaegher
 * Date:06/11/2024
 * Time:08:59
 */

public class UserDetailsImplTest {

    @Test
    public void shouldEqualWhenSameInstance() {
        UserDetailsImpl userDetails = new UserDetailsImpl(1, "test@example.com", "password");
        UserDetailsImpl sameUserDetails = userDetails;

        assertThat(userDetails.equals(sameUserDetails)).isTrue();
    }

    @Test
    public void shouldNotEqualWhenNull() {
        UserDetailsImpl userDetails = new UserDetailsImpl(1, "test@example.com", "password");
        UserDetailsImpl compareUserDetails = null;

        assertThat(userDetails.equals(compareUserDetails)).isFalse();
    }

    @Test
    public void shouldNotEqualWhenDifferentClass() {
        @Data
        @AllArgsConstructor
        class FakeUserDetails {
            private int id;
            private String username;
            @JsonIgnore
            private String password;
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(1, "test@example.com", "password");
        FakeUserDetails differentClass = new FakeUserDetails(1, "test@example.com", "password");


        assertThat(userDetails.equals(differentClass)).isFalse();
    }

    @Test
    public void shouldEqualWhenSameId() {
        UserDetailsImpl userDetails = new UserDetailsImpl(1, "test@example.com", "password");
        UserDetailsImpl compareUserDetails = new UserDetailsImpl(1, "othertest@example.com", "abcd1234");

        assertThat(userDetails.equals(compareUserDetails)).isTrue();
    }
}
