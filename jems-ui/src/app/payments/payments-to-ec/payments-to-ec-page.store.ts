import {Injectable} from '@angular/core';
import {PagePaymentApplicationsToEcDTO, PaymentApplicationsToECService, UserRoleCreateDTO,} from '@cat/api';
import {PermissionService} from '../../security/permissions/permission.service';
import {combineLatest, Observable, Subject} from 'rxjs';
import {map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {Tables} from '@common/utils/tables';
import {Log} from '@common/utils/log';
import {MatSort} from '@angular/material/sort';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@UntilDestroy()
@Injectable({
  providedIn: 'root'
})
export class PaymentsToEcPageStore {

  userCanView$: Observable<boolean>;
  userCanEdit$: Observable<boolean>;
  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();
  paymentToEcPage$: Observable<PagePaymentApplicationsToEcDTO>;

  constructor(private paymentToEcService: PaymentApplicationsToECService,
              private permissionService: PermissionService,
  ) {
    this.paymentToEcPage$ = this.paymentToEcPage();
    this.userCanView$ = this.userCanView();
    this.userCanEdit$ = this.userCanEdit();
  }

  private paymentToEcPage(): Observable<PagePaymentApplicationsToEcDTO> {
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
          this.paymentToEcService.getPaymentApplicationsToEc(pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the payments to ec page:', this, page.content)),
      );
  }

  private userCanView(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.PaymentsToEcRetrieve),
      this.permissionService.hasPermission(PermissionsEnum.PaymentsToEcUpdate),
    ])
      .pipe(
        map(([canRetrieve, canUpdate]) => canRetrieve || canUpdate)
      );
  }

  private userCanEdit(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.PaymentsToEcUpdate),
    ])
      .pipe(
        map(([canUpdate]) => canUpdate)
      );
  }

  deletePaymentToEc(paymentId: number) {
    this.paymentToEcService.deletePaymentApplicationToEc(paymentId)
      .pipe(
        take(1),
        tap(() => this.newPageIndex$.next(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
        untilDestroyed(this)
      ).subscribe();
  }
}
