import { TestBed } from '@angular/core/testing';

import { DataService } from './data.service';
import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting, TestRequest } from '@angular/common/http/testing';
import { Topic } from '../models/topic.interface';
import { catchError } from 'rxjs';

describe('ApiService', () => {
  let service: DataService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ]
    });
    service = TestBed.inject(DataService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Vérifie qu'aucune requête non attendue n'a été envoyée
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should trigger a GET request and return expected data', () => {
    const mockData = { id: 1, title: ' Test topic', description: 'Test topic description' };
    
    service.get<typeof mockData>('topics').subscribe((data) => {
      expect(data).toEqual(mockData);
    });
  
    // checks that a GET request has been made
    const req:TestRequest = httpMock.expectOne('api/topics');
    expect(req.request.method).toBe('GET');
  
    req.flush(mockData);
  });

  it('shoult trigger a POST request and return expected data', () => {
    const mockResponse = { id: 1, title: 'Test article', content: 'Test content' };
    const payload = { content: 'Test content' };
  
    service.post<typeof mockResponse>('posts', payload).subscribe((data) => {
      expect(data).toEqual(mockResponse); 
    });
  
    // checks a POST request has been made
    const req = httpMock.expectOne('api/posts');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
  
    req.flush(mockResponse);
  });

  it('shoult trigger a PUT request and return expected data', () => {
    const mockResponse = { id: 1, title: 'Test article', content: 'Test content' };
    const payload = { content: 'Test content' };
  
    service.put<typeof mockResponse>('posts', payload).subscribe((data) => {
      expect(data).toEqual(mockResponse); 
    });
  
    // checks a PUT request has been made
    const req = httpMock.expectOne('api/posts');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(payload);
  
    req.flush(mockResponse);
  });

  it('shoult trigger a DELETE request and return expected data', () => {
    service.delete<null>('posts/1').subscribe((data) => {
      expect(data).toEqual(null); 
    });
  
    // checks a DELETE request has been made
    const req = httpMock.expectOne('api/posts/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('shoult handle Http error ', () => {
    service.get<Topic[]>('topics').subscribe({
      next: () => fail('Service should throw an error'),
      error:  (error) => {
        expect(error.status).toBe(500);
      }
    });
  
    // checks a POST request has been made
    const req = httpMock.expectOne('api/topics');
    expect(req.request.method).toBe('GET');
    req.flush('Not Found', { status: 500, statusText: 'Internal server error' });
  });

});
