import {Injectable} from '@angular/core';
import {
  PagePaymentToProjectDTO,
  PaymentsApiService, UserRoleCreateDTO,
} from '@cat/api';
import {PermissionService} from '../../security/permissions/permission.service';
import {Observable, combineLatest, Subject} from 'rxjs';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Tables} from '@common/utils/tables';
import {Log} from '@common/utils/log';
import {MatSort} from '@angular/material/sort';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@Injectable({
  providedIn: 'root'
})
export class PaymentsToProjectPageStore {

  userCanView$: Observable<boolean>;
  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();
  paymentToProjectDTO$: Observable<PagePaymentToProjectDTO>;

  constructor(private paymentApiService: PaymentsApiService,
              private permissionService: PermissionService) {
    this.paymentToProjectDTO$ = this.paymentsToProjects();
    this.userCanView$ = this.userCanView();
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
        tap(page => Log.info('Fetched the projects:', this, page.content)),
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

  // Although these columns has sortable feature, they don't keep any value at the moment - so sort with default config
  private isSortCapable(column: string | undefined): boolean {
    return (column !== 'paymentType' && column !== 'dateOfLastPayment' && column !== 'amountPaidPerFund');
  }

}
