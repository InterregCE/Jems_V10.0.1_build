import {Injectable} from '@angular/core';
import {RoutingService} from '@common/services/routing.service';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {ChecklistInstanceDetailDTO, ChecklistInstanceDTO, ControlChecklistInstanceService, ControllerInstitutionsApiService, UserRoleCreateDTO} from '@cat/api';
import {filter, map, shareReplay, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {SecurityService} from '../../../../../../security/security.service';
import {ActivatedRoute} from '@angular/router';
import {PartnerControlReportStore} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';
import {ReportUtil} from '@project/common/report-util';
import {PermissionService} from '../../../../../../security/permissions/permission.service';
import {PartnerReportDetailPageStore} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@Injectable()
export class PartnerControlReportControlChecklistPageStore {
  static CHECKLIST_DETAIL_PATH = `controlReport/controlChecklistsTab/checklist/`;

  checklistId$: Observable<number>;
  checklist$: Observable<ChecklistInstanceDetailDTO>;
  userCanEditChecklist$: Observable<boolean>;
  userCanReturnChecklist$: Observable<boolean>;
  reportEditable$: Observable<boolean>;

  partnerId = Number(this.routingService.getParameter(this.activatedRoute, 'partnerId'));
  reportId = Number(this.routingService.getParameter(this.activatedRoute, 'reportId'));

  private updatedChecklist$ = new Subject<ChecklistInstanceDetailDTO>();
  private isAfterControl$: Observable<boolean>;
  private canEditChecklistBasedOnReportControlStatus$: Observable<boolean>;

  constructor(private routingService: RoutingService,
              private checklistInstanceService: ControlChecklistInstanceService,
              private securityService: SecurityService,
              private activatedRoute: ActivatedRoute,
              private reportControlStore: PartnerControlReportStore,
              private permissionService: PermissionService,
              private controllerInstitutionService: ControllerInstitutionsApiService,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore,
  ) {
    this.checklistId$ = this.checklistId();
    this.checklist$ = this.checklist();
    this.reportEditable$ = this.reportControlStore.controlReportEditable$;

    this.isAfterControl$ = this.isAfterControl();
    this.canEditChecklistBasedOnReportControlStatus$ = this.canEditChecklistBasedOnReportControlStatus();

    this.userCanEditChecklist$ = this.userCanEditChecklist();
    this.userCanReturnChecklist$ = this.userCanReturnChecklist();
  }

  updateChecklist(checklist: ChecklistInstanceDetailDTO): Observable<ChecklistInstanceDetailDTO> {
    return this.checklistInstanceService.updateControlChecklistInstance(this.partnerId, this.reportId, checklist)
      .pipe(
        take(1),
        tap(() => this.updatedChecklist$.next(checklist)),
        tap(updated => Log.info('Updated control checklist instance', this, updated))
      );
  }

  changeStatus(checklistId: number, status: ChecklistInstanceDTO.StatusEnum): Observable<ChecklistInstanceDTO> {
    return this.checklistInstanceService.changeControlChecklistStatus(checklistId, this.partnerId, this.reportId, status)
      .pipe(
        take(1),
        tap(updated => Log.info('Changed control checklist status', this, updated))
      );
  }

  private checklistId(): Observable<number> {
    return this.routingService.routeParameterChanges(PartnerControlReportControlChecklistPageStore.CHECKLIST_DETAIL_PATH, 'checklistId')
      .pipe(
        filter(Boolean),
        map(Number)
      );
  }

  private checklist(): Observable<ChecklistInstanceDetailDTO> {
    const initialChecklist$ = this.checklistId$.pipe(
      switchMap(checklistId => this.checklistInstanceService.getControlChecklistInstanceDetail(checklistId, this.partnerId, this.reportId)),
      tap(checklist => Log.info('Fetched the control checklist instance', this, checklist))
    );

    return merge(initialChecklist$, this.updatedChecklist$)
      .pipe(
        tap(checklist => checklist.components.sort((a, b) => a.position - b.position)),
        shareReplay()
      );
  }

  private userCanEditChecklist(): Observable<boolean> {
    return combineLatest([
      this.checklist$,
      this.securityService.currentUserDetails,
      this.reportControlStore.controlReportEditable$,
      this.reportControlStore.partnerControlReport$.pipe(map(controlReport => controlReport.reportControlEnd)),
      this.reportControlStore.controlReportCertifiedReOpened$,
      this.canEditChecklistBasedOnReportControlStatus$,
    ])
      .pipe(
        map(([checklist, user, reportEditable, reportControlEnd, controlReportCertifiedReOpened, checklistEditableBasedOnStatus]) =>
            checklist.status === ChecklistInstanceDetailDTO.StatusEnum.DRAFT
            && user?.email === checklist.creatorEmail
            && (
              controlReportCertifiedReOpened
                ? checklist.createdAt > reportControlEnd
                : ((checklist.createdAt > reportControlEnd && checklistEditableBasedOnStatus) || reportEditable)
            )
        ),
      );
  }

  private userCanReturnChecklist(): Observable<boolean> {
    return combineLatest([
      this.checklist$.pipe(map(checklist => checklist.status === ChecklistInstanceDetailDTO.StatusEnum.FINISHED)),
      this.canEditChecklistBasedOnReportControlStatus$,
      this.isAfterControl$
    ]).pipe(
      map(([checklistFinished, canEditChecklist, isAfterControlChecklist]) =>
        checklistFinished && canEditChecklist && isAfterControlChecklist)
    );
  }

  private canEditChecklistBasedOnReportControlStatus(): Observable<boolean> {
    return combineLatest([
      this.controllerInstitutionService.getControllerUserAccessLevelForPartner(this.partnerId),
      this.partnerReportDetailPageStore.reportStatus$,
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportingChecklistAfterControl),
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportingView)
    ])
      .pipe(
        map(([level, reportStatus, canEditChecklistsAfterControl, canViewReport]) =>
          (level === 'Edit' && ReportUtil.isControlReportOpen(reportStatus) || ReportUtil.isControlCertifiedReOpened(reportStatus))
          ||
          ((level === 'Edit' || level === 'View' || canViewReport)
            && ReportUtil.controlFinalized(reportStatus)
            && canEditChecklistsAfterControl)
        ),
      );
  }

  private isAfterControl(): Observable<boolean> {
    return combineLatest([
      this.checklist$.pipe(map(checklist => checklist.createdAt)),
      this.reportControlStore.partnerControlReport$.pipe(map(control => control.reportControlEnd)),
    ]).pipe(
      map(([createdAt, controlFinalizedDate]) => controlFinalizedDate ? createdAt > controlFinalizedDate : true),
    );
  }
}
