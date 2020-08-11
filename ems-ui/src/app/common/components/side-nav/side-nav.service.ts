
import { Injectable } from '@angular/core';
import {Observable, ReplaySubject, Subject} from 'rxjs';
import {take, tap} from 'rxjs/operators';
import {Log} from '../../utils/log';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';

@Injectable()
export class SideNavService {
  private headlines$ = new ReplaySubject<HeadlineRoute[]>();

  setHeadlines(destroyed$: Subject<any>, newHeadlines: HeadlineRoute[]): void {
    setTimeout(() =>  this.headlines$.next(newHeadlines), 50);
    Log.info(' setting headlines', this, this.headlines$);

    if (!destroyed$)
      return;

    destroyed$
      .pipe(
        take(1),
        tap(() => Log.info('setting headlines', this, []))
      )
      .subscribe(() => this.headlines$.next([]))
  }

  getHeadlines(): Observable<HeadlineRoute[]>{
    return this.headlines$.asObservable();
  }
}
