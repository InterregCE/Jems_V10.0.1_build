import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {
  AccountingYearDTO,
  AccountingYearAvailabilityDTO,
  AccountingYearService, PaymentApplicationToEcCreateDTO,
  PaymentApplicationToEcDetailDTO,
  PaymentApplicationToEcDTO,
  PaymentApplicationToECService,
  PaymentApplicationToEcSummaryUpdateDTO, PaymentToEcAmountSummaryDTO, PaymentToECLinkingAPIService,
  ProgrammeFundDTO,
  ProgrammeFundService,
  UserRoleCreateDTO,
} from '@cat/api';
import {PermissionService} from '../../../security/permissions/permission.service';
import {RoutingService} from '@common/services/routing.service';
import {filter, map, switchMap, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {MatSort} from '@angular/material/sort';
import {UntilDestroy} from '@ngneat/until-destroy';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import PaymentEcStatusEnum = PaymentApplicationToEcDTO.StatusEnum;

@UntilDestroy()
@Injectable({
  providedIn: 'root'
})
export class PaymentsToEcDetailPageStore {
  public static PAYMENTS_TO_EC_PATH = '/app/payments/paymentApplicationsToEc/';

  paymentToEcId$: Observable<number>;
  paymentToEcDetail$: Observable<PaymentApplicationToEcDetailDTO>;
  savedPaymentToEcDetail$ = new Subject<PaymentApplicationToEcDetailDTO>();
  updatedPaymentApplicationStatus$ = new BehaviorSubject<PaymentApplicationToEcDetailDTO.StatusEnum>(PaymentEcStatusEnum.Draft);
  paymentAvailableToReOpen$ =  new BehaviorSubject<boolean>(false);
  programmeFunds$: Observable<ProgrammeFundDTO[]>;
  accountingYears$: Observable<AccountingYearDTO[]>;
  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();
  userCanEdit$: Observable<boolean>;
  userCanView$: Observable<boolean>;
  tabChanged$ = new BehaviorSubject(true);

  constructor(private paymentApplicationToECService: PaymentApplicationToECService,
              private permissionService: PermissionService,
              private routingService: RoutingService,
              private programmeFundService: ProgrammeFundService,
              private accountingYearsService: AccountingYearService,
              private paymentToECLinkingAPIService: PaymentToECLinkingAPIService
  ) {
    this.paymentToEcId$ = this.routingService.routeParameterChanges(PaymentsToEcDetailPageStore.PAYMENTS_TO_EC_PATH, 'paymentToEcId').pipe(map(Number));
    this.accountingYears$ = this.accountingYears();
    this.paymentToEcDetail$ = this.paymentDetail();
    this.userCanEdit$ = this.userCanEdit();
    this.programmeFunds$ = this.allFunds();
    this.userCanView$ = this.userCanView();
  }


  private paymentDetail(): Observable<PaymentApplicationToEcDetailDTO> {
    const initialPaymentDetail$ = this.paymentToEcId$
      .pipe(
        switchMap((paymentId: number) => paymentId ? this.paymentApplicationToECService.getPaymentApplicationToEcDetail(paymentId) : of({status: PaymentEcStatusEnum.Draft}) as Observable<PaymentApplicationToEcDetailDTO>),
          tap(data => this.updatedPaymentApplicationStatus$.next(data.status)),
          tap(data => this.paymentAvailableToReOpen$.next(data.availableToReOpen)),
          tap(data => Log.info('Fetched payment to ec detail', this, data))
      );

    return merge(initialPaymentDetail$, this.savedPaymentToEcDetail$);
  }

  overviewForCurrentTab(): Observable<PaymentToEcAmountSummaryDTO> {
    return combineLatest([this.paymentToEcId$, this.tabChanged$, this.updatedPaymentApplicationStatus$]).pipe(
      switchMap(([paymentId]) => paymentId ? this.paymentToECLinkingAPIService.getPaymentApplicationToEcOverviewAmountsByType(paymentId) : of({totals: {totalEligibleExpenditure: 0, totalUnionContribution: 0, totalPublicContribution: 0}}) as Observable<PaymentToEcAmountSummaryDTO>),
      tap(data => Log.info('Fetched overview for summary tab', this, data))
    );
  }

  cumulativeOverviewForCurrentTab(): Observable<PaymentToEcAmountSummaryDTO> {
    return combineLatest([this.paymentToEcId$, this.tabChanged$, this.updatedPaymentApplicationStatus$]).pipe(
        switchMap(([paymentId]) => paymentId ? this.paymentToECLinkingAPIService.getPaymentApplicationToEcCumulativeOverview(paymentId) : of({
          totals: {
            totalEligibleExpenditure: 0,
            totalUnionContribution: 0,
            totalPublicContribution: 0
          }
        }) as Observable<PaymentToEcAmountSummaryDTO>),
        tap(data => Log.info('Fetched overview for summary tab', this, data))
    );
  }

  private userCanEdit(): Observable<boolean> {
    return  this.permissionService.hasPermission(PermissionsEnum.PaymentsToEcUpdate)
      .pipe(
        map((canUpdate) => canUpdate)
      );
  }

  private userCanView(): Observable<boolean> {
    return  this.permissionService.hasPermission(PermissionsEnum.PaymentsToEcRetrieve)
      .pipe(
        map((canView) => canView)
      );
  }

  private accountingYears(): Observable<AccountingYearDTO[]> {
    return this.accountingYearsService.getAccountingYears()
      .pipe(
        tap(accountingYears => Log.info('Fetched accounting years:', this, accountingYears))
      );
  }

  getProgrammeFundAvailableAccountingYears(programmeFundId: number): Observable<AccountingYearAvailabilityDTO[]> {
    return this.paymentApplicationToECService.getAvailableAccountingYearsForPaymentFund(programmeFundId)
        .pipe(
            tap(accountingYears => Log.info(`Fetched accounting years for programme fund ${programmeFundId}:`, this, accountingYears))
        );
  }

  updatePaymentToEcSummary(paymentToEcSummaryData: PaymentApplicationToEcSummaryUpdateDTO): Observable<PaymentApplicationToEcDetailDTO> {
    return this.paymentApplicationToECService.updatePaymentApplicationToEc(paymentToEcSummaryData.id, paymentToEcSummaryData).pipe(
      tap(saved => Log.info('Payment to Ec summary data updated!', saved)),
      tap(data => this.savedPaymentToEcDetail$.next(data))
    );
  }

  createPaymentToEc(paymentApplication: PaymentApplicationToEcCreateDTO): Observable<PaymentApplicationToEcDetailDTO> {
    return this.paymentApplicationToECService.createPaymentApplicationToEc(paymentApplication).pipe(
      tap(saved => Log.info('Payment to Ec created!', saved)),
      tap(data => this.savedPaymentToEcDetail$.next(data))
    );
  }

  finalizePaymentApplicationToEc(paymentId: number) {
    return this.paymentApplicationToECService.finalizePaymentApplicationToEc(paymentId)
      .pipe(
          tap(statusData => this.paymentAvailableToReOpen$.next(statusData.availableToReOpen)),
          map(statusData => statusData.status as PaymentApplicationToEcDetailDTO.StatusEnum),
          tap(status => this.updatedPaymentApplicationStatus$.next(status)),
          tap(status => Log.info('Changed status for payment application to EC', paymentId, status))
      );
  }

  reOpenFinalizedEcPaymentApplication(paymentId: number): Observable<PaymentApplicationToEcDetailDTO.StatusEnum> {
    return this.paymentApplicationToECService.reOpenFinalizedEcPaymentApplication(paymentId)
        .pipe(
            tap(statusData => this.paymentAvailableToReOpen$.next(statusData.availableToReOpen)),
            map(statusData => statusData.status as PaymentApplicationToEcDetailDTO.StatusEnum),
            tap(status => this.updatedPaymentApplicationStatus$.next(status)),
            tap(status => Log.info('Changed status for payment application to EC', paymentId, status))
        );
  }

   private allFunds(): Observable<ProgrammeFundDTO[]> {
    return this.programmeFundService.getProgrammeFundList()
      .pipe(
        tap(programmeFunds => Log.info('Fetched programme funds:', this, programmeFunds))
      );
  }
}
