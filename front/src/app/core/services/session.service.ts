import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  constructor() { }

  public $isLogged(): Observable<boolean> {
    return of(false);
  }
}
