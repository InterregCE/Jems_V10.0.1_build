import {Injectable} from '@angular/core';
import {ProgrammeChecklistDTO, ProgrammeChecklistService} from '@cat/api';
import { Log } from '@common/utils/log';
import {combineLatest, Observable, Subject} from 'rxjs';
import {map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {ProgrammeEditableStateStore} from '../programme-page/services/programme-editable-state-store.service';
import {MatSort} from '@angular/material/sort';
import {Tables} from '@common/utils/tables';
import { BehaviorSubject } from 'rxjs';

@Injectable()
export class ProgrammeChecklistListPageStore {

  defaultSort: Partial<MatSort> = {active: 'lastModificationDate', direction: 'desc'};

  checklists$: Observable<ProgrammeChecklistDTO[]>;
  canEditProgramme$: Observable<boolean>;

  newSort$ = new BehaviorSubject<Partial<MatSort>>(this.defaultSort);

  private checklistsChanged$ = new Subject<void>();

  constructor(private checklistService: ProgrammeChecklistService,
              private programmeEditableStateStore: ProgrammeEditableStateStore) {
    this.checklists$ = this.checklists();
    this.canEditProgramme$ = this.programmeEditableStateStore.hasEditPermission$;
  }

  deleteChecklist(id: number): Observable<any> {
    return this.checklistService.deleteChecklist(id)
      .pipe(
        take(1),
        tap(() => this.checklistsChanged$.next()),
        tap(() => Log.info('Deleted checklist', this, id)),
      );
  }

  private checklists(): Observable<ProgrammeChecklistDTO[]> {
    return combineLatest([
      this.checklistsChanged$.pipe(startWith(null)),
      this.newSort$.pipe(
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => [`${sort.active},${sort.direction}`])
      )
    ])
      .pipe(
        switchMap(([_, sort]) => this.checklistService.getProgrammeChecklists(sort)),
        tap(checklists => Log.info('Fetched checklists', this, checklists)),
      );
  }
}
