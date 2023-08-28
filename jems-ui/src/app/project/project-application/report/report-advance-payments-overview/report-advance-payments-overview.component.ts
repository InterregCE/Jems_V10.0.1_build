import {AfterViewInit, ChangeDetectionStrategy, Component, TemplateRef, ViewChild} from '@angular/core';
import {AdvancePaymentDTO, InputTranslation, PageAdvancePaymentDTO} from '@cat/api';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {
  ProjectAdvancePaymentsPageStore
} from '@project/project-application/report/report-advance-payments-overview/project-advance-payments-page.store';

@Component({
  selector: 'jems-report-advance-payments-overview',
  templateUrl: './report-advance-payments-overview.component.html',
  styleUrls: ['./report-advance-payments-overview.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReportAdvancePaymentsOverviewComponent implements AfterViewInit {

  @ViewChild('partnerRoleCell', {static: true})
  partnerRoleCell: TemplateRef<any>;

  @ViewChild('sourceForAdvanceCell', {static: true})
  sourceForAdvanceCell: TemplateRef<any>;

  @ViewChild('remainingToBeSettledCell', {static: true})
  remainingToBeSettledCell: TemplateRef<any>;

  @ViewChild('advanceAmountSettledCell', {static: true})
  advanceAmountSettledCell: TemplateRef<any>;

  @ViewChild('advanceAmountPaidCell', {static: true})
  advanceAmountPaidCell: TemplateRef<any>;

  SOURCE_TYPE = {
    fund: 'fund',
    contribution: 'contribution'
  };

  data$: Observable<{
    page: PageAdvancePaymentDTO;
    tableConfiguration: TableConfiguration;
  }>;

  tableConfiguration: TableConfiguration;

  constructor(public advancePaymentsStore: ProjectAdvancePaymentsPageStore,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService) {
    this.data$ = combineLatest([
      this.advancePaymentsStore.projectAdvancePaymentDTO$
    ]).pipe(
      map(([page]) => ({page, tableConfiguration: this.tableConfiguration})),
    );
  }

  ngAfterViewInit(): void {
    this.tableConfiguration = new TableConfiguration({
      isTableClickable: false,
      sortable: true,
      columns: [
        {
          displayedColumn: 'payments.payment.to.project.table.column.id',
          elementProperty: 'id',
          sortProperty: 'id',
          columnWidth: ColumnWidth.IdColumn,
        },
        {
          displayedColumn: 'payments.advance.payment.table.header.partner.number',
          columnType: ColumnType.CustomComponent,
          columnWidth: ColumnWidth.SmallColumn,
          customCellTemplate: this.partnerRoleCell,
          sortProperty: 'partnerSortNumber',
        },
        {
          displayedColumn: 'payments.advance.payment.table.header.partner.name',
          elementProperty: 'partnerAbbreviation',
          sortProperty: 'partnerAbbreviation',
          columnWidth: ColumnWidth.WideColumn,
        },
        {
          displayedColumn: 'payments.advance.payment.table.header.source.advance.granted',
          columnWidth: ColumnWidth.MediumColumn,
          customCellTemplate: this.sourceForAdvanceCell,
        },
        {
          displayedColumn: 'payments.advance.payment.table.header.advance.amount',
          columnType: ColumnType.CustomComponent,
          columnWidth: ColumnWidth.ChipColumn,
          elementProperty: 'amountPaid',
          customCellTemplate: this.advanceAmountPaidCell,
        },
        {
          displayedColumn: 'payments.advance.payment.table.header.date.advance.payment',
          columnType: ColumnType.DateOnlyColumn,
          columnWidth: ColumnWidth.DateColumn,
          elementProperty: 'paymentDate',
          sortProperty: 'paymentDate'
        },
        {
          displayedColumn: 'payments.advance.payment.table.header.amount.settled',
          elementProperty: 'amountSettled',
          columnWidth: ColumnWidth.ChipColumn,
          customCellTemplate: this.advanceAmountSettledCell
        },
        {
          displayedColumn: 'payments.advance.payment.table.header.remaining.amount.to.be.settled',
          columnType: ColumnType.CustomComponent,
          columnWidth: ColumnWidth.ChipColumn,
          customCellTemplate: this.remainingToBeSettledCell
        },
      ]
    });
  }

  getContributionName(paymentAdvance: AdvancePaymentDTO): String {
    if (paymentAdvance.partnerContribution?.id) {
      return paymentAdvance.partnerContribution?.name;
    } else if (paymentAdvance.partnerContributionSpf?.id) {
      return paymentAdvance.partnerContributionSpf.name;
    }
    return '';
  }

  getFundName(paymentAdvance: any): InputTranslation[] | null {
    return paymentAdvance.programmeFund?.abbreviation;
  }

  getSourceType(paymentAdvance: any): string {
    if (paymentAdvance.programmeFund?.id) {
      return 'fund';
    } else {
      return 'contribution';
    }
  }
}
