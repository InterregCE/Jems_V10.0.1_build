import {Injectable} from '@angular/core';
import {RoutingService} from '@common/services/routing.service';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {ChecklistInstanceDetailDTO, ChecklistInstanceDTO, ControlChecklistInstanceService} from '@cat/api';
import {map, shareReplay, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {SecurityService} from '../../../../../../security/security.service';
import {ActivatedRoute} from '@angular/router';

@Injectable()
export class PartnerControlReportControlChecklistPageStore {
  static CHECKLIST_DETAIL_PATH = `controlReport/controlChecklistsTab/checklist/`;

  checklist$: Observable<ChecklistInstanceDetailDTO>;
  checklistEditable$: Observable<boolean>;

  partnerId = Number(this.routingService.getParameter(this.activatedRoute, 'partnerId'));
  reportId = Number(this.routingService.getParameter(this.activatedRoute, 'reportId'));

  private updatedChecklist$ = new Subject<ChecklistInstanceDetailDTO>();

  constructor(private routingService: RoutingService,
              private checklistInstanceService: ControlChecklistInstanceService,
              private securityService: SecurityService,
              private activatedRoute: ActivatedRoute) {
    this.checklist$ = this.checklist();
    this.checklistEditable$ = this.checklistEditable();
  }

  updateChecklist(partnerId: number, reportId: number, checklist: ChecklistInstanceDetailDTO): Observable<ChecklistInstanceDetailDTO> {
    return this.checklistInstanceService.updateControlChecklistInstance(partnerId, reportId, checklist)
      .pipe(
        take(1),
        tap(() => this.updatedChecklist$.next(checklist)),
        tap(updated => Log.info('Updated control checklist instance', this, updated))
      );
  }

  changeStatus(partnerId: number, reportId: number, checklistId: number, status: ChecklistInstanceDTO.StatusEnum): Observable<ChecklistInstanceDTO> {
    return this.checklistInstanceService.changeControlChecklistStatus(checklistId, partnerId, reportId,  status)
      .pipe(
        take(1),
        tap(updated => Log.info('Changed control checklist status', this, updated))
      );
  }

  private checklist(): Observable<ChecklistInstanceDetailDTO> {
    const initialChecklist$ = combineLatest([
      this.routingService.routeParameterChanges(PartnerControlReportControlChecklistPageStore.CHECKLIST_DETAIL_PATH, 'checklistId')
      ]
    ).pipe(
        switchMap(([ checklistId]) => {
          return this.checklistInstanceService.getControlChecklistInstanceDetail(checklistId as number, this.partnerId, this.reportId);
        }),
        tap(checklist => Log.info('Fetched the control checklist instance', this, checklist))
      );

    return merge(initialChecklist$, this.updatedChecklist$)
      .pipe(
        tap(checklist => checklist.components.sort((a, b) => a.position - b.position)),
        shareReplay()
      );
  }

  private checklistEditable(): Observable<boolean> {
    return combineLatest([this.checklist$, this.securityService.currentUserDetails])
      .pipe(
        map(([checklist, user]) =>
          checklist.status === ChecklistInstanceDetailDTO.StatusEnum.DRAFT && user?.email === checklist.creatorEmail)
      );
  }
}
