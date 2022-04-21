import {Injectable} from '@angular/core';
import {RoutingService} from '@common/services/routing.service';
import {merge, Observable, Subject} from 'rxjs';
import {ChecklistInstanceDetailDTO, ChecklistInstanceService} from '@cat/api';
import {switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';

@Injectable()
export class AssessmentAndDecisionChecklistPageStore {
  static CHECKLIST_DETAIL_PATH = 'assessmentAndDecision/checklist/';

  checklist$: Observable<ChecklistInstanceDetailDTO>;

  private updatedChecklist$ = new Subject<ChecklistInstanceDetailDTO>();

  constructor(private routingService: RoutingService,
              private checklistInstanceService: ChecklistInstanceService) {
    this.checklist$ = this.checklist();
  }

  updateChecklist(checklist: ChecklistInstanceDetailDTO): Observable<ChecklistInstanceDetailDTO> {
    return this.checklistInstanceService.updateChecklistInstance(checklist)
      .pipe(
        take(1),
        tap(() => this.updatedChecklist$.next(checklist)),
        tap(updated => Log.info('Updated checklist instance', this, updated))
      );
  }

  private checklist(): Observable<ChecklistInstanceDetailDTO> {
    const initialChecklist$ = this.routingService.routeParameterChanges(AssessmentAndDecisionChecklistPageStore.CHECKLIST_DETAIL_PATH, 'checklistId')
      .pipe(
        switchMap(checklistId => this.checklistInstanceService.getChecklistInstanceDetail(checklistId as number)),
        tap(checklist => Log.info('Fetched the checklist instance', this, checklist))
      );

    return merge(initialChecklist$, this.updatedChecklist$)
      .pipe(
        tap(checklist => checklist.components.sort((a, b) => a.position - b.position))
      );
  }

}
