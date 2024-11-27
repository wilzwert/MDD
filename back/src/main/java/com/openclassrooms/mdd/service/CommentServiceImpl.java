package com.openclassrooms.mdd.service;


import com.openclassrooms.mdd.model.Comment;
import com.openclassrooms.mdd.model.Post;
import com.openclassrooms.mdd.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:07/11/2024
 * Time:16:32
 */
@Service
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    public CommentServiceImpl(
        final CommentRepository commentRepository
    ) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> getCommentsByPost(Post post) {
        return commentRepository.findCommentsByPost(post, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
