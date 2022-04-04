import {Injectable} from '@angular/core';
import {ProgrammeChecklistDetailDTO, ProgrammeChecklistDTO, ProgrammeChecklistService} from '@cat/api';
import {RoutingService} from '@common/services/routing.service';
import {Log} from '@common/utils/log';
import {Observable, of, Subject} from 'rxjs';
import {switchMap, take, tap} from 'rxjs/operators';
import {ProgrammeEditableStateStore} from '../../programme-page/services/programme-editable-state-store.service';

@Injectable()
export class ProgrammeChecklistDetailPageStore {
  static readonly CHECKLIST_DETAIL_PATH = '/app/programme/checklists/';

  checklist$: Observable<ProgrammeChecklistDetailDTO>;
  canEditProgramme$: Observable<boolean>;

  private savedChecklist$ = new Subject<ProgrammeChecklistDetailDTO>();

  constructor(private programmeChecklistService: ProgrammeChecklistService,
              private routingService: RoutingService,
              private programmeEditableStateStore: ProgrammeEditableStateStore) {
    this.checklist$ = this.checklist();
    this.canEditProgramme$ = this.programmeEditableStateStore.hasEditPermission$;
  }

  private checklist(): Observable<ProgrammeChecklistDetailDTO> {
    return this.checkListId()
      .pipe(
        switchMap(checklistId => checklistId
          ? this.programmeChecklistService.getProgrammeChecklistDetail(checklistId as number)
          : of({} as ProgrammeChecklistDetailDTO)
        ),
        tap(checklist => Log.debug('Checklist', checklist))
      );
  }

  public saveChecklist(checklist: ProgrammeChecklistDetailDTO): Observable<ProgrammeChecklistDTO> {
    return this.checkListId()
      .pipe(
        take(1),
        switchMap(id => id
          ? this.programmeChecklistService.updateProgrammeChecklist(checklist)
          : this.programmeChecklistService.createProgrammeChecklist(checklist)
            .pipe(
              tap(createdChecklist => this.routingService.navigate(
                [ProgrammeChecklistDetailPageStore.CHECKLIST_DETAIL_PATH + createdChecklist.id]
                )
              )
            )
        ),
        tap((saved: ProgrammeChecklistDetailDTO) => this.savedChecklist$.next(saved)),
        tap(saved => Log.info('Checklist saved', this, saved))
      );
  }

  private checkListId(): Observable<any> {
    return this.routingService.routeParameterChanges(ProgrammeChecklistDetailPageStore.CHECKLIST_DETAIL_PATH, 'checklistId');
  }
}
