import {Injectable} from '@angular/core';
import {
  CurrencyDTO,
  ProjectPartnerReportProcurementAttachmentService,
  ProjectPartnerReportProcurementBeneficialChangeDTO,
  ProjectPartnerReportProcurementBeneficialDTO,
  ProjectPartnerReportProcurementBeneficialOwnerService,
  ProjectPartnerReportProcurementChangeDTO,
  ProjectPartnerReportProcurementDTO,
  ProjectPartnerReportProcurementService,
  ProjectPartnerReportProcurementSubcontractChangeDTO,
  ProjectPartnerReportProcurementSubcontractDTO,
  ProjectPartnerReportProcurementSubcontractorService,
  ProjectPartnerReportService,
  ProjectReportFileMetadataDTO,
  ProjectReportProcurementFileDTO,
} from '@cat/api';
import {BehaviorSubject, combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {catchError, map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {Log} from '@common/utils/log';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {CurrencyStore} from '@common/services/currency.store';
import {v4 as uuid} from 'uuid';
import {FileListItem} from '@common/components/file-list/file-list-item';
import {
  ReportFileManagementStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-annexes-tab/report-file-management-store';
import {FileDescriptionChange} from '@common/components/file-list/file-list-table/file-description-change';
import {APIError} from '@common/models/APIError';

@Injectable({providedIn: 'root'})
export class PartnerReportProcurementStore {

  procurementId$ = new BehaviorSubject<number>(0);
  procurement$: Observable<ProjectPartnerReportProcurementDTO>;
  beneficials$: Observable<ProjectPartnerReportProcurementBeneficialDTO[]>;
  subcontracts$: Observable<ProjectPartnerReportProcurementSubcontractDTO[]>;
  attachments$: Observable<ProjectReportProcurementFileDTO[]>;

  partnerId$: Observable<string | number | null>;
  reportId$: Observable<string | number | null>;

  currencies$: Observable<CurrencyDTO[]>;
  savedProcurement$ = new Subject<ProjectPartnerReportProcurementDTO>();
  savedBeneficials$ = new Subject<ProjectPartnerReportProcurementBeneficialDTO[]>();
  savedSubcontracts$ = new Subject<ProjectPartnerReportProcurementSubcontractDTO[]>();
  filesChanged$ = new Subject<void>();
  error$ = new Subject<APIError | null>();

  constructor(
    private routingService: RoutingService,
    private projectPartnerReportService: ProjectPartnerReportService,
    private projectStore: ProjectStore,
    private currencyStore: CurrencyStore,
    private projectPartnerProcurementService: ProjectPartnerReportProcurementService,
    private projectPartnerProcurementBeneficialService: ProjectPartnerReportProcurementBeneficialOwnerService,
    private projectPartnerProcurementSubcontractorService: ProjectPartnerReportProcurementSubcontractorService,
    private projectPartnerProcurementAttachmentService: ProjectPartnerReportProcurementAttachmentService,
    private fileManagementStore: ReportFileManagementStore,
  ) {
    this.partnerId$ = this.partnerId();
    this.reportId$ = this.reportId();
    this.procurement$ = this.getProcurement();
    this.beneficials$ = this.getBeneficials();
    this.subcontracts$ = this.getSubcontracts();
    this.attachments$ = this.getAttachments();
    this.currencies$ = this.currencyStore.currencies$;
  }

  private partnerId(): Observable<number | string | null> {
    return this.routingService.routeParameterChanges(PartnerReportPageStore.PARTNER_REPORT_DETAIL_PATH, 'partnerId');
  }
  private reportId(): Observable<number | string | null> {
    return this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId');
  }

  public getProcurement(): Observable<ProjectPartnerReportProcurementDTO> {
    const initialProcurement$ = combineLatest([
      this.partnerId$.pipe(map(id => Number(id))),
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(id => Number(id))),
      this.procurementId$,
    ]).pipe(
      switchMap(([partnerId, reportId, procurementId]) => {
          if (procurementId) {
            return this.projectPartnerProcurementService.getProcurementById(partnerId, procurementId, reportId);
          } else {
            return of(({
              id: 0,
              reportId: 0,
              reportNumber: 0,
              createdInThisReport: true,
              lastChanged: new Date(),
              contractName: '',
              referenceNumber: '',
              contractDate: '',
              contractType: '',
              contractAmount: 0,
              currencyCode: '',
              supplierName: '',
              vatNumber: '',
              comment: '',
            }) as ProjectPartnerReportProcurementDTO);
          }
        }
      ),
      tap(data => Log.info('Fetched project procurement detail by id', this, data))
    );

    return merge(initialProcurement$, this.savedProcurement$);
  }

  createProcurement(payload: ProjectPartnerReportProcurementChangeDTO): Observable<ProjectPartnerReportProcurementDTO> {
    return combineLatest([
      this.partnerId$.pipe(map(id => Number(id))),
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(id => Number(id))),
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerProcurementService.addNewProcurement(partnerId, reportId, payload)
      ),
      tap(procurement => this.savedProcurement$.next(procurement)),
    );
  }

  updateProcurement(payload: ProjectPartnerReportProcurementChangeDTO): Observable<ProjectPartnerReportProcurementDTO> {
    return combineLatest([
      this.partnerId$.pipe(map(id => Number(id))),
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(id => Number(id))),
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerProcurementService.updateProcurement(partnerId, reportId, payload)
      ),
      tap(procurement => this.savedProcurement$.next(procurement)),
    );
  }

  public getBeneficials(): Observable<ProjectPartnerReportProcurementBeneficialDTO[]> {
    const initialBeneficials$ = combineLatest([
      this.partnerId$.pipe(map(id => Number(id))),
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(id => Number(id))),
      this.procurementId$,
    ]).pipe(
      switchMap(([partnerId, reportId, procurementId]) => {
          if (procurementId) {
            return this.projectPartnerProcurementBeneficialService.getBeneficialOwners(partnerId, procurementId, reportId);
          } else {
            return of([]);
          }
        }
      ),
      tap(data => Log.info('Fetched project procurement beneficials by id', this, data))
    );

    return merge(initialBeneficials$, this.savedBeneficials$);
  }

  updateBeneficials(procurementId: number, payload: ProjectPartnerReportProcurementBeneficialChangeDTO[]): Observable<ProjectPartnerReportProcurementBeneficialDTO[]> {
    return combineLatest([
      this.partnerId$.pipe(map(id => Number(id))),
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(id => Number(id))),
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerProcurementBeneficialService.updateBeneficialOwners(partnerId, procurementId, reportId, payload)
      ),
      tap(beneficials => this.savedBeneficials$.next(beneficials)),
    );
  }

  public getSubcontracts(): Observable<ProjectPartnerReportProcurementSubcontractDTO[]> {
    const initialSubcontracts$ = combineLatest([
      this.partnerId$.pipe(map(id => Number(id))),
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(id => Number(id))),
      this.procurementId$,
    ]).pipe(
      switchMap(([partnerId, reportId, procurementId]) => {
          if (procurementId) {
            return this.projectPartnerProcurementSubcontractorService.getSubcontractors(partnerId, procurementId, reportId);
          } else {
            return of([]);
          }
        }
      ),
      tap(data => Log.info('Fetched project procurement subcontractors by id', this, data))
    );

    return merge(initialSubcontracts$, this.savedSubcontracts$);
  }

  updateSubcontracts(procurementId: number, payload: ProjectPartnerReportProcurementSubcontractChangeDTO[]): Observable<ProjectPartnerReportProcurementSubcontractDTO[]> {
    return combineLatest([
      this.partnerId$.pipe(map(id => Number(id))),
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(id => Number(id))),
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerProcurementSubcontractorService.updateSubcontractors(partnerId, procurementId, reportId, payload)
      ),
      tap(subcontracts => this.savedSubcontracts$.next(subcontracts)),
    );
  }

  public getAttachments(): Observable<ProjectReportProcurementFileDTO[]> {
    return combineLatest([
      this.partnerId$.pipe(map(id => Number(id))),
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(id => Number(id))),
      this.procurementId$,
      this.filesChanged$.pipe(startWith(null)),
    ]).pipe(
      switchMap(([partnerId, reportId, procurementId]) => {
          if (procurementId) {
            return this.projectPartnerProcurementAttachmentService.getAttachments(partnerId, procurementId, reportId);
          } else {
            return of([]);
          }
        }
      ),
      tap(data => Log.info('Fetched project procurement attachments by id', this, data))
    );
  }

  uploadProcurementFile(file: File): Observable<ProjectReportFileMetadataDTO> {
    const serviceId = uuid();
    this.routingService.confirmLeaveMap.set(serviceId, true);
    return combineLatest([
      this.partnerId$.pipe(map(id => Number(id))),
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(id => Number(id))),
      this.procurementId$,
    ]).pipe(
      take(1),
      switchMap(([partnerId, reportId, procurementId]) =>
        this.projectPartnerProcurementAttachmentService.uploadAttachmentForm(file, Number(partnerId), procurementId, reportId)
      ),
      tap(() => this.filesChanged$.next()),
      tap(() => this.error$.next(null)),
      tap(() => this.routingService.confirmLeaveMap.delete(serviceId)),
      catchError(error => {
        this.error$.next(error.error);
        return of({} as ProjectReportFileMetadataDTO);
      }),
    );
  }

  updateDescription(data: FileDescriptionChange): Observable<void> {
    return combineLatest([
      this.partnerId$.pipe(map(id => Number(id))),
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(id => Number(id))),
    ]).pipe(
      take(1),
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerReportService.updateDescription(data.id, partnerId, reportId, data.description)
      ),
    );
  }

  deleteProcurementFile(file: FileListItem): Observable<void> {
    return this.fileManagementStore.deleteFile(file.id)
      .pipe(tap(() => this.filesChanged$.next()));
  }

}
