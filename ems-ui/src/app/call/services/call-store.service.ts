import {Injectable} from '@angular/core';
import {OutputCall} from '@cat/api'
import {Observable, ReplaySubject} from 'rxjs';

@Injectable()
export class CallStore {
  private publishedCall$ = new ReplaySubject<string | null>(1);

  callPublished(call: OutputCall): void {
    this.publishedCall$.next(call?.name);
    setTimeout(() => this.publishedCall$.next(null), 4000);
  }

  publishedCall(): Observable<string | null> {
    return this.publishedCall$.asObservable();
  }
}
