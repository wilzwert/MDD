package com.openclassrooms.mdd.service;

import com.openclassrooms.mdd.model.Comment;
import com.openclassrooms.mdd.model.Post;
import java.util.List;


/**
 * @author Wilhelm Zwertvaegher
 * Date:08/11/2024
 * Time:15:34
 */

public interface CommentService {
    List<Comment> getCommentsByPost(Post post);
}