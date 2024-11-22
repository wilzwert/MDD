import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { Topic } from '../../../../core/models/topic.interface';
import { Subscription } from '../../../../core/models/subscription.interface';
import {MatButtonModule} from '@angular/material/button';

@Component({
  selector: 'app-topic',
  standalone: true,
  imports: [MatButtonModule],
  templateUrl: './topic.component.html',
  styleUrl: './topic.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TopicComponent {
  @Input({required: true}) topic!: Topic;
  @Input({required: true}) loggedIn!: boolean | null;
  @Input({required: false}) subscriptions!: Subscription[] | null;
  @Input({required: false}) subscription!: Subscription | null;
  @Output() delete = new EventEmitter<Topic>();
  @Output() subscribe = new EventEmitter<number>();
  @Output() unSubscribe = new EventEmitter<number>();

  isSubscribed(topic: Topic) :boolean {
    return this.subscriptions !== null && this.subscriptions.some(s => s.topic.id == topic.id);
  }

  onDelete() {
    this.delete.emit(this.topic);
  }

  onSubscribe() {
    this.subscribe.emit(this.topic.id);
  }

  onUnsubscribe() {
    this.unSubscribe.emit(this.topic.id);
  }
}
