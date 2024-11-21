import { Pipe, PipeTransform } from "@angular/core";
import { Subscription } from "../models/subscription.interface";
import { Topic } from "../models/topic.interface";

@Pipe({
    name: 'filterByTopic',
    standalone: true
  })
  export class FilterByTopicPipe implements PipeTransform {
    transform(subscriptions: Subscription[]|null, topic: Topic) : any {
        if(subscriptions == null) {
            return null;
        }
        return subscriptions.filter(s => s.topic.id == topic.id)[0];
    }
  }