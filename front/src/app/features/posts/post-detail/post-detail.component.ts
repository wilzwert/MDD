import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { Post } from '../../../core/models/post.interface';
import { PostService } from '../../../core/services/post.service';
import { AsyncPipe, DatePipe } from '@angular/common';
import { CommentService } from '../../../core/services/comment.service';
import { Comment } from '../../../core/models/comment.interface';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CreateCommentRequest } from '../../../core/models/create-comment-request.interface';
import { Title } from '@angular/platform-browser';
import { MatIconModule } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [AsyncPipe, ReactiveFormsModule, DatePipe, MatIconModule, MatIconButton, MatFormFieldModule, MatInputModule],
  templateUrl: './post-detail.component.html',
  styleUrl: './post-detail.component.scss'
})
export class PostDetailComponent implements OnInit {
  public post$!: Observable<Post>;
  public comments$!: Observable<Comment[]>;
  public form!: FormGroup;
  private postId!: number;


  constructor(
    private activatedRoute: ActivatedRoute, 
    private postService: PostService, 
    private commentService: CommentService, 
    private notificationService: NotificationService,
    private fb: FormBuilder, 
    private title: Title) {
  }

  private initForm() :void {
    this.form = this.fb.group({
      content: [
        '', 
        [
          Validators.required,
          Validators.minLength(5)
        ]
      ],
    });
  }

  submit(): void {
    this.commentService.createPostComment(this.postId, this.form.value as CreateCommentRequest)
      .pipe(
        tap(() => this.notificationService.confirmation("Merci pour votre commentaire !"))
      )
      .subscribe();
  }

  get content() {
    return this.form.get('content');
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      // Access the 'id' parameter from the URL
      this.postId = params['id'];
      this.post$ = this.postService.getPostById(params['id']).pipe(
        // set page title once the post is available
        tap((post: Post) =>{this.title.setTitle(`Article - ${post.title}`)})
      );
      // load post comments 
      this.comments$ = this.commentService.getPostComments(params['id']); 
      this.initForm();
    });
  }
}
