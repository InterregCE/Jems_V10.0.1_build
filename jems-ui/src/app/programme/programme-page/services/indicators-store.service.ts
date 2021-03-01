import {Injectable} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';

@Injectable()
export class IndicatorsStore {
  private savedOutputIndicator$ = new ReplaySubject<string | null>(1);
  private savedResultIndicator$ = new ReplaySubject<string | null>(1);

  outputIndicator(): Observable<string | null> {
    return this.savedOutputIndicator$.asObservable();
  }

  resultIndicator(): Observable<string | null> {
    return this.savedResultIndicator$.asObservable();
  }
}
