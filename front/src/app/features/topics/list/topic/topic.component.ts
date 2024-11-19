import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { User } from '../../../../core/models/user.interface';
import { Topic } from '../../../../core/models/topic.interface';

@Component({
  selector: 'app-topic',
  standalone: true,
  imports: [],
  templateUrl: './topic.component.html',
  styleUrl: './topic.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TopicComponent {
  @Input({required: true}) topic!: Topic;
  @Output() delete = new EventEmitter<number>();

  onDelete() {
    this.delete.emit(this.topic.id);
  }
}
