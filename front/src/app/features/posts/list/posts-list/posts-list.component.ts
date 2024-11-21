import { Component } from '@angular/core';
import { PostService } from '../../../../core/services/post.service';
import { Post } from '../../../../core/models/post.interface';
import { Observable } from 'rxjs';
import { AsyncPipe } from '@angular/common';
import { PostComponent } from '../post/post.component';

@Component({
  selector: 'app-posts-list',
  standalone: true,
  imports: [PostComponent, AsyncPipe],
  templateUrl: './posts-list.component.html',
  styleUrl: './posts-list.component.scss'
})
export class PostsListComponent {

  public posts$!: Observable<Post[]>;

  constructor(private postService: PostService) {
    this.posts$ = this.postService.getAllPosts();
  }
}
