
import { Injectable } from '@angular/core';
import {Observable, ReplaySubject, Subject} from 'rxjs';
import {take, tap} from 'rxjs/operators';
import {Log} from '../../utils/log';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';

@Injectable()
export class SideNavService {
  private headlines$ = new ReplaySubject<HeadlineRoute[]>();
  private alertStatus$ = new ReplaySubject<boolean>()

  setHeadlines(destroyed$: Subject<any>, newHeadlines: HeadlineRoute[]): void {
    setTimeout(() =>  this.headlines$.next(newHeadlines), 50);
    Log.debug('Setting headlines', this, this.headlines$);

    if (!destroyed$)
      return;

    destroyed$
      .pipe(
        take(1),
        tap(() => Log.debug('Setting headlines', this, [])),
        tap(() => this.headlines$.next([])),
        tap(() =>this.alertStatus$.next())
      ).subscribe()
  }

  getHeadlines(): Observable<HeadlineRoute[]>{
    return this.headlines$.asObservable();
  }

  setAlertStatus(newAlertStatus: boolean): void {
    setTimeout(() =>  this.alertStatus$.next(newAlertStatus), 50);
    Log.debug('Setting alert status', this, this.alertStatus$);
  }

  getAlertStatus(): Observable<boolean>{
    return this.alertStatus$.asObservable();
  }
}
