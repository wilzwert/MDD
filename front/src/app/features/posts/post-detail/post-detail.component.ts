import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { Post } from '../../../core/models/post.interface';
import { PostService } from '../../../core/services/post.service';
import { AsyncPipe } from '@angular/common';
import { CommentService } from '../../../core/services/comment.service';
import { Comment } from '../../../core/models/comment.interface';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CreateCommentRequest } from '../../../core/models/create-comment-request.interface';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [AsyncPipe, ReactiveFormsModule],
  templateUrl: './post-detail.component.html',
  styleUrl: './post-detail.component.scss'
})
export class PostDetailComponent implements OnInit {
  public post$!: Observable<Post>;
  public comments$!: Observable<Comment[]>;
  public form!: FormGroup;
  private postId!: number;


  constructor(private activatedRoute: ActivatedRoute, private postService: PostService, private commentService: CommentService, private fb: FormBuilder) {
  }

  private initForm() :void {
    this.form = this.fb.group({
      content: [
        '', 
        [
          Validators.required,
          Validators.min(10)
        ]
      ],
    });
  }

  submit(): void {
    console.log(this.form.value as CreateCommentRequest);
    this.commentService.createPostComment(this.postId, this.form.value as CreateCommentRequest).subscribe();
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      this.post$ = this.postService.getPostById(params['id']); // Access the 'id' parameter from the URL
      this.comments$ = this.commentService.getPostComments(params['id']); // Access the 'id' parameter from the URL
      this.initForm();
    });
    this.post$.subscribe({next: (p) => this.postId = p.id});
    this.comments$.subscribe({next: console.log});
  }
}
