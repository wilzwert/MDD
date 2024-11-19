import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { Topic } from '../../../../core/models/topic.interface';
import { Subscription } from '../../../../core/models/subscription.interface';
import { ExtendedTopic } from '../../../../core/models/extended-topic.interface';

@Component({
  selector: 'app-topic',
  standalone: true,
  imports: [],
  templateUrl: './topic.component.html',
  styleUrl: './topic.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TopicComponent {
  @Input({required: true}) topic!: ExtendedTopic;
  @Output() delete = new EventEmitter<ExtendedTopic>();
  @Output() subscribe = new EventEmitter<number>();
  @Output() unSubscribe = new EventEmitter<number>();

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
