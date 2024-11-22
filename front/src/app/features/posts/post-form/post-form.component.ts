import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { PostService } from '../../../core/services/post.service';
import { CreatePostRequest } from '../../../core/models/createPostRequest.interface';
import { Observable, take } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Post } from '../../../core/models/post.interface';
import { MatOptionModule } from '@angular/material/core';
import { AsyncPipe } from '@angular/common';
import { Topic } from '../../../core/models/topic.interface';
import { TopicService } from '../../../core/services/topic.service';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-post-form',
  standalone: true,
  imports: [
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    MatIconModule,
    MatInputModule,
    ReactiveFormsModule,
    MatOptionModule,
    AsyncPipe
  ],
  templateUrl: './post-form.component.html',
  styleUrl: './post-form.component.scss'
})
export class PostFormComponent implements OnInit {
  public error: string |null = null;

  public form: FormGroup | undefined;

  public topics$!:Observable<Topic[]>;

  constructor(
    private postService: PostService,
    private topicService:TopicService,
    private router: Router,
    private fb: FormBuilder,
    private matSnackBar: MatSnackBar,
  ) {
    this.topics$ = this.topicService.getAllTopics();
  }

  ngOnInit(): void {
    this.initForm();
  }

  submit() :void {
    this.error = null;
    this.postService.createPost(this.form?.value as CreatePostRequest)
    .pipe(take(1))
    .subscribe({
      next: data => {
        this.matSnackBar.open("Your post has been created.", 'Close', { duration: 3000 })
        this.router.navigate(["/posts"])
      },
      error: err => {this.error = err.error?.message}
    });
  }

  private initForm(post?: Post): void {
    this.form = this.fb.group({
      topicId: [
        post?.topic.id, 
        [
          Validators.required,
          Validators.pattern('^[0-9]+$')
        ]
      ],
      title: [
        post?.title, 
        [
          Validators.required,
          Validators.min(10)
        ]
      ],
      content: [
        post?.content,
        [
          Validators.required,
          Validators.min(50)
        ]
      ]
    });
  }

}
