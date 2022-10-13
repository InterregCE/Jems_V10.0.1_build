import {AfterViewInit, ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
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
export class PaymentsToProjectPageComponent implements OnInit, AfterViewInit {

  @ViewChild('remainingToBePaidCell', {static: true})
  remainingToBePaidCell: TemplateRef<any>;

  data$: Observable<{
    userCanView: boolean;
    page: PagePaymentToProjectDTO;
    tableConfiguration: TableConfiguration;
  }>;

  tableConfiguration: TableConfiguration;

  constructor(public paymentToProjectsStore: PaymentsToProjectPageStore) {
  }

  ngOnInit(): void {
    this.data$ = combineLatest([
      this.paymentToProjectsStore.paymentToProjectDTO$,
      this.paymentToProjectsStore.userCanView$
    ])
      .pipe(
        map(([page, userCanView]) => ({page, userCanView, tableConfiguration: this.tableConfiguration})),
      );
  }

  ngAfterViewInit(): void {
    this.tableConfiguration = new TableConfiguration({
      isTableClickable: true,
      sortable: true,
      columns: [
        {
          displayedColumn: 'payments.payment.to.project.table.column.id',
          elementProperty: 'id',
          sortProperty: 'id',
          columnWidth: ColumnWidth.IdColumn,
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.payment.type',
          elementProperty: 'paymentType',
          sortProperty: 'type',
          columnWidth: ColumnWidth.SmallColumn,
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
          sortProperty: 'project.contractedDecision.updated',
          infoMessage: 'payments.payment.to.project.table.column.payment.claim.submission.date.info'
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.payment.claim.approval.date',
          columnType: ColumnType.DateOnlyColumn,
          columnWidth: ColumnWidth.WideColumn,
          elementProperty: 'paymentApprovalDate',
          infoMessage: 'payments.payment.to.project.table.column.payment.claim.approval.date.info'
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.total.eligible.amount',
          elementProperty: 'totalEligibleAmount',
          columnWidth: ColumnWidth.ChipColumn,
          columnType: ColumnType.DecimalWithJustifiedStart,
          infoMessage: 'payments.payment.to.project.table.column.total.eligible.amount.info'
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.fund',
          elementProperty: 'fundName',
          sortProperty: 'fund.type',
          columnWidth: ColumnWidth.SmallColumn,
          infoMessage: 'payments.payment.to.project.table.column.fund.info'
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.amount.approved.per.fund',
          elementProperty: 'amountApprovedPerFund',
          columnWidth: ColumnWidth.ChipColumn,
          columnType: ColumnType.DecimalWithJustifiedStart,
          infoMessage: 'payments.payment.to.project.table.column.amount.approved.per.fund.info'
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.amount.paid.per.fund',
          elementProperty: 'amountPaidPerFund',
          columnWidth: ColumnWidth.ChipColumn,
          columnType: ColumnType.DecimalWithJustifiedStart
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.last.payment.date',
          columnType: ColumnType.DateOnlyColumn,
          columnWidth: ColumnWidth.DateColumn,
          elementProperty: 'dateOfLastPayment',
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.remaining.to.be.paid',
          columnType: ColumnType.CustomComponent,
          columnWidth: ColumnWidth.ChipColumn,
          customCellTemplate: this.remainingToBePaidCell
        }
      ]
    });

    this.tableConfiguration.routerLink = `/app/payments`;
  }

  truncateAmounts(dto: PagePaymentToProjectDTO): PagePaymentToProjectDTO {
    for (const paymentToProject of dto.content) {
      paymentToProject.totalEligibleAmount = NumberService.truncateNumber(paymentToProject.totalEligibleAmount);
      paymentToProject.amountPaidPerFund = NumberService.truncateNumber(paymentToProject.amountPaidPerFund);
      paymentToProject.amountApprovedPerFund = NumberService.truncateNumber(paymentToProject.amountApprovedPerFund);
    }
    return dto;
  }
}
