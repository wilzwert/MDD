import { Component, Input } from '@angular/core';
import { Post } from '../../../../core/models/post.interface';

@Component({
  selector: 'app-post',
  standalone: true,
  imports: [],
  templateUrl: './post.component.html',
  styleUrl: './post.component.scss'
})
export class PostComponent {
  @Input({required: true}) post!: Post;

}
