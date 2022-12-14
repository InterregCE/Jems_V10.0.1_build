import {Component} from '@angular/core';
import {ControlWorkOverviewDTO, ProjectPartnerReportDTO} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {catchError, finalize, map, tap} from 'rxjs/operators';
import {
  PartnerControlReportOverviewAndFinalizeStore
} from '@project/project-application/report/partner-control-report/partner-control-report-overview-and-finalize-tab/partner-control-report-overview-and-finalize.store';
import {FormService} from '@common/components/section/form/form.service';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {APIError} from '@common/models/APIError';
import {ActivatedRoute, Router} from '@angular/router';
import {Alert} from '@common/components/forms/alert';

@Component({
  selector: 'jems-partner-control-report-overview-and-finalize-tab',
  templateUrl: './partner-control-report-overview-and-finalize-tab.component.html',
  styleUrls: ['./partner-control-report-overview-and-finalize-tab.component.scss'],
  providers: [FormService],
})
export class PartnerControlReportOverviewAndFinalizeTabComponent {
  Alert = Alert;
  displayedColumns = ['declaredByPartner', 'inControlSample', 'parked', 'deductedByControl', 'eligibleAfterControl', 'eligibleAfterControlPercentage'];

  finalizationLoading = false;
  error$ = new BehaviorSubject<APIError | null>(null);

  data$: Observable<{
    dataSource: MatTableDataSource<ControlWorkOverviewDTO>;
    finalizationAllowed: boolean;
    reportId: number;
    partnerId: number;
  }>;
  constructor(
    private pageStore: PartnerControlReportOverviewAndFinalizeStore,
    private reportPageStore: PartnerReportDetailPageStore,
    private router: Router,
    private route: ActivatedRoute,
  ) {
    this.data$ = combineLatest([
      this.pageStore.controlWorkOverview$,
      this.reportPageStore.partnerReport$,
      this.reportPageStore.partnerId$.pipe(map(id => Number(id))),
    ]).pipe(
      map(([data, report, partnerId]) => ({
        dataSource: new MatTableDataSource([data]),
        finalizationAllowed: report.status === ProjectPartnerReportDTO.StatusEnum.InControl,
        reportId: report.id,
        partnerId,
      })),
    );
  }

  finalizeReport(partnerId: number, reportId: number): void {
    this.finalizationLoading = true;
    this.reportPageStore.finalizeReport(partnerId, reportId)
      .pipe(
        tap(() => this.redirectToReportList()),
        catchError((error) => this.showErrorMessage(error.error)),
        finalize(() => this.finalizationLoading = false)
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

  private redirectToReportList(): void {
    this.router.navigate(['../../..'], { relativeTo: this.route });
  }

}
