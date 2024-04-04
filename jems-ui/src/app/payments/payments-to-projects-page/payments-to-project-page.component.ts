import {AfterViewInit, ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {PagePaymentToProjectDTO, PaymentSearchRequestDTO, ProgrammeFundDTO, ProgrammeFundService} from '@cat/api';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {PaymentsToProjectPageStore} from './payments-to-projects-page.store';
import {map, startWith, tap} from 'rxjs/operators';
import {FormBuilder} from '@angular/forms';
import {PaymentsPageSidenavService} from '../payments-page-sidenav.service';

@Component({
  selector: 'jems-payments-to-projects-page',
  templateUrl: './payments-to-project-page.component.html',
  styleUrls: ['./payments-to-project-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaymentsToProjectPageComponent implements OnInit, AfterViewInit {

  PaymentTypeEnum = PaymentSearchRequestDTO.PaymentTypeEnum;

  @ViewChild('idCell', {static: true})
  idCell: TemplateRef<any>;

  @ViewChild('typeCell', {static: true})
  typeCell: TemplateRef<any>;

  @ViewChild('claimNoCell', {static: true})
  claimNoCell: TemplateRef<any>;

  @ViewChild('paymentToEcCell', {static: true})
  paymentToEcCell: TemplateRef<any>;

  @ViewChild('fundCell', {static: true})
  fundCell: TemplateRef<any>;

  data$: Observable<{
    userCanView: boolean;
    page: PagePaymentToProjectDTO;
    tableConfiguration: TableConfiguration;
    availableFunds: Map<number, string>;
  }>;

  filtersActive = false;
  filterForm = this.formBuilder.group({
    paymentId: [null],
    paymentType: [null],
    projectIdentifiers: [[]],
    projectAcronym: [null],
    claimSubmissionDateFrom: [null],
    claimSubmissionDateTo: [null],
    approvalDateFrom: [null],
    approvalDateTo: [null],
    fundIds: [[]],
    lastPaymentDateFrom: [null],
    lastPaymentDateTo: [null],
  });
  defaultFilter = JSON.stringify(this.filterForm.value);

  filterChanges = this.filterForm.valueChanges.pipe(
    startWith(this.filterForm.value),
    tap(filters => this.filtersActive = this.defaultFilter !== JSON.stringify(filters)),
    tap(filters => this.paymentToProjectsStore.filter$.next(this.transformFiltersToSearchDto(filters))),
  );

  tableConfiguration: TableConfiguration;

  constructor(
    public paymentToProjectsStore: PaymentsToProjectPageStore,
    private formBuilder: FormBuilder,
    private programmeFundService: ProgrammeFundService,
    private paymentsPageSidenav: PaymentsPageSidenavService
  ) {
  }

  ngOnInit(): void {
    this.data$ = combineLatest([
      this.paymentToProjectsStore.paymentToProjectDTO$,
      this.paymentToProjectsStore.userCanView$,
      this.programmeFundService.getProgrammeFundList(),
      this.filterChanges,
    ])
      .pipe(
        map(([page, userCanView, funds]) => ({
          page,
          userCanView,
          tableConfiguration: this.tableConfiguration,
          availableFunds: new Map(funds
            .filter(fund => fund.selected)
            .map(fund =>
              [fund.id, fund.type !== ProgrammeFundDTO.TypeEnum.OTHER ? fund.type : `${fund.type} (${fund.id})`]
            )
          ),
        })),
      );
  }

  ngAfterViewInit(): void {
    this.tableConfiguration = new TableConfiguration({
      isTableClickable: true,
      sortable: true,
      columns: [
        {
          displayedColumn: 'payments.payment.to.project.table.column.id',
          sortProperty: 'id',
          columnWidth: ColumnWidth.IdColumn,
          customCellTemplate: this.idCell,
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.payment.type',
          sortProperty: 'type',
          columnWidth: ColumnWidth.SmallColumn,
          customCellTemplate: this.typeCell,
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.project.id',
          elementProperty: 'projectCustomIdentifier',
          sortProperty: 'projectCustomIdentifier',
          columnWidth: ColumnWidth.DateColumn,
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.project.acronym',
          elementProperty: 'projectAcronym',
          sortProperty: 'projectAcronym',
          columnWidth: ColumnWidth.WideColumn,
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.payment.claim.no',
          customCellTemplate: this.claimNoCell,
          sortProperty: 'projectReport.number',
          columnWidth: ColumnWidth.SmallColumn,
          infoMessage: 'payments.payment.to.project.table.column.payment.claim.no.info'
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.payment.payment.to.ec',
          customCellTemplate: this.paymentToEcCell,
          sortProperty: 'ecPaymentId',
          columnWidth: ColumnWidth.SmallColumn,
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.payment.claim.submission.date',
          columnType: ColumnType.DateOnlyColumn,
          columnWidth: ColumnWidth.DateColumn,
          elementProperty: 'paymentClaimSubmissionDate',
          sortProperty: 'project.contractedDecision.updated',
          infoMessage: 'payments.payment.to.project.table.column.payment.claim.submission.date.info'
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.payment.claim.approval.date',
          columnType: ColumnType.DateOnlyColumn,
          columnWidth: ColumnWidth.DateColumn,
          elementProperty: 'paymentApprovalDate',
          sortProperty: 'projectReport.paymentApprovalDate',
          infoMessage: 'payments.payment.to.project.table.column.payment.claim.approval.date.info'
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.total.eligible.amount',
          elementProperty: 'totalEligibleAmount',
          sortProperty: 'totalEligible',
          columnWidth: ColumnWidth.ChipColumn,
          columnType: ColumnType.Decimal,
          infoMessage: 'payments.payment.to.project.table.column.total.eligible.amount.info'
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.fund',
          customCellTemplate: this.fundCell,
          sortProperty: 'fund.type',
          columnWidth: ColumnWidth.MediumColumn,
          infoMessage: 'payments.payment.to.project.table.column.fund.info'
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.amount.approved.per.fund',
          elementProperty: 'fundAmount',
          sortProperty: 'fundAmount',
          columnWidth: ColumnWidth.ChipColumn,
          columnType: ColumnType.Decimal,
          infoMessage: 'payments.payment.to.project.table.column.amount.approved.per.fund.info'
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.authorised',
          elementProperty: 'amountAuthorizedPerFund',
          sortProperty: 'authorized',
          columnWidth: ColumnWidth.ChipColumn,
          columnType: ColumnType.Decimal,
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.amount.paid.per.fund',
          elementProperty: 'amountPaidPerFund',
          sortProperty: 'paid',
          columnWidth: ColumnWidth.ChipColumn,
          columnType: ColumnType.Decimal
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.last.payment.date',
          elementProperty: 'dateOfLastPayment',
          sortProperty: 'dateOfLastPayment',
          columnType: ColumnType.DateOnlyColumn,
          columnWidth: ColumnWidth.DateColumn,
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.remaining.to.be.paid',
          elementProperty: 'remainingToBePaid',
          sortProperty: 'remainingToBePaid',
          columnWidth: ColumnWidth.ChipColumn,
          columnType: ColumnType.Decimal,
        },
      ]
    });

    this.tableConfiguration.routerLink = `/app/payments/paymentsToProjects`;
  }

  private transformFiltersToSearchDto(filters: any): PaymentSearchRequestDTO {
    return {
      ...filters,
    } as PaymentSearchRequestDTO;
  }
}
