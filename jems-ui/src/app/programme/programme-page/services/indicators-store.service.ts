import {Injectable} from '@angular/core';
import {OutputIndicatorOutput, OutputIndicatorResult} from '@cat/api'
import {Observable, ReplaySubject} from 'rxjs';

@Injectable()
export class IndicatorsStore {
  private savedOutputIndicator$ = new ReplaySubject<string | null>(1);
  private savedResultIndicator$ = new ReplaySubject<string | null>(1);

  savedOutputIndicator(indicator: OutputIndicatorOutput): void {
    this.savedOutputIndicator$.next(indicator?.name);
    setTimeout(() => this.savedOutputIndicator$.next(null), 4000);
  }

  savedResultIndicator(indicator: OutputIndicatorResult): void {
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
