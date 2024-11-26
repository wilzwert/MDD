import { Component, Input } from '@angular/core';
import { Post } from '../../../../core/models/post.interface';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-post',
  standalone: true,
  imports: [RouterLink, MatCardModule, DatePipe],
  templateUrl: './post.component.html',
  styleUrl: './post.component.scss'
})
export class PostComponent {
  @Input({required: true}) post!: Post;

}
