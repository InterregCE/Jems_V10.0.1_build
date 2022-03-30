import {Injectable} from '@angular/core';
import {ProgrammeChecklistDTO, ProgrammeChecklistService} from '@cat/api';
import { Log } from '@common/utils/log';
import {Observable, Subject} from 'rxjs';
import {startWith, switchMap, take, tap} from 'rxjs/operators';

@Injectable()
export class ProgrammeChecklistListPageStore {

  checklists$: Observable<ProgrammeChecklistDTO[]>;

  private checklistsChanged$ = new Subject<void>();

  constructor(private checklistService: ProgrammeChecklistService) {
    this.checklists$ = this.checklists();
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
    return this.checklistsChanged$
      .pipe(
        startWith(null),
        switchMap(() => this.checklistService.getProgrammeChecklists()),
        tap(checklists => Log.info('Fetched checklists', this, checklists)),
      );
  }
}
