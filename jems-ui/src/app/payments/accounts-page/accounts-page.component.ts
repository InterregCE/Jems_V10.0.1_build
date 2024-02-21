import {AfterViewInit, ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {combineLatest, Observable} from 'rxjs';
import {PaymentAccountOverviewDetailDTO, PaymentAccountOverviewDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {PaymentsPageSidenavService} from '../payments-page-sidenav.service';
import {AccountsPageStore} from './accounts-page.store';

@Component({
  selector: 'jems-accounts-page',
  templateUrl: './accounts-page.component.html',
  styleUrls: ['./accounts-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AccountsPageComponent implements OnInit, AfterViewInit {

  PaymentAccountStatus = PaymentAccountOverviewDetailDTO.StatusEnum;

  @ViewChild('statusCell', {static: true})
  statusCell: TemplateRef<any>;

  @ViewChild('accountingYearCell', {static: true})
  accountingYearCell: TemplateRef<any>;

  @ViewChild('totalEligibleCell', {static: true})
  totalEligibleCell: TemplateRef<any>;

  @ViewChild('totalPublicCell', {static: true})
  totalPublicCell: TemplateRef<any>;

  @ViewChild('totalClaimCell', {static: true})
  totalClaimCell: TemplateRef<any>;

  data$: Observable<{
    userCanView: boolean;
    funds: PaymentAccountOverviewDTO[];
    tableConfiguration: TableConfiguration;
  }>;
  userCanEdit$: Observable<boolean>;

  tableConfiguration: TableConfiguration;
  constructor(
    public accountsPageStore: AccountsPageStore,
    private paymentsPageSidenav: PaymentsPageSidenavService
  ) { }

  ngOnInit(): void {
    this.userCanEdit$ = this.accountsPageStore.userCanEdit$;
    this.data$ = combineLatest([
      this.accountsPageStore.accountsByFund$,
      this.accountsPageStore.userCanView$,
    ])
      .pipe(
        map(([funds, userCanView]) => ({funds, userCanView, tableConfiguration: this.tableConfiguration})),
      );
  }

  ngAfterViewInit(): void {
    this.tableConfiguration = new TableConfiguration({
      isTableClickable: true,
      sortable: false,
      columns: [
        {
          displayedColumn: 'payments.accounts.table.accounting.year',
          columnWidth: ColumnWidth.WideColumn,
          customCellTemplate: this.accountingYearCell,
        },
        {
          displayedColumn: 'payments.accounts.table.status',
          columnWidth: ColumnWidth.SmallColumn,
          customCellTemplate: this.statusCell,
        },
        {
          displayedColumn: 'payments.accounts.table.total.eligible.expenditure',
          columnType: ColumnType.Decimal,
          columnWidth: ColumnWidth.SmallColumn,
          customCellTemplate: this.totalEligibleCell,
        },
        {
          displayedColumn: 'payments.accounts.table.technical.assistance',
          elementProperty: 'technicalAssistance',
          columnType: ColumnType.Decimal,
          columnWidth: ColumnWidth.SmallColumn,
        },
        {
          displayedColumn: 'payments.accounts.table.total.public.contribution',
          columnType: ColumnType.Decimal,
          columnWidth: ColumnWidth.SmallColumn,
          customCellTemplate: this.totalPublicCell
        },
        {
          displayedColumn: 'payments.accounts.table.total.claim',
          columnType: ColumnType.Decimal,
          columnWidth: ColumnWidth.SmallColumn,
          customCellTemplate: this.totalClaimCell,
        },
        {
          displayedColumn: 'payments.accounts.table.national.reference',
          elementProperty: 'nationalReference',
          columnType: ColumnType.StringColumn,
          columnWidth: ColumnWidth.SmallColumn,
        },
        {
          displayedColumn: 'payments.accounts.table.sfc.number',
          elementProperty: 'sfcNumber',
          columnType: ColumnType.StringColumn,
          columnWidth: ColumnWidth.SmallColumn,
        },
        {
          displayedColumn: 'payments.accounts.table.sfc.date',
          elementProperty: 'submissionToSfcDate',
          columnType: ColumnType.DateOnlyColumn,
          columnWidth: ColumnWidth.DateColumn,
        }
      ]
    });

    this.tableConfiguration.routerLink = `/app/payments/accounts`;
  }
}
