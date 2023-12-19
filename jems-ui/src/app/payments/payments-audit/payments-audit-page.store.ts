import {UntilDestroy} from '@ngneat/until-destroy';
import {Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {map, switchMap, tap} from 'rxjs/operators';
import {
  AccountingYearDTO,
  AccountingYearService, PaymentToECAuditService, PaymentToEcExportMetadataDTO,
  PluginInfoDTO, ProgrammeFundDTO,
  ProgrammeFundService,
  UserRoleCreateDTO
} from '@cat/api';
import {PermissionService} from '../../security/permissions/permission.service';
import {Log} from '@common/utils/log';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {PluginStore} from '@common/services/plugin-store.service';
import TypeEnum = PluginInfoDTO.TypeEnum;
import {DownloadService} from '@common/services/download.service';


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
              private paymentToEcAuditService: PaymentToECAuditService) {
    this.paymentAuditExportPlugins$ = this.pluginStore.getPluginListByType(TypeEnum.PAYMENTAPPLICATIONTOECAUDITEXPORT);
    this.paymentAuditExportMetadata$ = combineLatest([this.exportTriggeredEvent$, this.refreshExportMetadata$]).pipe(switchMap(() => this.paymentToEcAuditService.list()));
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
    return this.programmeFundsService.getAvailableProgrammeFunds()
      .pipe(
        tap(funds => Log.info('Fetched available funds:', this, funds))
      );
  }

  private accountingYears(): Observable<AccountingYearDTO[]> {
    return this.accountingYearService.getAccountingYears()
      .pipe(
        tap(accountingYears => Log.info('Fetched all accounting years:', this, accountingYears))
      );
  }

  exportData(pluginKey: string, programmeFundId: number, accountingYearId: number): Observable<any> {
    return this.paymentToEcAuditService._export(pluginKey, accountingYearId, programmeFundId).pipe(
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
