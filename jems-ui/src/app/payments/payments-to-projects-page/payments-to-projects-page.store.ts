import {Injectable} from '@angular/core';
import {
  PagePaymentToProjectDTO, PaymentDetailDTO,
  PaymentsApiService, UserRoleCreateDTO,
} from '@cat/api';
import {PermissionService} from '../../security/permissions/permission.service';
import {Observable, combineLatest, Subject, of, merge} from 'rxjs';
import {catchError, map, shareReplay, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '@common/utils/tables';
import {Log} from '@common/utils/log';
import {MatSort} from '@angular/material/sort';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {RoutingService} from '@common/services/routing.service';

@Injectable({
  providedIn: 'root'
})
export class PaymentsToProjectPageStore {

  public static PAYMENTS_DETAIL_PATH = '/payments/';
  private paymentId: number;

  userCanView$: Observable<boolean>;
  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();
  paymentToProjectDTO$: Observable<PagePaymentToProjectDTO>;
  payment$: Observable<PaymentDetailDTO>;

  private savedPayment$ = new Subject<PaymentDetailDTO>();

  constructor(private paymentApiService: PaymentsApiService,
              private permissionService: PermissionService,
              private routingService: RoutingService) {
    this.paymentToProjectDTO$ = this.paymentsToProjects();
    this.userCanView$ = this.userCanView();
    this.payment$ = this.payment();
  }

  private paymentsToProjects(): Observable<PagePaymentToProjectDTO> {
    return combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => (sort?.direction && this.isSortCapable(sort?.active)) ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      )
    ])
      .pipe(
        switchMap(([pageIndex, pageSize, sort]) =>
          this.paymentApiService.getPaymentsToProjects(pageIndex, pageSize, sort)),
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

  // Although these columns has sortable feature, they don't keep any value at the moment - so sort with default config
  private isSortCapable(column: string | undefined): boolean {
    return (column !== 'paymentType' && column !== 'dateOfLastPayment');
  }

}
