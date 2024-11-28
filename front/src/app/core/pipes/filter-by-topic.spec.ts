import { Subscription } from "../models/subscription.interface";
import { Topic } from "../models/topic.interface";
import { FilterByTopicPipe } from "./filter-by-topic";

describe("FilterByTopicPipe unit tests", () =>  {
    let filter: FilterByTopicPipe;

    beforeEach(() => {
        filter = new FilterByTopicPipe();
    });

    it('should return null when subscriptions is null', () => {
        expect(filter.transform(null, {id: 1, title: 'Topic title', description: 'Topic description', createdAt: '2024-11-01T00:00:00', updatedAt: '2024-11-01T00:00:00'})).toBeNull();
    }); 

    it('should return a subscription when matches topic', () => {
        const subscriptions:Subscription[] = [
            {userId: 1, createdAt: '2024-11-12T12:00:00', topic: {id: 1, title: 'Topic title', description: 'Topic description', createdAt: '2024-11-01T00:00:00', updatedAt: '2024-11-01T00:00:00'} },
            {userId: 2, createdAt: '2024-11-12T12:00:00', topic: {id: 2, title: 'Second topic title', description: 'Second topic description', createdAt: '2024-11-01T00:00:00', updatedAt: '2024-11-01T00:00:00'} }
        ];
        const topic: Topic = {id: 1, title: 'Topic title', description: 'Topic description', createdAt: '2024-11-01T00:00:00', updatedAt: '2024-11-01T00:00:00'};
        expect(filter.transform(subscriptions, topic)).toEqual(subscriptions[0]);
    }); 

    it('should return null when no subscription matches topic', () => {
        const subscriptions:Subscription[] = [
            {userId: 1, createdAt: '2024-11-12T12:00:00', topic: {id: 1, title: 'Topic title', description: 'Topic description', createdAt: '2024-11-01T00:00:00', updatedAt: '2024-11-01T00:00:00'} },
            {userId: 2, createdAt: '2024-11-12T12:00:00', topic: {id: 2, title: 'Second topic title', description: 'Second topic description', createdAt: '2024-11-01T00:00:00', updatedAt: '2024-11-01T00:00:00'} }
        ];
        const topic: Topic = {id: 3, title: 'Third topic title', description: 'Third Topic description', createdAt: '2024-11-01T00:00:00', updatedAt: '2024-11-01T00:00:00'};
        expect(filter.transform(subscriptions, topic)).toBeNull();
    });
});