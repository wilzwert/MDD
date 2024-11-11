package com.openclassrooms.mdd.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mdd.repository.CommentRepository;
import com.openclassrooms.mdd.repository.PostRepository;
import com.openclassrooms.mdd.repository.TopicRepository;
import com.openclassrooms.mdd.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/11/2024
 * Time:10:53
 */

@SpringBootTest
@AutoConfigureMockMvc
@Tag("Integration")
public class PostControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TopicRepository topicRepository;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private CommentRepository commentRepository;

    // TODO
    @Nested
    class PostControllerRetrievalIT {

    }

    // TODO
    @Nested
    class PostControllerCreationIT {

    }

    // TODO
    @Nested
    class PostControllerUpdateIT {

    }

    // TODO
    @Nested
    class PostControllerDeleteIT {

    }

    // TODO
    @Nested
    class PostControllerCommentRetrievalIT {

    }

    // TODO
    @Nested
    class PostControllerCommentCreationIT {

    }
}
