import {Injectable} from '@angular/core';
import {RoutingService} from '@common/services/routing.service';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {ChecklistInstanceDetailDTO, ChecklistInstanceDTO, ChecklistInstanceService, UserRoleDTO} from '@cat/api';
import {map, shareReplay, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {SecurityService} from '../../../../security/security.service';

@Injectable()
export class AssessmentAndDecisionChecklistPageStore {
  static CHECKLIST_DETAIL_PATH = 'assessmentAndDecision/checklist/';

  checklist$: Observable<ChecklistInstanceDetailDTO>;
  userCanConsolidate$: Observable<boolean>;
  checklistEditable$: Observable<boolean>;

  private updatedChecklist$ = new Subject<ChecklistInstanceDetailDTO>();

  constructor(private routingService: RoutingService,
              private checklistInstanceService: ChecklistInstanceService,
              private securityService: SecurityService,
              private permissionService: PermissionService) {
    this.checklist$ = this.checklist();
    this.userCanConsolidate$ = this.permissionService.hasPermission(UserRoleDTO.PermissionsEnum.ProjectAssessmentChecklistConsolidate);
    this.checklistEditable$ = this.checklistEditable();
  }

  updateChecklist(checklist: ChecklistInstanceDetailDTO): Observable<ChecklistInstanceDetailDTO> {
    return this.checklistInstanceService.updateChecklistInstance(checklist)
      .pipe(
        take(1),
        tap(() => this.updatedChecklist$.next(checklist)),
        tap(updated => Log.info('Updated checklist instance', this, updated))
      );
  }

  changeStatus(checklistId: number, status: ChecklistInstanceDTO.StatusEnum): Observable<ChecklistInstanceDTO> {
    return this.checklistInstanceService.changeChecklistStatus(checklistId, status)
      .pipe(
        take(1),
        tap(updated => Log.info('Changed checklist status', this, updated))
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
