import {Injectable} from '@angular/core';
import {ProgrammePriorityDTO, ProgrammePriorityService} from '@cat/api';
import {combineLatest, Observable, Subject} from 'rxjs';
import {startWith, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '../../../common/utils/log';

@Injectable()
export class ProgrammePrioritiesPageStore {

  priorities$: Observable<ProgrammePriorityDTO[]>;
  private priorityChanged$ = new Subject<void>();


  constructor(private programmePriorityService: ProgrammePriorityService) {
    this.priorities$ = this.priorities();
  }

  deletePriority(id: number): Observable<any> {
    return this.programmePriorityService._delete(id)
      .pipe(
        take(1),
        tap(() => this.priorityChanged$.next()),
        tap(() => Log.info('Deleted priority', this, id)),
      );
  }

  private priorities(): Observable<ProgrammePriorityDTO[]> {
    return combineLatest([
      this.priorityChanged$.pipe(startWith(null))
    ]).pipe(
       switchMap(() => this.programmePriorityService.get()
      .pipe(
        tap(priorities => Log.info('Fetched the priorities:', this, priorities)),
      )));
  }
}
