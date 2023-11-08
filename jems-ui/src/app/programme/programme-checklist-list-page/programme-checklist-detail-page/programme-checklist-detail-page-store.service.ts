import {Injectable} from '@angular/core';
import {ProgrammeChecklistDetailDTO, ProgrammeChecklistDTO, ProgrammeChecklistService} from '@cat/api';
import {RoutingService} from '@common/services/routing.service';
import {Log} from '@common/utils/log';
import {BehaviorSubject, merge, Observable, of, Subject} from 'rxjs';
import {map, mergeMap, switchMap, take, tap, withLatestFrom} from 'rxjs/operators';
import {ProgrammeEditableStateStore} from '../../programme-page/services/programme-editable-state-store.service';

@Injectable()
export class ProgrammeChecklistDetailPageStore {
  static readonly CHECKLIST_DETAIL_PATH = '/app/programme/checklists/';

  checklist$: Observable<ProgrammeChecklistDetailDTO>;
  isEditable$ = new BehaviorSubject<boolean>(true);

  private savedChecklist$ = new Subject<ProgrammeChecklistDetailDTO>();

  constructor(private programmeChecklistService: ProgrammeChecklistService,
              private routingService: RoutingService,
              private programmeEditableStateStore: ProgrammeEditableStateStore) {
    this.checklist$ = this.checklist();
  }

  saveChecklist(checklist: ProgrammeChecklistDetailDTO): Observable<ProgrammeChecklistDTO> {
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
        tap((saved: ProgrammeChecklistDetailDTO) => setTimeout(() => this.savedChecklist$.next(saved),3000)),
        tap(saved => Log.info('Checklist saved', this, saved))
      );
  }

  private checklist(): Observable<ProgrammeChecklistDetailDTO> {
    const initialChecklist$ =  this.checkListId()
      .pipe(
        switchMap(checklistId => checklistId
          ? this.programmeChecklistService.getProgrammeChecklistDetail(checklistId as number)
          : this.copyChecklist
        ),
        tap(checklist => Log.info('Fetched checklist', this, checklist)),
        withLatestFrom(this.programmeEditableStateStore.hasEditPermission$),
        tap(([checklist, hasEditPermission]) => this.isEditable$.next(hasEditPermission && !checklist.locked)),
        map(([checklist]) => checklist)
      );

    return merge(initialChecklist$, this.savedChecklist$);
  }

  private get copyChecklist(): Observable<ProgrammeChecklistDetailDTO> {
    return this.routingService.routeParameterChanges(
      ProgrammeChecklistDetailPageStore.CHECKLIST_DETAIL_PATH + 'copy', 'copyId'
    )
      .pipe(
        mergeMap((id: string | number | null) => id
          ? this.modifyChecklist(id as number)
          : of({} as ProgrammeChecklistDetailDTO)
        )
      )
  }

  private modifyChecklist(id: number): Observable<ProgrammeChecklistDetailDTO> {
    return this.programmeChecklistService.getProgrammeChecklistDetail(id)
      .pipe(
        map((checklist) => (
          {
            type: checklist.type,
            minScore: checklist.minScore,
            maxScore: checklist.maxScore,
            allowsDecimalScore: checklist.allowsDecimalScore,
            name: checklist.name + '-copy',
            components: checklist.components.map(it => {
              const { id, ...rest } = it
              return rest
            }),
          } as ProgrammeChecklistDetailDTO)
        )
      )
  }

  private checkListId(): Observable<any> {
    return this.routingService.routeParameterChanges(ProgrammeChecklistDetailPageStore.CHECKLIST_DETAIL_PATH, 'checklistId');
  }
}
