import {AfterViewInit, ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {PageAdvancePaymentDTO} from '@cat/api';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {map} from 'rxjs/operators';
import {AdvancePaymentsPageStore} from './advance-payments-page.store';

@Component({
  selector: 'jems-advanced-payments-page',
  templateUrl: './advance-payments-page.component.html',
  styleUrls: ['./advance-payments-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdvancePaymentsPageComponent implements OnInit, AfterViewInit {

  @ViewChild('partnerRoleCell', {static: true})
  partnerRoleCell: TemplateRef<any>;

  @ViewChild('remainingToBeSettledCell', {static: true})
  remainingToBeSettledCell: TemplateRef<any>;

  @ViewChild('deleteButtonCell', {static: true})
  deleteButtonCell: TemplateRef<any>;

  data$: Observable<{
    userCanView: boolean;
    page: PageAdvancePaymentDTO;
    tableConfiguration: TableConfiguration;
  }>;
  userCanEdit$: Observable<boolean>;

  tableConfiguration: TableConfiguration;

  constructor(public advancePaymentsStore: AdvancePaymentsPageStore) {

  }

  ngOnInit(): void {
    this.userCanEdit$ = this.advancePaymentsStore.userCanEdit$;

    this.data$ = combineLatest([
      this.advancePaymentsStore.advancePaymentDTO$,
      this.advancePaymentsStore.userCanView$,
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
          displayedColumn: 'payments.advance.payment.table.header.project.id',
          elementProperty: 'projectCustomIdentifier',
          sortProperty: 'projectCustomIdentifier',
          columnWidth: ColumnWidth.MediumColumn,
        },
        {
          displayedColumn: 'payments.payment.to.project.table.column.project.acronym',
          elementProperty: 'projectAcronym',
          sortProperty: 'projectAcronym',
          columnWidth: ColumnWidth.ChipColumn,
        },
        {
          displayedColumn: 'payments.advance.payment.table.header.partner.role',
          columnType: ColumnType.CustomComponent,
          columnWidth: ColumnWidth.SmallColumn,
          customCellTemplate: this.partnerRoleCell
        },
        {
          displayedColumn: 'payments.advance.payment.table.header.partner.name',
          elementProperty: 'partnerAbbreviation',
          sortProperty: 'partnerAbbreviation',
          columnWidth: ColumnWidth.ChipColumn,
        },
        {
          displayedColumn: 'payments.advance.payment.table.header.source.advance.granted',
          elementProperty: 'sourceOrFundName',
          sortProperty:'sourceOrFundName',
          columnWidth: ColumnWidth.SmallColumn,
        },
        {
          displayedColumn: 'payments.advance.payment.table.header.advance.amount',
          columnWidth: ColumnWidth.MediumColumn,
          elementProperty: 'amountAdvance',
          sortProperty: 'amountAdvance',
        },
        {
          displayedColumn: 'payments.advance.payment.table.header.date.advance.payment',
          columnType: ColumnType.DateOnlyColumn,
          columnWidth: ColumnWidth.DateColumn,
          elementProperty: 'dateOfPayment',
          sortProperty: 'dateOfPayment'
        },
        {
          displayedColumn: 'payments.advance.payment.table.header.amount.settled',
          elementProperty: 'amountSettled',
          columnWidth: ColumnWidth.ChipColumn,
        },
        {
          displayedColumn: 'payments.advance.payment.table.header.remaining.amount.to.be.settled',
          columnType: ColumnType.CustomComponent,
          columnWidth: ColumnWidth.ChipColumn,
          customCellTemplate: this.remainingToBeSettledCell
        },
        {
          displayedColumn: 'payments.advance.payment.table.header.actions.delete',
          columnType: ColumnType.CustomComponent,
          columnWidth: ColumnWidth.SmallColumn,
          customCellTemplate: this.deleteButtonCell
        },
      ]
    });

    this.tableConfiguration.routerLink = `/app/payments/advancePayments/`;
  }

  removeItem(paymentId: number) {
      this.advancePaymentsStore.deleteAdvancedPayment(paymentId);
  }
}

