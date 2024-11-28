import { Pipe, PipeTransform } from "@angular/core";
import { Subscription } from "../models/subscription.interface";
import { Topic } from "../models/topic.interface";

/**
 * Filters a Subscription list by a Topic
 */

@Pipe({
    name: 'filterByTopic',
    standalone: true
  })
  export class FilterByTopicPipe implements PipeTransform {
    transform(subscriptions: Subscription[]|null, topic: Topic) : Subscription|null {
        if(subscriptions == null) {
            return null;
        }
        const filtered = subscriptions.filter(s => s.topic.id == topic.id);
        return filtered.length ? filtered[0] : null;
    }
  }