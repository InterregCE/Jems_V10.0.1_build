import {ChangeDetectionStrategy, Component} from '@angular/core';
import {catchError, filter, map, switchMap, take, tap} from 'rxjs/operators';
import {UntilDestroy} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {
  PageProjectPartnerReportProcurementDTO, ProjectPartnerReportDTO, ProjectPartnerReportProcurementDTO
} from '@cat/api';
import {
  PartnerReportProcurementsPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-procurements-tab/partner-report-procurement-page-store.service';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {RoutingService} from '@common/services/routing.service';
import {MatTableDataSource} from '@angular/material/table';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {Alert} from '@common/components/forms/alert';
import {APIError} from '@common/models/APIError';

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

  dataSource: MatTableDataSource<ProjectPartnerReportProcurementDTO> = new MatTableDataSource([]);

  data$: Observable<{
    procurements: PageProjectPartnerReportProcurementDTO;
    limitReached: boolean;
    isReportEditable: boolean;
    isReportReopenedLimited: boolean;
  }>;
  error$ = new BehaviorSubject<APIError | null>(null);

  constructor(
    public pageStore: PartnerReportProcurementsPageStore,
    private routingService: RoutingService,
    private reportDetailPageStore: PartnerReportDetailPageStore,
    private dialog: MatDialog,
  ) {
    this.data$ = combineLatest([
      this.pageStore.page$,
      this.reportDetailPageStore.reportEditable$,
      this.pageStore.currentReport$
    ]).pipe(
      tap(([procurements, isEditable]) => this.prepareVisibleColumns(isEditable)),
      tap(([procurements, isEditable]) => this.dataSource.data = procurements.content),
      map(([procurements, isReportEditable, currentReport]) => ({
        procurements,
        limitReached: procurements.totalElements >= 50,
        isReportEditable,
        isReportReopenedLimited: currentReport.status === ProjectPartnerReportDTO.StatusEnum.ReOpenSubmittedLimited || currentReport.status === ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLimited
      })),
    );
  }

  private prepareVisibleColumns(isEditable: boolean) {
    this.displayedColumns.splice(0);
    (isEditable ? this.allColumns : this.readonlyColumns).forEach(column => {
      this.displayedColumns.push(column);
    });
  }

  deleteProcurement(procurement: ProjectPartnerReportProcurementDTO): void {
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
        catchError(err => this.showErrorMessage(err?.error)),
      ).subscribe();
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      if (this.error$.value?.id === error.id) {
        this.error$.next(null);
      }
    }, 10000);

    return of(null);
  }

}
