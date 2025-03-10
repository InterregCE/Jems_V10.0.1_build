import {Injectable} from '@angular/core';
import {
  CurrencyDTO,
  JemsFileMetadataDTO,
  ProjectPartnerReportProcurementAttachmentService,
  ProjectPartnerReportProcurementBeneficialChangeDTO,
  ProjectPartnerReportProcurementBeneficialDTO,
  ProjectPartnerReportProcurementBeneficialOwnerService,
  ProjectPartnerReportProcurementChangeDTO,
  ProjectPartnerReportProcurementDTO,
  ProjectPartnerReportProcurementGDPRAttachmentService,
  ProjectPartnerReportProcurementService,
  ProjectPartnerReportProcurementSubcontractChangeDTO,
  ProjectPartnerReportProcurementSubcontractDTO,
  ProjectPartnerReportProcurementSubcontractorService,
  ProjectPartnerReportService,
  ProjectReportProcurementFileDTO,
} from '@cat/api';
import {BehaviorSubject, combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {catchError, filter, finalize, map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {Log} from '@common/utils/log';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {PartnerReportDetailPageStore} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {CurrencyStore} from '@common/services/currency.store';
import {v4 as uuid} from 'uuid';
import {APIError} from '@common/models/APIError';
import {DownloadService} from '@common/services/download.service';

@Injectable({providedIn: 'root'})
export class PartnerReportProcurementStore {

  procurementId$ = new BehaviorSubject<number>(0);
  procurement$: Observable<ProjectPartnerReportProcurementDTO>;
  beneficials$: Observable<ProjectPartnerReportProcurementBeneficialDTO[]>;
  subcontracts$: Observable<ProjectPartnerReportProcurementSubcontractDTO[]>;
  attachments$: Observable<ProjectReportProcurementFileDTO[]>;
  gdprAttachments$: Observable<ProjectReportProcurementFileDTO[]>;

  partnerId$: Observable<number>;
  reportId$: Observable<number>;

  currencies$: Observable<CurrencyDTO[]>;
  savedProcurement$ = new Subject<ProjectPartnerReportProcurementDTO>();
  savedBeneficials$ = new Subject<ProjectPartnerReportProcurementBeneficialDTO[]>();
  savedSubcontracts$ = new Subject<ProjectPartnerReportProcurementSubcontractDTO[]>();
  filesChanged$ = new Subject<void>();
  error$ = new Subject<APIError | null>();
  gdprError$ = new Subject<APIError | null>();

  constructor(
    private routingService: RoutingService,
    private projectPartnerReportService: ProjectPartnerReportService,
    private projectStore: ProjectStore,
    private currencyStore: CurrencyStore,
    private projectPartnerProcurementService: ProjectPartnerReportProcurementService,
    private projectPartnerProcurementBeneficialService: ProjectPartnerReportProcurementBeneficialOwnerService,
    private projectPartnerProcurementSubcontractorService: ProjectPartnerReportProcurementSubcontractorService,
    private projectPartnerProcurementAttachmentService: ProjectPartnerReportProcurementAttachmentService,
    private projectPartnerProcurementGdprAttachmentService: ProjectPartnerReportProcurementGDPRAttachmentService,
    private downloadService: DownloadService,
  ) {
    this.partnerId$ = this.partnerId();
    this.reportId$ = this.reportId();
    this.procurement$ = this.getProcurement();
    this.beneficials$ = this.getBeneficials();
    this.subcontracts$ = this.getSubcontracts();
    this.attachments$ = this.getAttachments();
    this.gdprAttachments$ = this.getGdprAttachments();
    this.currencies$ = this.currencyStore.currencies$;
  }

  private partnerId(): Observable<number> {
    return this.routingService.routeParameterChanges(PartnerReportPageStore.PARTNER_REPORT_DETAIL_PATH, 'partnerId')
      .pipe(filter(Boolean), map(Number));
  }
  private reportId(): Observable<number> {
    return this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
      .pipe(filter(Boolean), map(Number));
  }

  public getProcurement(): Observable<ProjectPartnerReportProcurementDTO> {
    const initialProcurement$ = combineLatest([
      this.partnerId$,
      this.reportId$,
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
      this.partnerId$,
      this.reportId$,
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerProcurementService.addNewProcurement(partnerId, reportId, payload)
      ),
      tap(procurement => this.savedProcurement$.next(procurement)),
    );
  }

  updateProcurement(payload: ProjectPartnerReportProcurementChangeDTO): Observable<ProjectPartnerReportProcurementDTO> {
    return combineLatest([
      this.partnerId$,
      this.reportId$,
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerProcurementService.updateProcurement(partnerId, reportId, payload)
      ),
      tap(procurement => this.savedProcurement$.next(procurement)),
    );
  }

  public getBeneficials(): Observable<ProjectPartnerReportProcurementBeneficialDTO[]> {
    const initialBeneficials$ = combineLatest([
      this.partnerId$,
      this.reportId$,
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
      this.partnerId$,
      this.reportId$,
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerProcurementBeneficialService.updateBeneficialOwners(partnerId, procurementId, reportId, payload)
      ),
      tap(beneficials => this.savedBeneficials$.next(beneficials)),
    );
  }

  public getSubcontracts(): Observable<ProjectPartnerReportProcurementSubcontractDTO[]> {
    const initialSubcontracts$ = combineLatest([
      this.partnerId$,
      this.reportId$,
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
      this.partnerId$,
      this.reportId$,
    ]).pipe(
      switchMap(([partnerId, reportId]) =>
        this.projectPartnerProcurementSubcontractorService.updateSubcontractors(partnerId, procurementId, reportId, payload)
      ),
      tap(subcontracts => this.savedSubcontracts$.next(subcontracts)),
    );
  }

  public getAttachments(): Observable<ProjectReportProcurementFileDTO[]> {
    return combineLatest([
      this.partnerId$,
      this.reportId$,
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

  public getGdprAttachments(): Observable<ProjectReportProcurementFileDTO[]> {
    return combineLatest([
      this.partnerId$,
      this.reportId$,
      this.procurementId$,
      this.filesChanged$.pipe(startWith(null)),
    ]).pipe(
      switchMap(([partnerId, reportId, procurementId]) => {
          if (procurementId) {
            return this.projectPartnerProcurementGdprAttachmentService.getAttachments(partnerId, procurementId, reportId);
          } else {
            return of([]);
          }
        }
      ),
      tap(data => Log.info('Fetched project procurement GDPR attachments by id', this, data))
    );
  }

  uploadProcurementFile(file: File): Observable<JemsFileMetadataDTO> {
    const serviceId = uuid();
    this.routingService.confirmLeaveSet.add(serviceId);
    return combineLatest([
      this.partnerId$,
      this.reportId$,
      this.procurementId$,
    ]).pipe(
      take(1),
      switchMap(([partnerId, reportId, procurementId]) =>
        this.projectPartnerProcurementAttachmentService.uploadAttachmentForm(file, Number(partnerId), procurementId, reportId)
      ),
      tap(() => this.filesChanged$.next()),
      tap(() => this.error$.next(null)),
      finalize(() => this.routingService.confirmLeaveSet.delete(serviceId)),
      catchError(error => {
        this.error$.next(error.error);
        return of({} as JemsFileMetadataDTO);
      }),
    );
  }

  uploadProcurementGdprFile(file: File): Observable<JemsFileMetadataDTO> {
    const serviceId = uuid();
    this.routingService.confirmLeaveSet.add(serviceId);
    return combineLatest([
      this.partnerId$,
      this.reportId$,
      this.procurementId$,
    ]).pipe(
      take(1),
      switchMap(([partnerId, reportId, procurementId]) =>
        this.projectPartnerProcurementGdprAttachmentService.uploadAttachmentForm(file, Number(partnerId), procurementId, reportId)
      ),
      tap(() => this.filesChanged$.next()),
      tap(() => this.gdprError$.next(null)),
      finalize(() => this.routingService.confirmLeaveSet.delete(serviceId)),
      catchError(error => {
        this.gdprError$.next(error.error);
        return of({} as JemsFileMetadataDTO);
      }),
    );
  }

  downloadGdprFile(fileId: number): Observable<any> {
    return this.partnerId$
      .pipe(
        take(1),
        filter(partnerId => !!partnerId),
        tap(() => this.gdprError$.next(null)),
        switchMap((partnerId) => {
          return this.downloadService.download(`/api/project/report/partner/procurement/gdprAttachment/byPartnerId/${partnerId}/byFileId/${fileId}/download`, 'partner-report');
        }),
        catchError(error => {
          this.gdprError$.next(error.error);
          return of({} as JemsFileMetadataDTO);
        })
      );
  }

}
