import {UntilDestroy} from '@ngneat/until-destroy';
import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {filter, map, switchMap, tap} from 'rxjs/operators';
import {
  AccountingYearDTO,
  AccountingYearService,
  PaymentAuditService,
  PaymentToEcExportMetadataDTO,
  PluginInfoDTO,
  ProgrammeFundDTO,
  ProgrammeFundService,
  UserRoleCreateDTO
} from '@cat/api';
import {PermissionService} from '../../security/permissions/permission.service';
import {Log} from '@common/utils/log';
import {PluginStore} from '@common/services/plugin-store.service';
import {DownloadService} from '@common/services/download.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import TypeEnum = PluginInfoDTO.TypeEnum;


@UntilDestroy()
@Injectable({
  providedIn: 'root'
})
export class PaymentsAuditPageStore {

  userCanView$: Observable<boolean>;
  userCanEdit$: Observable<boolean>;
  availableFunds$: Observable<ProgrammeFundDTO[]>;
  accountingYears$: Observable<AccountingYearDTO[]>;
  paymentAuditExportPlugins$: Observable<PluginInfoDTO[]>;
  paymentAuditExportMetadata$: Observable<PaymentToEcExportMetadataDTO[]>;

  private exportTriggeredEvent$ = new BehaviorSubject<void>(undefined);
  private refreshExportMetadata$ = new BehaviorSubject<void>(undefined);


  constructor(private permissionService: PermissionService,
              private programmeFundsService: ProgrammeFundService,
              private accountingYearService: AccountingYearService,
              private pluginStore: PluginStore,
              private downloadService: DownloadService,
              private paymentAuditService: PaymentAuditService) {
    this.paymentAuditExportPlugins$ = this.pluginStore.getPluginListByType(TypeEnum.PAYMENTAPPLICATIONTOECAUDITEXPORT);
    this.paymentAuditExportMetadata$ = combineLatest([this.exportTriggeredEvent$, this.refreshExportMetadata$]).pipe(switchMap(() => this.paymentAuditService.list()));
    this.userCanView$ = this.userCanView();
    this.userCanEdit$ = this.userCanEdit();
    this.availableFunds$ = this.availableFunds();
    this.accountingYears$ = this.accountingYears();
  }

  private userCanView(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.PaymentsAuditRetrieve),
      this.permissionService.hasPermission(PermissionsEnum.PaymentsAuditUpdate),
    ])
      .pipe(
        map(([canRetrieve, canUpdate]) => canRetrieve || canUpdate)
      );
  }

  private userCanEdit(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.PaymentsAuditUpdate),
    ])
      .pipe(
        map(([canUpdate]) => canUpdate)
      );
  }

  private availableFunds(): Observable<ProgrammeFundDTO[]> {
    return this.programmeFundsService.getProgrammeFundList()
      .pipe(
        map(funds => funds.filter(fund => fund.selected)),
        tap(availableFunds => Log.info('Fetched available funds:', this, availableFunds))
      );
  }

  private accountingYears(): Observable<AccountingYearDTO[]> {
    return this.accountingYearService.getAccountingYears()
      .pipe(
        tap(accountingYears => Log.info('Fetched all accounting years:', this, accountingYears))
      );
  }

  exportData(pluginKey: string, programmeFundId: number, accountingYearId: number): Observable<any> {
    return this.paymentAuditService._export(pluginKey, accountingYearId, programmeFundId).pipe(
      tap(() => this.exportTriggeredEvent$.next())
    );
  }

  download(fileId: number, pluginKey: string): Observable<any> {
    return this.downloadService.download(`/api/paymentApplicationsToEc/audit/download/${fileId}?pluginKey=${pluginKey}`, 'audit-data.xls');
  }

  refreshExportMetaData() {
    this.refreshExportMetadata$.next();
  }
}
