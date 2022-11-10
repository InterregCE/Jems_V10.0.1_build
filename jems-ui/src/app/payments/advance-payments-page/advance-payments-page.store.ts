import {Injectable} from '@angular/core';
import {
  AdvancePaymentDetailDTO, AdvancePaymentsService, PageAdvancePaymentDTO, UserRoleCreateDTO,
} from '@cat/api';
import {PermissionService} from '../../security/permissions/permission.service';
import {Observable, combineLatest, Subject, of, merge} from 'rxjs';
import {catchError, map, shareReplay, startWith, switchMap, take, tap} from 'rxjs/operators';
import {Tables} from '@common/utils/tables';
import {Log} from '@common/utils/log';
import {MatSort} from '@angular/material/sort';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {RoutingService} from '@common/services/routing.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Injectable({
  providedIn: 'root'
})
export class AdvancePaymentsPageStore {

  public static ADVANCE_PAYMENTS_PATH = '/advancePayments/';
  private paymentId: number;

  userCanView$: Observable<boolean>;
  userCanEdit$: Observable<boolean>;
  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();
  advancePaymentDTO$: Observable<PageAdvancePaymentDTO>;
  advancePayment$: Observable<AdvancePaymentDetailDTO>;

  private savedAdvancePaymentDetail$ = new Subject<AdvancePaymentDetailDTO>();

  constructor(private advancePaymentsService: AdvancePaymentsService,
              private permissionService: PermissionService,
              private routingService: RoutingService) {
    this.advancePaymentDTO$ = this.advancePayments();
    this.userCanView$ = this.userCanView();
    this.userCanEdit$ = this.userCanEdit();
    this.advancePayment$ = this.advancePayment();
  }

  private advancePayments(): Observable<PageAdvancePaymentDTO> {
    return combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith(Tables.DEFAULT_INITIAL_SORT),
        map(sort => (sort?.direction) ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      )
    ])
      .pipe(
        switchMap(([pageIndex, pageSize, sort]) =>
          this.advancePaymentsService.getAdvancePayments(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the advance payments:', this, page.content)),
      );
  }

  private userCanView(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.AdvancePaymentsRetrieve),
      this.permissionService.hasPermission(PermissionsEnum.AdvancePaymentsUpdate),
    ])
      .pipe(
        map(([canRetrieve, canUpdate]) => canRetrieve || canUpdate)
      );
  }

  private userCanEdit(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.AdvancePaymentsUpdate),
    ])
      .pipe(
        map(([canUpdate]) => canUpdate)
      );
  }

  private advancePayment(): Observable<AdvancePaymentDetailDTO> {
    const initialPayment$ = this.routingService.routeParameterChanges(AdvancePaymentsPageStore.ADVANCE_PAYMENTS_PATH, 'paymentId')
      .pipe(
        tap((paymentId) => {
          this.paymentId = Number(paymentId);
        }),
        switchMap((paymentId) => paymentId
          ? this.advancePaymentsService.getAdvancePaymentDetail(Number(paymentId))
            .pipe(
              catchError(() => {
                this.routingService.navigate([AdvancePaymentsPageStore.ADVANCE_PAYMENTS_PATH]);
                return of({} as AdvancePaymentDetailDTO);
              })
            )
          : of({} as AdvancePaymentDetailDTO)
        ),
        tap(payment => Log.info('Fetched the payment detail:', this, payment))
      );

    return merge(initialPayment$, this.savedAdvancePaymentDetail$)
      .pipe(
        shareReplay(1)
      );
  }

  deleteAdvancedPayment(paymentId: number) {
    this.advancePaymentsService.deleteAdvancePayment(paymentId)
      .pipe(
        take(1),
        tap(() => this.newPageIndex$.next(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
        untilDestroyed(this)
      ).subscribe();
  }
}
