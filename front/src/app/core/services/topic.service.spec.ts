import { TestBed } from '@angular/core/testing';

import { TopicService } from './topic.service';
import { Topic } from '../models/topic.interface';
import { HttpTestingController, provideHttpClientTesting, TestRequest } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

describe('TopicService', () => {
  let service: TopicService;
  let mockHttpController: HttpTestingController;
  let spyOnClearCache: any;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ]
    });
    service = TestBed.inject(TopicService);
    mockHttpController = TestBed.inject(HttpTestingController);
    spyOnClearCache = jest.spyOn(service, 'clearCache');
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve all topics', (done) => {
    const mockTopics: Topic[] = [
      {id: 1, title: "Test topic", description: "Test topic description", createdAt: '2024-09-10T01:00:00', updatedAt: '2024-09-10T01:00:00'}
    ];

    service.getAllTopics().subscribe(
      (topics: Topic[]) => {
        expect(topics).toEqual(mockTopics);
        done();
      }
    );

    const getRequest: TestRequest = mockHttpController.expectOne("api/topics");
    expect(getRequest.request.method).toEqual("GET");
    getRequest.flush(mockTopics);
  });

  it('should retrieve all posts from cache when present', (done) => {
    const mockTopics: Topic[] = [
      {id: 1, title: "Test topic", description: "Test topic description", createdAt: '2024-09-10T01:00:00', updatedAt: '2024-09-10T01:00:00'}
    ];
    // first retrieval should populate local cache
    service.getAllTopics().subscribe(
      (topics: Topic[]) => {
        expect(topics).toEqual(mockTopics);
      }
    );

    const getRequest: TestRequest = mockHttpController.expectOne("api/topics");
    expect(getRequest.request.method).toEqual("GET");
    getRequest.flush(mockTopics);

    // second retrieval should not trigger API request as local cache should be present
    service.getAllTopics().subscribe(
      (topics: Topic[]) => {
        expect(topics).toEqual(mockTopics);
        done();
      }
    );

    mockHttpController.expectNone("api/topics");
  });
  
  it('should clear local cache and trigger api request on next retrieval', (done) => {
    // to manually check the effect of cache clearing on API requests,
    // we have to do a first retrieval, manually clear the local cache
    // and do another retrieval to ensure API request is triggered

    // first retrieval should trigger an API request, as no local cache is present on first call
    const mockTopics: Topic[] = [
      {id: 1, title: "Test topic", description: "Test topic description", createdAt: '2024-09-10T01:00:00', updatedAt: '2024-09-10T01:00:00'}
    ];
    // first retrieval should populate local cache
    service.getAllTopics().subscribe(
      (topics: Topic[]) => {
        expect(topics).toEqual(mockTopics);
      }
    );

    const getRequest: TestRequest = mockHttpController.expectOne("api/topics");
    expect(getRequest.request.method).toEqual("GET");
    getRequest.flush(mockTopics);

    // cache has thus no reason to get cleared
    expect(spyOnClearCache).not.toHaveBeenCalled();
    
    // let's manually clear local cache
    service.clearCache();
    // clean up the spy to avoid previous invocation to interfere with further tests
    spyOnClearCache.mockClear();

    // it makes sense to check that no API request has been made on cache clearing
    // as earlier versions of the topic service did trigger requests because of a misuse of the local BehaviorSubject
    mockHttpController.expectNone("api/topics");

    // next retrieval should trigger an API request
    service.getAllTopics().subscribe(
      (topics: Topic[]) => {
        expect(topics).toEqual(mockTopics);
        done();
      }
    );
    const nextRequest: TestRequest = mockHttpController.expectOne("api/topics");
    expect(nextRequest.request.method).toEqual("GET");
    nextRequest.flush(mockTopics);
    expect(spyOnClearCache).not.toHaveBeenCalled();
  });

  it('should retrieve all topics from API when cache present but expired', (done) => {
    const mockTopics: Topic[] = [
      {id: 1, title: "Test topic", description: "Test topic description", createdAt: '2024-09-10T01:00:00', updatedAt: '2024-09-10T01:00:00'}
    ];

    // first posts retrieval should trigger an API request and populate cache
    service.getAllTopics().subscribe(
      (topics: Topic[]) => {
        expect(topics).toEqual(mockTopics);
      }
    );
    const retrievalRequest: TestRequest = mockHttpController.expectOne("api/topics");
    expect(retrievalRequest.request.method).toEqual("GET");
    retrievalRequest.flush(mockTopics);

    let hasBeenCalled: boolean = false;
    // shouldReload will return true one time only
    const spyOnShouldReload = jest.spyOn(service, 'shouldReload').mockImplementation(() => {const res = hasBeenCalled ? false : true; hasBeenCalled = true; return res;});

    // second posts retrieval when cache expired should trigger an API request and repopulate cache
    service.getAllTopics().subscribe(
      (topics: Topic[]) => {
        expect(topics).toEqual(mockTopics);
        done();
        
      }
    );
    const secondRetrievalRequest: TestRequest = mockHttpController.expectOne("api/topics");
    expect(secondRetrievalRequest.request.method).toEqual("GET");
    secondRetrievalRequest.flush(mockTopics);

    expect(spyOnShouldReload).toHaveBeenCalled();
  });
});
