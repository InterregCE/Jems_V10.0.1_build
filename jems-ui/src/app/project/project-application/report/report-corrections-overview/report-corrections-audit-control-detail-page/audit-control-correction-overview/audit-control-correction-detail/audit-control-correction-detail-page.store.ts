import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {RoutingService} from '@common/services/routing.service';
import {UntilDestroy} from '@ngneat/until-destroy';
import {
  CorrectionAvailablePartnerDTO,
  ProjectAuditAndControlService,
  ProjectCorrectionService,
  ProjectAuditControlCorrectionDTO,
  ProjectCorrectionFinancialDescriptionDTO,
  ProjectCorrectionFinancialDescriptionService,
  ProjectCorrectionFinancialDescriptionUpdateDTO,
  ProjectCorrectionIdentificationUpdateDTO,
  UserRoleDTO, PageCorrectionCostItemDTO,
  AuditControlCorrectionDTO,
} from '@cat/api';
import {catchError, filter, map, shareReplay, switchMap, tap} from 'rxjs/operators';
import {ProjectPaths} from '@project/common/project-util';
import {Log} from '@common/utils/log';
import {
  ReportCorrectionsAuditControlDetailPageStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/report-corrections-audit-control-detail-page.store';
import {PermissionService} from '../../../../../../../security/permissions/permission.service';
import {
  AuditControlCorrectionStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/audit-control-correction-overview/audit-control-correction-store.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;
import {Tables} from '@common/utils/tables';

@UntilDestroy()
@Injectable({
  providedIn: 'root'
})
export class AuditControlCorrectionDetailPageStore {

  AUDIT_CONTROL_CORRECTION_PATH = 'correction/';
  private static readonly COST_ITEMS_DEFAULT_PAGE_SIZE = 5;

  projectId$: Observable<number>;
  auditControlId$: Observable<number>;
  correctionId$: Observable<string | number | null>;
  correction$: Observable<ProjectAuditControlCorrectionDTO>;
  canEdit$: Observable<boolean>;
  canClose$: Observable<boolean>;
  correctionPartnerData$: Observable<CorrectionAvailablePartnerDTO[]>;
  pastCorrections$: Observable<AuditControlCorrectionDTO[]>;
  updatedCorrection$ = new Subject<ProjectAuditControlCorrectionDTO>();
  financialDescription$: Observable<ProjectCorrectionFinancialDescriptionDTO>;
  savedFinancialDescription$ = new Subject<ProjectCorrectionFinancialDescriptionDTO>();

  costItemsPage$: Observable<PageCorrectionCostItemDTO>;
  costItemsPageSize$ = new BehaviorSubject<number>(AuditControlCorrectionDetailPageStore.COST_ITEMS_DEFAULT_PAGE_SIZE);
  costItemsPageIndex$ = new BehaviorSubject<number>(Tables.DEFAULT_INITIAL_PAGE_INDEX);
  refreshScopeLimitationData$ = new BehaviorSubject(null);

  availableProcurements$: Observable<Map<number, string>>;

  constructor(
    private routingService: RoutingService,
    private reportCorrectionsAuditControlDetailPageStore: ReportCorrectionsAuditControlDetailPageStore,
    private projectAuditControlCorrectionService: ProjectCorrectionService,
    private permissionService: PermissionService,
    private auditControlCorrectionStore: AuditControlCorrectionStore,
    private projectAuditAndControlService: ProjectAuditAndControlService,
    private projectCorrectionFinancialDescriptionService: ProjectCorrectionFinancialDescriptionService
  ) {
    this.projectId$ = this.reportCorrectionsAuditControlDetailPageStore.projectId$.pipe(filter(Boolean), map(Number));
    this.auditControlId$ = this.reportCorrectionsAuditControlDetailPageStore.auditControlId$.pipe(filter(Boolean), map(Number));
    this.correctionId$ = this.correctionId();
    this.correction$ = this.correction();
    this.correctionPartnerData$ = this.correctionPartnerData();
    this.pastCorrections$ = this.pastCorrections();
    this.financialDescription$ = this.financialDescription();
    this.canEdit$ = this.canEdit();
    this.canClose$ = this.canClose();
    this.costItemsPage$ = this.correctionAvailableCostItems();
    this.availableProcurements$ = this.correctionAvailableProcurements();
  }

  saveCorrection(id: number, correctionData: ProjectCorrectionIdentificationUpdateDTO): Observable<ProjectAuditControlCorrectionDTO> {
    return combineLatest([
      this.auditControlId$,
      this.projectId$,
    ]).pipe(
        switchMap(([auditControlId, projectId]) =>
          this.projectAuditControlCorrectionService.updateCorrectionIdentification(auditControlId, id, projectId, correctionData)
        ),
        tap(correction => this.updatedCorrection$.next(correction)),
        tap(correction => this.refreshScopeLimitationData$.next(null)),
        tap(correction => Log.info('Updated correction', this, correction))
    );
  }

  private correctionId(): Observable<string | number | null> {
    return this.routingService.routeParameterChanges(this.AUDIT_CONTROL_CORRECTION_PATH, 'correctionId');
  }

  private correctionPartnerData(): Observable<CorrectionAvailablePartnerDTO[]> {
    return this.projectId$.pipe(
      switchMap(projectId =>
        this.projectAuditAndControlService.getPartnerAndPartnerReportData(projectId)
      ),
      tap(correctionPartnerData => Log.info('Fetched correction partner data: ', this, correctionPartnerData))
    );
  }

  private pastCorrections(): Observable<AuditControlCorrectionDTO[]> {
    return combineLatest([
      this.auditControlId$,
      this.projectId$,
      this.correctionId$.pipe(filter(Boolean), map(Number)),
    ]).pipe(
      switchMap(([auditControlId, projectId, correctionId]) =>
        this.projectAuditControlCorrectionService.getPreviousClosedCorrections(auditControlId, correctionId, projectId)
      ),
      tap(correctionPartnerData => Log.info('Fetched past corrections: ', this, correctionPartnerData))
    );
  }

  private correction(): Observable<ProjectAuditControlCorrectionDTO> {
    const initialCorrection = combineLatest([
      this.auditControlId$,
      this.projectId$,
      this.correctionId$.pipe(filter(Boolean), map(Number)),
    ]).pipe(
      switchMap(([auditControlId, projectId, correctionId]) =>
        correctionId
          ? this.projectAuditControlCorrectionService.getProjectAuditCorrection(auditControlId, correctionId, projectId).pipe(
            catchError(() => {
              this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, projectId]);
              return of({} as ProjectAuditControlCorrectionDTO);
            })
          )
          : of({} as ProjectAuditControlCorrectionDTO)
      ),
      tap((correction) => Log.info('Fetched correction', this, correction)),
      shareReplay(1),
    );

    return merge(initialCorrection, this.updatedCorrection$);
  }


  private financialDescription(): Observable<ProjectCorrectionFinancialDescriptionDTO> {
    const initialData$ = combineLatest([
      this.projectId$,
      this.auditControlId$,
      this.correctionId$.pipe(filter(Boolean), map(Number)),
    ])
      .pipe(
        switchMap(([projectId, auditControlId, correctionId]) =>
          this.projectCorrectionFinancialDescriptionService.getCorrectionFinancialDescription(auditControlId, correctionId, projectId)),
      );

    return merge(initialData$, this.savedFinancialDescription$)
      .pipe(shareReplay(1));
  }

  saveFinancialDescription(correctionId: number, financialDescriptionData: ProjectCorrectionFinancialDescriptionUpdateDTO): Observable<ProjectCorrectionFinancialDescriptionDTO> {
    return combineLatest([
      this.auditControlId$,
      this.projectId$,
    ]).pipe(
      switchMap(([auditControlId, projectId]) =>
        this.projectCorrectionFinancialDescriptionService.updateCorrectionFinancialDescription(auditControlId, correctionId, projectId, financialDescriptionData)
      ),
      tap(financialDescription => this.savedFinancialDescription$.next(financialDescription)),
      tap(financialDescription => Log.info('Updated correction financial description', this, financialDescription))
    );
  }

  private canEdit(): Observable<boolean> {
    return combineLatest([
      this.reportCorrectionsAuditControlDetailPageStore.canEdit$,
      this.correction$.pipe(map(correction => correction?.status)),
    ]).pipe(
      map(([canEditAuditControl, status]) =>
        canEditAuditControl && status !== ProjectAuditControlCorrectionDTO.StatusEnum.Closed)
    );
  }

  private canClose(): Observable<boolean> {
    return combineLatest([
      this.canEdit$,
      this.permissionService.hasPermission(PermissionsEnum.ProjectMonitorCloseAuditControlCorrection),
      this.correction$,
      this.financialDescription$,
    ]).pipe(
      map(([canEdit, canClose, identification, financialDescription]) =>
        canEdit &&
        canClose &&
        !!identification.partnerId &&
        !!identification.partnerReportId &&
        !!identification.programmeFundId &&
        !!financialDescription.correctionType
      )
    );
  }

  closeCorrection(projectId: number, auditControlId: number, correctionId: number): Observable<ProjectAuditControlCorrectionDTO.StatusEnum> {
    return this.projectAuditControlCorrectionService.closeProjectCorrection(auditControlId, correctionId, projectId).pipe(
      map(status => status as ProjectAuditControlCorrectionDTO.StatusEnum),
      tap(status => this.auditControlCorrectionStore.refreshCorrections$.next()),
      tap(status => Log.info('Changed status for correction', this, correctionId, status)),
    );
  }

  private correctionAvailableCostItems(): Observable<PageCorrectionCostItemDTO> {
    return combineLatest([
        this.correction$,
        this.refreshScopeLimitationData$,
        this.reportCorrectionsAuditControlDetailPageStore.auditControlId$,
        this.reportCorrectionsAuditControlDetailPageStore.projectId$,
        this.correctionId$,
        this.costItemsPageIndex$,
        this.costItemsPageSize$
    ])
        .pipe(
            filter(([correction, refresh, auditControlId, projectId, correctionId, pageIndex, pageSize]: any) => correction.type === 'LinkedToInvoice' && correction.status === 'Ongoing'),
            switchMap(([correction, refresh, auditControlId, projectId, correctionId, pageIndex, pageSize]: any) =>
                this.projectAuditControlCorrectionService.listCorrectionAvailableCostItems(Number(auditControlId), Number(correctionId), projectId, pageIndex, pageSize)
            )
        );
  }

  private correctionAvailableProcurements(): Observable<Map<number, string>> {
    return combineLatest([
      this.reportCorrectionsAuditControlDetailPageStore.auditControlId$,
      this.reportCorrectionsAuditControlDetailPageStore.projectId$,
      this.correction$,
      this.refreshScopeLimitationData$,
    ])
        .pipe(
            filter(([auditControlId, projectId, correction]) => !!correction.partnerId && !!correction.partnerReportId),
            switchMap(([auditControlId, projectId, identification]) =>
                this.projectAuditControlCorrectionService.listCorrectionAvailableProcurements(Number(auditControlId), Number(identification.id), Number(projectId))
            ),
            map(procurements =>
                new Map(procurements.map(procurement => [procurement.id, procurement.name])))
        );
  }

}
