import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { Post } from '../../../core/models/post.interface';
import { PostService } from '../../../core/services/post.service';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-post-detail',
  standalone: true,
  imports: [AsyncPipe],
  templateUrl: './post-detail.component.html',
  styleUrl: './post-detail.component.scss'
})
export class PostDetailComponent implements OnInit {
  public post$!: Observable<Post>;

  constructor(private activatedRoute: ActivatedRoute, private postService: PostService) {}

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      this.post$ = this.postService.getPostById(params['id']); // Access the 'id' parameter from the URL
    });
  }
}
