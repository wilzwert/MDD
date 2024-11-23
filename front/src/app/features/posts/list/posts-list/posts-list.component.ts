import { Component, OnInit } from '@angular/core';
import { PostService } from '../../../../core/services/post.service';
import { Post } from '../../../../core/models/post.interface';
import { Observable } from 'rxjs';
import { AsyncPipe } from '@angular/common';
import { PostComponent } from '../post/post.component';
import { RouterLink } from '@angular/router';
import { MatGridListModule } from '@angular/material/grid-list';
import { User } from '../../../../core/models/user.interface';
import { PostSort } from '../../../../core/models/post-sort.interface';

@Component({
  selector: 'app-posts-list',
  standalone: true,
  imports: [PostComponent, AsyncPipe, RouterLink, MatGridListModule],
  templateUrl: './posts-list.component.html',
  styleUrl: './posts-list.component.scss'
})
export class PostsListComponent implements OnInit {

  public cols!:number;

  public posts$!: Observable<Post[]>;
  
  // current sorting state
  private sort: PostSort = {} as PostSort;

  constructor(private postService: PostService) {
    this.posts$ = this.postService.getAllPosts();
  }

  sortByPostCreation(order: 'asc' | 'desc') :void {
    this.sort.sortByPost = true;
    this.sort.orderByPostAscending = order == 'asc';
    // sorting by post creation date resets author sorting
    this.sort.sortByAuthor = false;
    this.posts$ = this.postService.getAllPosts(this.sort);
  }

  sortByAuthor(order: 'asc' | 'desc') :void {
    this.sort.sortByAuthor = true;
    this.sort.orderByAuthorAscending = order == 'asc';
    this.posts$ = this.postService.getAllPosts(this.sort);
  }

  onResize(event: any) {
    this.cols = (event.target.innerWidth <= 400) ? 1 : 2;
  }

  ngOnInit(): void {
    this.cols = (window.innerWidth <= 400) ? 1 : 2;
  }
}
