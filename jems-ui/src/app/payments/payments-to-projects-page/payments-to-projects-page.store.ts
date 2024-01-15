import {Injectable} from '@angular/core';
import {
  AuditControlCorrectionDTO,
  PagePaymentToProjectDTO,
  PaymentDetailDTO,
  PaymentsAPIService,
  PaymentSearchRequestDTO,
  ProjectAuditAndControlService,
  UserRoleCreateDTO,
} from '@cat/api';
import {PermissionService} from '../../security/permissions/permission.service';
import {BehaviorSubject, combineLatest, merge, Observable, of, Subject} from 'rxjs';
import {catchError, map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '@common/utils/tables';
import {Log} from '@common/utils/log';
import {MatSort} from '@angular/material/sort';
import {RoutingService} from '@common/services/routing.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@Injectable({
  providedIn: 'root'
})
export class PaymentsToProjectPageStore {

  public static PAYMENTS_DETAIL_PATH = '/payments/';
  private paymentId: number;

  userCanView$: Observable<boolean>;
  userCanEdit$: Observable<boolean>;
  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  filter$ = new BehaviorSubject<PaymentSearchRequestDTO>(null as any);
  newSort$ = new Subject<Partial<MatSort>>();
  paymentToProjectDTO$: Observable<PagePaymentToProjectDTO>;
  payment$: Observable<PaymentDetailDTO>;

  private savedPayment$ = new Subject<PaymentDetailDTO>();

  constructor(private paymentApiService: PaymentsAPIService,
              private permissionService: PermissionService,
              private routingService: RoutingService) {
    this.paymentToProjectDTO$ = this.paymentsToProjects();
    this.userCanView$ = this.userCanView();
    this.userCanEdit$ = this.userCanEdit();
    this.payment$ = this.payment();
  }

  private paymentsToProjects(): Observable<PagePaymentToProjectDTO> {
    return combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.filter$,
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => (sort?.direction && this.isSortCapable(sort?.active)) ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      )
    ])
      .pipe(
        switchMap(([pageIndex, pageSize, filter, sort]) =>
          this.paymentApiService.getPaymentsToProjects(filter, pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the payments to projects:', this, page.content)),
      );
  }

  private userCanView(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.PaymentsRetrieve),
      this.permissionService.hasPermission(PermissionsEnum.PaymentsUpdate),
    ])
      .pipe(
        map(([canRetrieve, canUpdate]) => canRetrieve || canUpdate)
      );
  }

  private userCanEdit(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.PaymentsUpdate),
    ])
      .pipe(
        map(([canUpdate]) => canUpdate)
      );
  }

  private payment(): Observable<PaymentDetailDTO> {
    const initialPayment$ = this.routingService.routeParameterChanges(PaymentsToProjectPageStore.PAYMENTS_DETAIL_PATH, 'paymentId')
      .pipe(
        tap((paymentId) => {
          this.paymentId = Number(paymentId);
        }),
        switchMap((paymentId) => paymentId
          ? this.paymentApiService.getPaymentDetail(Number(paymentId))
            .pipe(
              catchError(() => {
                this.routingService.navigate([PaymentsToProjectPageStore.PAYMENTS_DETAIL_PATH]);
                return of({} as PaymentDetailDTO);
              })
            )
          : of({} as PaymentDetailDTO)
        ),
        tap(payment => Log.info('Fetched the payment detail:', this, payment))
      );

    return merge(initialPayment$, this.savedPayment$)
      .pipe(
        shareReplay(1)
      );
  }

  // Although these columns have a sortable feature, they don't keep any value at the moment - so sort with default config
  private isSortCapable(column: string | undefined): boolean {
    return (column !== 'paymentType' && column !== 'dateOfLastPayment');
  }

}
