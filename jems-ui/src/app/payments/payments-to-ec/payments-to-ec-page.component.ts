import {AfterViewInit, ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {PagePaymentApplicationToEcDTO} from '@cat/api';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {filter, map, take, tap} from 'rxjs/operators';
import {PaymentsToEcPageStore} from './payments-to-ec-page.store';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {PaymentsPageSidenavService} from '../payments-page-sidenav.service';

@UntilDestroy()
@Component({
  selector: 'jems-payments-applications-to-ec-page',
  templateUrl: './payments-to-ec-page.component.html',
  styleUrls: ['./payments-to-ec-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaymentsToEcPageComponent implements OnInit, AfterViewInit {

  @ViewChild('fundCell', {static: true})
  fundCell: TemplateRef<any>;

  @ViewChild('accountingYearCell', {static: true})
  accountingYearCell: TemplateRef<any>;

  @ViewChild('deleteButtonCell', {static: true})
  deleteButtonCell: TemplateRef<any>;

  data$: Observable<{
    userCanView: boolean;
    page: PagePaymentApplicationToEcDTO;
    tableConfiguration: TableConfiguration;
  }>;
  userCanEdit$: Observable<boolean>;
  tableConfiguration: TableConfiguration;

  constructor(public paymentsToEcPageStore: PaymentsToEcPageStore,
              private dialog: MatDialog,
              private paymentsPageSidenav: PaymentsPageSidenavService) {
  }

  ngOnInit(): void {
    this.userCanEdit$ = this.paymentsToEcPageStore.userCanEdit$;
    this.data$ = combineLatest([
      this.paymentsToEcPageStore.paymentToEcPage$,
      this.paymentsToEcPageStore.userCanView$,
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
          displayedColumn: 'common.id',
          elementProperty: 'id',
          sortProperty: 'id',
          columnWidth: ColumnWidth.IdColumn,
        },
        {
          displayedColumn: 'project.partner.budget.table.fund',
          sortProperty: 'programmeFund',
          columnWidth: ColumnWidth.MediumColumn,
          customCellTemplate: this.fundCell
        },
        {
          displayedColumn: 'payments.to.ec.table.header.accounting.year',
          sortProperty: 'accountingYear',
          columnWidth: ColumnWidth.WideColumn,
          customCellTemplate: this.accountingYearCell
        },
        {
          displayedColumn: 'common.status',
          elementProperty: 'status',
          sortProperty: 'status',
          columnWidth: ColumnWidth.SmallColumn,
        }
      ]
    });

    this.paymentsToEcPageStore.userCanEdit$.pipe(
      tap(userCanEdit => {
          if (userCanEdit) {
            this.tableConfiguration.columns.push({
              displayedColumn: 'common.delete.entry',
              columnType: ColumnType.CustomComponent,
              columnWidth: ColumnWidth.DeletionColumn,
              customCellTemplate: this.deleteButtonCell
            });
          }
        }
      ),
      untilDestroyed(this)
    ).subscribe();

    this.tableConfiguration.routerLink = `/app/payments/paymentApplicationsToEc/`;
  }

  removeItem(paymentId: number) {
    Forms.confirm(this.dialog, {
      title: 'payments.advance.table.action.delete.dialog.header',
      message: 'payments.to.ec.table.action.delete.dialog.message',
      warnMessage: 'payments.to.ec.table.action.delete.dialog.warning'
    }).pipe(
      take(1),
      filter(yes => yes),
      tap(() => this.paymentsToEcPageStore.deletePaymentToEc(paymentId))
    ).subscribe();
  }

}

