import {Injectable} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {
  OutputIndicatorUpdateRequestDTO,
  ResultIndicatorUpdateRequestDTO
} from '@cat/api';

@Injectable()
export class IndicatorsStore {
  private savedOutputIndicator$ = new ReplaySubject<string | null>(1);
  private savedResultIndicator$ = new ReplaySubject<string | null>(1);

  savedOutputIndicator(indicator: OutputIndicatorUpdateRequestDTO): void {
    this.savedOutputIndicator$.next(indicator?.name);
    setTimeout(() => this.savedOutputIndicator$.next(null), 4000);
  }

  savedResultIndicator(indicator: ResultIndicatorUpdateRequestDTO): void {
    this.savedResultIndicator$.next(indicator?.name);
    setTimeout(() => this.savedResultIndicator$.next(null), 4000);
  }

  outputIndicator(): Observable<string | null> {
    return this.savedOutputIndicator$.asObservable();
  }

  resultIndicator(): Observable<string | null> {
    return this.savedResultIndicator$.asObservable();
  }
}
