import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {
  ControllerInstitutionsApiService,
  UserSimpleDTO,
  PartnerUserCollaboratorDTO,
  ProjectPartnerControlReportChangeDTO,
  ProjectPartnerControlReportDTO,
  ProjectPartnerDetailDTO,
  ProjectPartnerReportIdentificationService,
  ProjectPartnerReportSummaryDTO,
  ProjectPartnerService,
  ProjectPartnerUserCollaboratorService,
} from '@cat/api';
import {RoutingService} from '@common/services/routing.service';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {catchError, map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {ProjectPaths} from '@project/common/project-util';
import {Log} from '@common/utils/log';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';

@Injectable({providedIn: 'root'})
export class PartnerControlReportStore {

  partnerControlReport$: Observable<ProjectPartnerControlReportDTO>;
  controlReportEditable$: Observable<boolean>;
  partner$: Observable<ProjectPartnerDetailDTO>;
  controlInstitutionUsers$: Observable<UserSimpleDTO[]>;

  partnerId$: Observable<number>;
  reportId$: Observable<number>;

  private updatedControlReport$ = new Subject<ProjectPartnerControlReportDTO>();

  readonly fullControlReportView$ = combineLatest([
    this.partnerReportDetailPageStore.partnerReportPageStore.userCanViewReport$,
    this.partnerReportDetailPageStore.partnerReportPageStore.userCanEditReport$,
    this.partnerReportDetailPageStore.partnerReportPageStore.institutionUserCanViewControlReports$,
    this.partnerReportDetailPageStore.partnerReportPageStore.institutionUserCanEditControlReports$
  ]).pipe(
    map(([viewRightsForReport, editRightsForReport, viewRightsFromInstitution, editRightsFromInstitution]) =>
      !(editRightsForReport || viewRightsForReport) || viewRightsFromInstitution || editRightsFromInstitution
    )
  );

  readonly limitedControlReportView$ = combineLatest([
    this.partnerReportDetailPageStore.partnerReportPageStore.userCanViewReport$,
    this.partnerReportDetailPageStore.partnerReportPageStore.userCanEditReport$,
    this.partnerReportDetailPageStore.partnerReportPageStore.institutionUserCanViewControlReports$,
    this.partnerReportDetailPageStore.partnerReportPageStore.institutionUserCanEditControlReports$
  ]).pipe(
    map(([viewRightsForReport, editRightsForReport, viewRightsFromInstitution, editRightsFromInstitution]) =>
      (viewRightsForReport || editRightsForReport) && !viewRightsFromInstitution && !editRightsFromInstitution
    )
  );

  readonly canEditControlReport$ = combineLatest([
    this.partnerReportLevel()
      .pipe(map(level => level === PartnerUserCollaboratorDTO.LevelEnum.EDIT)),
    this.partnerReportDetailPageStore.partnerReportPageStore.institutionUserCanEditControlReports$,
  ]).pipe(
    map(([editRightsForReport, editRightsFromInstitution]) =>
      editRightsForReport || editRightsFromInstitution
    ),
  );

  constructor(
    private routingService: RoutingService,
    private partnerReportDetailPageStore: PartnerReportDetailPageStore,
    private reportPageStore: PartnerReportPageStore,
    private projectStore: ProjectStore,
    private reportIdentificationService: ProjectPartnerReportIdentificationService,
    private partnerService: ProjectPartnerService,
    private partnerUserCollaboratorService: ProjectPartnerUserCollaboratorService,
    private controllerInstitutionsService: ControllerInstitutionsApiService
  ) {
    this.partnerControlReport$ = this.partnerControlReport();
    this.controlReportEditable$ = this.controlReportEditable();
    this.partner$ = this.partner();
    this.partnerId$ = this.partnerId();
    this.reportId$ = this.reportId();
    this.controlInstitutionUsers$ = this.controlInstitutionUsers();
  }

  private controlReportEditable(): Observable<boolean> {
    return combineLatest([
      this.reportPageStore.institutionUserCanEditControlReports$,
      this.partnerReportDetailPageStore.reportStatus$
    ])
      .pipe(
        map(([canEdit, status]) => canEdit && status === ProjectPartnerReportSummaryDTO.StatusEnum.InControl)
      );
  }

  private partnerReportLevel(): Observable<PartnerUserCollaboratorDTO.LevelEnum | undefined> {
    return combineLatest([
      this.projectStore.projectId$,
      this.routingService.routeParameterChanges(PartnerReportPageStore.PARTNER_REPORT_DETAIL_PATH, 'partnerId')
        .pipe(map(id => Number(id))),
    ]).pipe(
      switchMap(([projectId, partnerId]) =>
        combineLatest([
          this.partnerUserCollaboratorService.listCurrentUserPartnerCollaborations(projectId),
          of(partnerId),
        ])
      ),
      map(([collaborations, partnerId]) => collaborations.find(collab => collab.partnerId === partnerId)?.level),
    );
  }

  private partnerControlReport(): Observable<ProjectPartnerControlReportDTO> {
    const initialReport$ = combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerReportDetailPageStore.partnerReportId$,
      this.projectStore.projectId$,
      this.partnerReportDetailPageStore.reportStatus$,
      this.fullControlReportView$
    ]).pipe(
      switchMap(([partnerId, reportId, projectId, status, isAllowed]) => !!partnerId && !!projectId && !!reportId && status !== 'Draft' && status !== 'Submitted' && isAllowed
        ? this.reportIdentificationService.getControlIdentification(Number(partnerId), Number(reportId))
          .pipe(
            catchError(() => {
              this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, projectId, 'reporting']);
              return of({} as ProjectPartnerControlReportDTO);
            })
          )
        : of({} as ProjectPartnerControlReportDTO)
      ),
      tap(report => Log.info('Fetched the partner control report:', this, report)),
    );

    return merge(initialReport$, this.updatedControlReport$)
      .pipe(
        shareReplay(1)
      );
  }

  private controlInstitutionUsers(): Observable<UserSimpleDTO[]> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.partnerControlReport(),
      this.projectStore.projectId$,
    ]).pipe(
      switchMap(([partnerId, control, projectId]) => !!partnerId && !!control.designatedController?.controlInstitutionId && !!projectId
        ? this.controllerInstitutionsService.getUsersByControllerInstitutionId(Number(control.designatedController?.controlInstitutionId), Number(partnerId))
          .pipe(
            catchError(() => {
              this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, projectId, 'reporting']);
              return of([{}] as UserSimpleDTO[]);
            })
          )
        : of([{}] as UserSimpleDTO[])
      ),
      tap(controlUsers => Log.info('Fetched the control institution users:', this, controlUsers)),
    );
  }

  public saveIdentification(identification: ProjectPartnerControlReportChangeDTO): Observable<ProjectPartnerControlReportDTO> {
    return combineLatest([
      this.partnerReportDetailPageStore.partnerId$,
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId'),
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.reportIdentificationService.updateControlIdentification(Number(partnerId), Number(reportId), identification)),
      tap(data => Log.info('Updated identification for control report', this, data)),
      tap(data => this.updatedControlReport$.next(data)),
    );
  }

  private partner(): Observable<ProjectPartnerDetailDTO> {
    return combineLatest([
      this.routingService.routeParameterChanges(PartnerReportPageStore.PARTNER_REPORT_DETAIL_PATH, 'partnerId'),
      this.projectStore.projectId$,
      this.partnerControlReport$
    ]).pipe(
      switchMap(([partnerId, projectId, controlReport]) => partnerId && projectId
        ? this.partnerService.getProjectPartnerById(Number(partnerId), controlReport.linkedFormVersion)
          .pipe(
            catchError(() => {
              this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, projectId, 'reporting']);
              return of({} as ProjectPartnerDetailDTO);
            })
          )
        : of({} as ProjectPartnerDetailDTO)
      ),
      tap(partner => Log.info('Fetched the programme partner:', this, partner)),
      shareReplay(1)
    );
  }

  private partnerId(): Observable<number> {
    return this.routingService.routeParameterChanges(PartnerReportPageStore.PARTNER_REPORT_DETAIL_PATH, 'partnerId')
        .pipe(map(id => Number(id)));
  }

  private reportId(): Observable<number> {
    return this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(id => Number(id)));
  }
}
