import {AfterViewInit, ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {PagePaymentToProjectDTO, PaymentSearchRequestDTO, ProgrammeFundDTO, ProgrammeFundService} from '@cat/api';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {PaymentsToProjectPageStore} from './payments-to-projects-page.store';
import {map, startWith, tap} from 'rxjs/operators';
import {FormBuilder} from "@angular/forms";

@Component({
  selector: 'jems-payments-to-projects-page',
  templateUrl: './payments-to-project-page.component.html',
  styleUrls: ['./payments-to-project-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaymentsToProjectPageComponent implements OnInit, AfterViewInit {

  PaymentTypeEnum = PaymentSearchRequestDTO.PaymentTypeEnum;

  @ViewChild('remainingToBePaidCell', {static: true})
  remainingToBePaidCell: TemplateRef<any>;

  @ViewChild('idCell', {static: true})
  idCell: TemplateRef<any>;

  @ViewChild('typeCell', {static: true})
  typeCell: TemplateRef<any>;

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
          elementProperty: 'paymentClaimNo',
          columnWidth: ColumnWidth.SmallColumn,
          infoMessage: 'payments.payment.to.project.table.column.payment.claim.no.info'
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
          infoMessage: 'payments.payment.to.project.table.column.payment.claim.approval.date.info'
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.total.eligible.amount',
          elementProperty: 'totalEligibleAmount',
          columnWidth: ColumnWidth.ChipColumn,
          columnType: ColumnType.Decimal,
          infoMessage: 'payments.payment.to.project.table.column.total.eligible.amount.info'
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.fund',
          elementProperty: 'fundName',
          sortProperty: 'fund.type',
          columnWidth: ColumnWidth.MediumColumn,
          infoMessage: 'payments.payment.to.project.table.column.fund.info'
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.amount.approved.per.fund',
          elementProperty: 'amountApprovedPerFund',
          columnWidth: ColumnWidth.ChipColumn,
          columnType: ColumnType.Decimal,
          infoMessage: 'payments.payment.to.project.table.column.amount.approved.per.fund.info'
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.amount.paid.per.fund',
          elementProperty: 'amountPaidPerFund',
          columnWidth: ColumnWidth.ChipColumn,
          columnType: ColumnType.Decimal
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.last.payment.date',
          columnType: ColumnType.DateOnlyColumn,
          columnWidth: ColumnWidth.DateColumn,
          elementProperty: 'dateOfLastPayment',
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.remaining.to.be.paid',
          columnType: ColumnType.Decimal,
          columnWidth: ColumnWidth.ChipColumn,
          customCellTemplate: this.remainingToBePaidCell
        }
      ]
    });

    this.tableConfiguration.routerLink = `/app/payments`;
  }

  private transformFiltersToSearchDto(filters: any): PaymentSearchRequestDTO {
    return {
      ...filters,
    } as PaymentSearchRequestDTO;
  }
}
