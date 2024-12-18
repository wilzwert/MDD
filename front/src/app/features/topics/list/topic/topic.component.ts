import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { Topic } from '../../../../core/models/topic.interface';
import { Subscription } from '../../../../core/models/subscription.interface';
import {MatButtonModule} from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-topic',
  standalone: true,
  imports: [MatButtonModule, MatCardModule],
  templateUrl: './topic.component.html',
  styleUrl: './topic.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TopicComponent {
  @Input({required: true}) topic!: Topic;
  @Input({required: true}) loggedIn!: boolean | null;
  @Input({required: false}) subscription!: Subscription | null;
  @Output() subscribe = new EventEmitter<number>();
  @Output() unSubscribe = new EventEmitter<number>();

  onSubscribe() {
    this.subscribe.emit(this.topic.id);
  }

  onUnsubscribe() {
    this.unSubscribe.emit(this.topic.id);
  }
}
