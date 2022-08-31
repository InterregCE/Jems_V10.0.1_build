import {Injectable} from '@angular/core';
import {combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {
  ProjectPartnerControlReportChangeDTO,
  ProjectPartnerControlReportDTO,
  ProjectPartnerDetailDTO,
  ProjectPartnerReportIdentificationService,
  ProjectPartnerReportSummaryDTO,
  ProjectPartnerService
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
  private updatedControlReport$ = new Subject<ProjectPartnerControlReportDTO>();
  private partnerId: number;
  private projectId: number;

  readonly fullControlReportView$ = combineLatest([
    this.partnerReportDetailPageStore.partnerReportPageStore.userCanViewReports$,
    this.partnerReportDetailPageStore.partnerReportPageStore.userCanEditReports$,
    this.partnerReportDetailPageStore.partnerReportPageStore.institutionUserCanViewControlReports$,
    this.partnerReportDetailPageStore.partnerReportPageStore.institutionUserCanEditControlReports$
  ]).pipe(
    map(([viewRightsForReport, editRightsForReport, viewRightsFromInstitution, editRightsFromInstitution]) =>
      !(editRightsForReport || viewRightsForReport) || viewRightsFromInstitution || editRightsFromInstitution
    )
  );

  readonly limitedControlReportView$ = combineLatest([
    this.partnerReportDetailPageStore.partnerReportPageStore.userCanViewReports$,
    this.partnerReportDetailPageStore.partnerReportPageStore.userCanEditReports$,
    this.partnerReportDetailPageStore.partnerReportPageStore.institutionUserCanViewControlReports$,
    this.partnerReportDetailPageStore.partnerReportPageStore.institutionUserCanEditControlReports$
  ]).pipe(
    map(([viewRightsForReport, editRightsForReport, viewRightsFromInstitution, editRightsFromInstitution]) =>
      (viewRightsForReport || editRightsForReport) && !viewRightsFromInstitution && !editRightsFromInstitution
    )
  );

  constructor(private routingService: RoutingService,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore,
              private reportPageStore: PartnerReportPageStore,
              private projectStore: ProjectStore,
              private reportIdentificationService: ProjectPartnerReportIdentificationService,
              private partnerService: ProjectPartnerService,) {
    this.partnerControlReport$ = this.partnerControlReport();
    this.controlReportEditable$ = this.controlReportEditable();
    this.partner$ = this.partner();
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
      tap(([partnerId, projectId, controlReport]) => {
        this.partnerId = Number(partnerId);
        this.projectId = projectId;
      }),
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
}
