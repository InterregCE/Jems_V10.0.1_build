import {ChangeDetectionStrategy, Component, OnDestroy} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {PaymentsAuditPageStore} from './payments-audit-page.store';
import {map, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {PaymentsPageSidenavService} from '../payments-page-sidenav.service';
import {Alert} from '@common/components/forms/alert';
import {FormBuilder, FormGroup} from '@angular/forms';
import {AccountingYearDTO, PaymentToEcExportMetadataDTO, PluginInfoDTO, ProgrammeFundDTO} from '@cat/api';

const REFRESH_INTERVAL_IN_MILLISECOND = 5000;

@UntilDestroy()
@Component({
  selector: 'jems-payments-audit-page',
  templateUrl: './payments-audit-page.component.html',
  styleUrls: ['./payments-audit-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaymentsAuditPageComponent implements OnDestroy {
  Alert = Alert;
  refreshInterval;
  data$: Observable<{
    userCanView: boolean;
    isExportDisabled: boolean;
    availableFunds: ProgrammeFundDTO[];
    plugins: PluginInfoDTO[];
    paymentAuditExportMetadata: PaymentToEcExportMetadataDTO[];
    accountingYears: AccountingYearDTO[];
  }>;
  userCanEdit$: Observable<boolean>;
  exportForm: FormGroup;


  constructor(private pageStore: PaymentsAuditPageStore,
              private paymentsPageSidenav: PaymentsPageSidenavService,
              private formBuilder: FormBuilder) {

    this.refreshInterval = setInterval(() => {
      this.pageStore.refreshExportMetaData();
    }, REFRESH_INTERVAL_IN_MILLISECOND);

    this.userCanEdit$ = this.pageStore.userCanEdit$;

    this.pageStore.paymentAuditExportPlugins$.pipe(
      tap(plugins => this.initForm(plugins)),
      untilDestroyed(this)
    ).subscribe();


    this.data$ = combineLatest([
      this.pageStore.userCanView$,
      this.pageStore.availableFunds$,
      this.pageStore.accountingYears$,
      this.pageStore.paymentAuditExportPlugins$,
      this.pageStore.paymentAuditExportMetadata$
    ]).pipe(
      map(([userCanView, availableFunds, accountingYears, plugins, paymentAuditExportMetadata]) => ({
        userCanView,
        availableFunds,
        accountingYears,
        plugins,
        paymentAuditExportMetadata,
        isExportDisabled: this.isExportDisabled(paymentAuditExportMetadata),
      }))
    );

  }
  ngOnDestroy(): void {
    clearInterval(this.refreshInterval);
  }

  initForm(plugins: PluginInfoDTO[]): void {
    this.exportForm = this.formBuilder.group({
      pluginKey: [plugins.length > 0 ? plugins[0].key : undefined],
      fund: '',
      accountingYear: ''
    });
  }

  downloadData(fileId: number, pluginKey: string): void {
    this.pageStore.download(fileId, pluginKey).pipe(untilDestroyed(this)).subscribe();
  }

  exportData(pluginKey: string, programmeFundId: number, accountingYearId: number): void {
    this.pageStore.exportData(pluginKey, programmeFundId, accountingYearId).pipe(untilDestroyed(this)).subscribe();
  }

  get pluginKey(): string {
    return this.exportForm.get('pluginKey')?.value;
  }

  get fund(): ProgrammeFundDTO {
    return this.exportForm.get('fund')?.value;
  }

  get accountingYear(): AccountingYearDTO {
    return this.exportForm.get('accountingYear')?.value;
  }

  isExportDisabled(programmeDataExportMetadata: PaymentToEcExportMetadataDTO[]): boolean {
    let isAnyNotTimedOutExportInProgress = false;
    programmeDataExportMetadata.forEach((val) => {
      if (!val.readyToDownload && !val.failed && !val.timedOut) {
        isAnyNotTimedOutExportInProgress = true;
      }
    });
    return isAnyNotTimedOutExportInProgress || !this.pluginKey;
  }

  getPluginName(plugins: PluginInfoDTO[], pluginKey: string): string {
    return plugins.find(it => it.key === pluginKey)?.name || pluginKey;
  }

}
