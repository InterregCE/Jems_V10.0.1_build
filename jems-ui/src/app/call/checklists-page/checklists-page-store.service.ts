import {Injectable} from '@angular/core';
import { CallChecklistDTO, CallService, PageCallChecklistDTO } from '@cat/api'
import { Log } from '@common/utils/log';
import {combineLatest, Observable, Subject} from 'rxjs';
import { filter, map, switchMap, tap } from 'rxjs/operators'
import {MatSort} from '@angular/material/sort';
import { BehaviorSubject } from 'rxjs';
import { CallStore } from '../services/call-store.service'
import { RoutingService } from '@common/services/routing.service'

@Injectable()
export class ChecklistsPageStore {

  checklists$ = new Subject<CallChecklistDTO[]>();
  canEditCall$: Observable<boolean>;
  callId$: Observable<number>;

  newSort$ = new BehaviorSubject<Partial<MatSort>>({});

  constructor(private callService: CallService,
              private callStore: CallStore,
              private router: RoutingService) {
    this.checklists().pipe(
        tap(checklists => this.checklists$.next(checklists.content))
    ).subscribe();
    this.canEditCall$ = this.callStore.callIsEditable$;
    this.callId$ = this.callId();
  }

  private checklists(): Observable<PageCallChecklistDTO> {
    return combineLatest([
      this.newSort$.pipe(
          map(sort => sort?.direction ? [`${sort.active},${sort.direction}`] : undefined)
      ),
      this.callStore.call$
    ])
      .pipe(
        filter(([_, call]) => !!call?.id),
        switchMap(([sort, call]) => this.callService.getChecklists(call.id, sort)),
        tap(checklists => Log.info('Fetched call checklists', this, checklists)),
      );
  }

  saveSelectedChecklists(checklistIds: number[]): Observable<CallChecklistDTO[]> {
    return this.callId$.pipe(
        switchMap(callId => this.callService.updateSelectedChecklists(callId, checklistIds).pipe(map(_ => callId))),
        switchMap(_ => this.checklists()),
        map(saved => saved.content),
        tap(saved => this.checklists$.next(saved)),
        tap(saved => Log.info('Updated selected checklists for call:', this, saved))
    )
  }

  private callId(): Observable<number> {
    return this.router.routeParameterChanges(CallStore.CALL_DETAIL_PATH, 'callId')
               .pipe(map(Number));
  }
}
