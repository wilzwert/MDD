import { Component, OnInit } from '@angular/core';
import { PostService } from '../../../../core/services/post.service';
import { Post } from '../../../../core/models/post.interface';
import { Observable } from 'rxjs';
import { AsyncPipe } from '@angular/common';
import { PostComponent } from '../post/post.component';
import { RouterLink } from '@angular/router';
import { MatGridListModule } from '@angular/material/grid-list';
import { PostSort } from '../../../../core/models/post-sort.interface';
import { MatButtonModule } from '@angular/material/button';
import {MatMenuModule} from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-posts-list',
  standalone: true,
  imports: [PostComponent, AsyncPipe, RouterLink, MatGridListModule, MatButtonModule, MatTooltipModule, MatMenuModule, MatIconModule],
  templateUrl: './posts-list.component.html',
  styleUrl: './posts-list.component.scss'
})
export class PostsListComponent {

  public posts$!: Observable<Post[]>;
  
  // current sorting state
  public sort: PostSort = {sortByPost: true, orderByPostAscending: false} as PostSort;

  public orderAscending: boolean = false;

  constructor(private postService: PostService) {
    this.posts$ = this.postService.getAllPosts();
  }

  sortByPostCreation() :void {
    this.sort.sortByPost = true;
    this.sort.sortByAuthor = this.sort.orderByAuthorAscending = undefined;
    // when we sort by post creation we keep previous order
    this.sort.orderByPostAscending = this.orderAscending;
    // sorting by post creation date resets author sorting
    this.sort.sortByAuthor = false;
    this.posts$ = this.postService.getAllPosts(this.sort);
  }

  sortByAuthor() :void {
    this.sort.sortByAuthor = true;
    this.sort.sortByPost = false;
    // by default, sorting by author gets ascending order
    this.sort.orderByAuthorAscending = this.orderAscending = true;
    this.posts$ = this.postService.getAllPosts(this.sort);
  }

  sortOrder(): void {
    this.orderAscending = !this.orderAscending;
    if(this.sort.sortByAuthor) {
      this.sort.orderByAuthorAscending = this.orderAscending;
    }
    if(this.sort.sortByPost) {
      this.sort.orderByPostAscending = this.orderAscending;
    }
    this.posts$ = this.postService.getAllPosts(this.sort);
  }
}