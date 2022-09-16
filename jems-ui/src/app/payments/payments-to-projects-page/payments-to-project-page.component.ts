import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {PagePaymentToProjectDTO} from '@cat/api';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {PaymentsToProjectPageStore} from './payments-to-projects-page.store';
import {map} from 'rxjs/operators';
import {NumberService} from '@common/services/number.service';

@Component({
  selector: 'jems-payments-to-projects-page',
  templateUrl: './payments-to-projects-page.component.html',
  styleUrls: ['./payments-to-projects-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaymentsToProjectPageComponent implements OnInit {

  data$: Observable<{
    userCanView: boolean;
    page: PagePaymentToProjectDTO;
    tableConfiguration: TableConfiguration;
  }>;

  tableConfiguration: TableConfiguration = new TableConfiguration({
    isTableClickable: false,
    columns: [
      {
        displayedColumn: 'payments.payment.to.project.table.column.id',
        elementProperty: 'paymentId',
        sortProperty: 'id',
        columnWidth: ColumnWidth.IdColumn,
      },
      {
        displayedColumn: 'payments.payment.to.project.table.column.payment.type',
        elementProperty: 'paymentType',
        sortProperty: 'paymentType',
        columnWidth: ColumnWidth.SmallColumn,
      },
      {
        displayedColumn: 'payments.payment.to.project.table.column.project.id',
        elementProperty: 'projectId',
        sortProperty: 'project_id',
        columnWidth: ColumnWidth.DateColumn,
      },
      {
        displayedColumn: 'payments.payment.to.project.table.column.project.acronym',
        elementProperty: 'projectAcronym',
        sortProperty: 'acronym',
        columnWidth: ColumnWidth.ChipColumn,
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
        sortProperty: 'payment_claim_submission_date',
        infoMessage: 'payments.payment.to.project.table.column.payment.claim.submission.date.info'
      },
      {
        displayedColumn: 'payments.payment.to.project.table.column.payment.claim.approval.date',
        columnType: ColumnType.DateOnlyColumn,
        columnWidth: ColumnWidth.WideColumn,
        elementProperty: 'paymentApprovalDate',
        sortProperty: 'payment_approval_date',
        infoMessage: 'payments.payment.to.project.table.column.payment.claim.approval.date.info'
      },
      {
        displayedColumn: 'payments.payment.to.project.table.column.total.eligible.amount',
        elementProperty: 'totalEligibleAmount',
        columnWidth: ColumnWidth.ChipColumn,
        alternativeValue: '3232',
        columnType: ColumnType.DecimalWithJustifiedStart,
        infoMessage: 'payments.payment.to.project.table.column.total.eligible.amount.info'
      },
      {
        displayedColumn: 'payments.payment.to.project.table.column.fund',
        elementProperty: 'fundName',
        sortProperty: 'programme_fund_id',
        columnWidth: ColumnWidth.SmallColumn,
        infoMessage: 'payments.payment.to.project.table.column.fund.info'
      },
      {
        displayedColumn: 'payments.payment.to.project.table.column.amount.approved.per.fund',
        elementProperty: 'amountApprovedPerFound',
        sortProperty: 'amount_approved_per_fund',
        columnWidth: ColumnWidth.ChipColumn,
        columnType: ColumnType.DecimalWithJustifiedStart,
        infoMessage: 'payments.payment.to.project.table.column.amount.approved.per.fund.info'
      },
      {
        displayedColumn: 'payments.payment.to.project.table.column.amount.paid.per.fund',
        elementProperty: 'amountPaidPerFund',
        sortProperty: 'amountPaidPerFund',
        columnWidth: ColumnWidth.ChipColumn,
        columnType: ColumnType.DecimalWithJustifiedStart
      },
      {
        displayedColumn: 'payments.payment.to.project.table.column.last.payment.date',
        columnType: ColumnType.DateOnlyColumn,
        columnWidth: ColumnWidth.DateColumn,
        elementProperty: 'dateOfLastPayment',
        sortProperty: 'dateOfLastPayment'
      }
    ]
  });

  constructor(public paymentToProjectsStore: PaymentsToProjectPageStore) {
  }

  ngOnInit(): void {
    this.data$ = combineLatest([
      this.paymentToProjectsStore.paymentToProjectDTO$,
      this.paymentToProjectsStore.userCanView$
    ])
      .pipe(
        map(([page, userCanView]) => ({page: this.truncateAmounts(page), userCanView, tableConfiguration: this.tableConfiguration})),
      );
  }

  truncateAmounts(dto: PagePaymentToProjectDTO): PagePaymentToProjectDTO {
    for (const paymentToProject of dto.content) {
      paymentToProject.totalEligibleAmount = NumberService.truncateNumber(paymentToProject.totalEligibleAmount);
      paymentToProject.amountPaidPerFund = NumberService.truncateNumber(paymentToProject.amountPaidPerFund);
      paymentToProject.amountApprovedPerFound = NumberService.truncateNumber(paymentToProject.amountApprovedPerFound);
    }
    return dto;
  }
}
