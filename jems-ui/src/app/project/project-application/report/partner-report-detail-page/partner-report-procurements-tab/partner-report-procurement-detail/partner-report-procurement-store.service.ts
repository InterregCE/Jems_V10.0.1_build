import {Injectable} from '@angular/core';
import {
  ProjectPartnerReportProcurementService,
  ProjectPartnerReportService,
  ProjectPartnerReportProcurementDTO,
  ProjectPartnerReportProcurementChangeDTO,
  CurrencyDTO,
  ProjectPartnerReportProcurementBeneficialDTO,
  ProjectPartnerReportProcurementBeneficialOwnerService,
  ProjectPartnerReportProcurementBeneficialChangeDTO,
} from '@cat/api';
import {BehaviorSubject, combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {map, switchMap, tap} from 'rxjs/operators';
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

@Injectable({providedIn: 'root'})
export class PartnerReportProcurementStore {

  procurementId$ = new BehaviorSubject<number>(0);
  procurement$: Observable<ProjectPartnerReportProcurementDTO>;
  beneficials$: Observable<ProjectPartnerReportProcurementBeneficialDTO[]>;

  partnerId$: Observable<string | number | null>;

  currencies$: Observable<CurrencyDTO[]>;
  savedProcurement$ = new Subject<ProjectPartnerReportProcurementDTO>();
  savedBeneficials$ = new Subject<ProjectPartnerReportProcurementBeneficialDTO[]>();

  constructor(private routingService: RoutingService,
              private partnerReportPageStore: PartnerReportPageStore,
              private projectPartnerReportService: ProjectPartnerReportService,
              private projectStore: ProjectStore,
              private currencyStore: CurrencyStore,
              private projectPartnerProcurementService: ProjectPartnerReportProcurementService,
              private projectPartnerProcurementBeneficialService: ProjectPartnerReportProcurementBeneficialOwnerService,
  ) {
    this.partnerId$ = this.partnerId();
    this.procurement$ = this.getProcurement();
    this.beneficials$ = this.getBeneficials();
    this.currencies$ = this.currencyStore.currencies$;
  }

  private partnerId(): Observable<number | string | null> {
    return this.routingService.routeParameterChanges(PartnerReportPageStore.PARTNER_REPORT_DETAIL_PATH, 'partnerId');
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

  updateBeneficials(payload: ProjectPartnerReportProcurementBeneficialChangeDTO[]): Observable<ProjectPartnerReportProcurementBeneficialDTO[]> {
    return combineLatest([
      this.partnerId$.pipe(map(id => Number(id))),
      this.routingService.routeParameterChanges(PartnerReportDetailPageStore.REPORT_DETAIL_PATH, 'reportId')
        .pipe(map(id => Number(id))),
      this.procurementId$,
    ]).pipe(
      switchMap(([partnerId, reportId, procurementId]) =>
        this.projectPartnerProcurementBeneficialService.updateBeneficialOwners(partnerId, procurementId, reportId, payload)
      ),
      tap(beneficials => this.savedBeneficials$.next(beneficials)),
    );
  }

}
