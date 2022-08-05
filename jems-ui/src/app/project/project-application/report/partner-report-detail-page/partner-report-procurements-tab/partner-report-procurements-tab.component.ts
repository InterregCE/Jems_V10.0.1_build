import {ChangeDetectionStrategy, Component} from '@angular/core';
import {filter, map, switchMap, take, tap} from 'rxjs/operators';
import {UntilDestroy} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {
  PageProjectPartnerReportProcurementSummaryDTO, ProjectPartnerReportProcurementSummaryDTO
} from '@cat/api';
import {
  PartnerReportProcurementsPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurement-page-store.service';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {RoutingService} from '@common/services/routing.service';
import {MatTableDataSource} from "@angular/material/table";
import {Forms} from "@common/utils/forms";
import {MatDialog} from "@angular/material/dialog";
import {Alert} from '@common/components/forms/alert';

@UntilDestroy()
@Component({
  selector: 'jems-partner-procurements-cost',
  templateUrl: './partner-report-procurements-tab.component.html',
  styleUrls: ['./partner-report-procurements-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PartnerReportProcurementsTabComponent {
  Alert = Alert;

  private allColumns = ['reportNumber', 'lastChanged', 'contractName', 'referenceNumber', 'contractDate', 'contractType', 'contractAmount', 'currencyCode', 'supplierName', 'vatNumber', 'delete'];
  private readonlyColumns = this.allColumns.filter(col => col !== 'delete');
  displayedColumns: string[] = [];

  dataSource: MatTableDataSource<ProjectPartnerReportProcurementSummaryDTO> = new MatTableDataSource([]);

  data$: Observable<{
    procurements: PageProjectPartnerReportProcurementSummaryDTO;
    limitReached: boolean;
    isReportEditable: boolean;
  }>;

  constructor(
    public pageStore: PartnerReportProcurementsPageStore,
    private routingService: RoutingService,
    private reportDetailPageStore: PartnerReportDetailPageStore,
    private dialog: MatDialog,
  ) {
    this.data$ = combineLatest([
      this.pageStore.page$,
      this.reportDetailPageStore.reportEditable$,
    ]).pipe(
      tap(([procurements, isEditable]) => this.prepareVisibleColumns(isEditable)),
      tap(([procurements, isEditable]) => this.dataSource.data = procurements.content),
      map(([procurements, isReportEditable]) => ({
        procurements,
        limitReached: procurements.totalElements >= 50,
        isReportEditable,
      })),
    );
  }

  private prepareVisibleColumns(isEditable: boolean) {
    this.displayedColumns.splice(0);
    (isEditable ? this.allColumns : this.readonlyColumns).forEach(column => {
      this.displayedColumns.push(column);
    });
  }

  deleteProcurement(procurement: ProjectPartnerReportProcurementSummaryDTO): void {
    Forms.confirm(
      this.dialog, {
        title: procurement.contractName,
        message: {
          i18nKey: 'project.application.partner.report.procurements.delete.message',
          i18nArguments: { contractName: procurement.contractName, reportNumber: procurement.reportNumber.toString() },
        },
      }).pipe(
        take(1),
        filter(answer => !!answer),
        switchMap(() => this.pageStore.deleteProcurement(procurement.id)),
        take(1),
      ).subscribe();
  }

}
