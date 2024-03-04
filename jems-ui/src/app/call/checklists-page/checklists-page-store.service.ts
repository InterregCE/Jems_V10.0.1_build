import {Injectable} from '@angular/core';
import { CallChecklistDTO, CallService, PageCallChecklistDTO } from '@cat/api'
import { Log } from '@common/utils/log';
import {combineLatest, Observable, Subject} from 'rxjs';
import {filter, map, startWith, switchMap, tap} from 'rxjs/operators';
import {MatSort} from '@angular/material/sort';
import { BehaviorSubject } from 'rxjs';
import { CallStore } from '../services/call-store.service'
import { RoutingService } from '@common/services/routing.service'

@Injectable()
export class ChecklistsPageStore {

  callId$: Observable<number>;
  canEditCall$: Observable<boolean>;
  checklists$: Observable<CallChecklistDTO[]>;

  newSort$ = new BehaviorSubject<Partial<MatSort>>({});
  newRefresh$ = new Subject();

  constructor(private callService: CallService,
              private callStore: CallStore,
              private router: RoutingService) {
    this.callId$ = this.callStore.callId$;
    this.canEditCall$ = this.callStore.callIsEditable$;
    this.checklists$ = this.checklists();
  }

  private checklists(): Observable<Array<CallChecklistDTO>> {
    return combineLatest([
      this.callId$.pipe(filter(Boolean), map(Number)),
      this.newSort$.pipe(
          map(sort => sort?.direction ? [`${sort.active},${sort.direction}`] : undefined)
      ),
      this.newRefresh$.pipe(startWith(1)),
    ])
      .pipe(
        switchMap(([callId, sort, refresh]) => this.callService.getChecklists(callId, sort)),
        map(checklists => checklists.content),
        tap(checklists => Log.info('Fetched call checklists', this, checklists)),
      );
  }

  saveSelectedChecklists(checklistIds: number[]): Observable<any> {
    return this.callId$.pipe(
        switchMap(callId => this.callService.updateSelectedChecklists(callId, checklistIds)),
        tap(() => this.newRefresh$.next(1)),
        tap(saved => Log.info('Updated selected checklists for call:', this, saved))
    )
  }

}
