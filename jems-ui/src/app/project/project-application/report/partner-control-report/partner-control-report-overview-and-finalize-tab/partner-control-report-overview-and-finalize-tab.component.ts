import {Component} from '@angular/core';
import {
  ControlOverviewDTO,
  ControlWorkOverviewDTO,
  ProjectPartnerReportControlOverviewService,
  ProjectPartnerReportDTO
} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {catchError, finalize, map, take, tap} from 'rxjs/operators';
import {
  PartnerControlReportOverviewAndFinalizeStore
} from '@project/project-application/report/partner-control-report/partner-control-report-overview-and-finalize-tab/partner-control-report-overview-and-finalize.store';
import {
  PartnerControlReportStore
} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';
import {FormService} from '@common/components/section/form/form.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {APIError} from '@common/models/APIError';
import {ActivatedRoute, Router} from '@angular/router';
import {Alert} from '@common/components/forms/alert';
import {PartnerReportPageStore} from '@project/project-application/report/partner-report-page-store.service';
import {LocaleDatePipe} from '@common/pipe/locale-date.pipe';

@Component({
  selector: 'jems-partner-control-report-overview-and-finalize-tab',
  templateUrl: './partner-control-report-overview-and-finalize-tab.component.html',
  styleUrls: ['./partner-control-report-overview-and-finalize-tab.component.scss'],
  providers: [FormService]
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
    userCanEdit: boolean;
    userCanView: boolean;
    controlReportOverview: ControlOverviewDTO;
  }>;

  overviewForm: FormGroup = this.formBuilder.group({
    controlWorkStartDate: [''],
    controlWorkEndDate: [''],
    requestsForClarifications: ['', Validators.maxLength(1000)],
    receiptOfSatisfactoryAnswers: ['', Validators.maxLength(1000)],
    findingDescription: ['', Validators.maxLength(5000)],
    followUpMeasuresFromLastReport: ['', Validators.maxLength(5000)],
    conclusion: ['', Validators.maxLength(5000)],
    followUpMeasuresForNextReport: ['', Validators.maxLength(5000)],
    previousFollowUpMeasuresFromLastReport: ['', Validators.maxLength(5000)],
    changedLastCertifiedReportEndDate: [''],
    lastCertifiedReportNumber: ['']
  });

  constructor(
    private formBuilder: FormBuilder,
    private formService: FormService,
    private pageStore: PartnerControlReportOverviewAndFinalizeStore,
    private reportDetailPageStore: PartnerReportDetailPageStore,
    private reportPageStore: PartnerReportPageStore,
    private router: Router,
    private route: ActivatedRoute,
    public store: PartnerControlReportStore,
    private projectPartnerReportControlOverviewService: ProjectPartnerReportControlOverviewService,
    private localeDatePipe: LocaleDatePipe
  ) {
    this.data$ = combineLatest([
      this.pageStore.controlWorkOverview$,
      this.reportDetailPageStore.partnerReport$,
      this.reportDetailPageStore.partnerId$.pipe(map(id => Number(id))),
      this.reportPageStore.institutionUserCanEditControlReports$,
      this.reportPageStore.institutionUserCanViewControlReports$,
      this.store.partnerControlReportOverview$
    ]).pipe(
      map(([data, report, partnerId , userCanEdit, userCanView,  controlReportOverview]) => ({
        dataSource: new MatTableDataSource([data]),
        finalizationAllowed: report.status === ProjectPartnerReportDTO.StatusEnum.InControl,
        reportId: report.id,
        partnerId,
        userCanEdit: userCanEdit,
        userCanView: userCanView,
        controlReportOverview
      })),
      tap(() => this.initForm()),
      tap(data => this.resetForm(data.controlReportOverview)),
      tap(data => this.disableForms(data.userCanEdit))
    );
  }

  private initForm(): void {
    this.formService.init(this.overviewForm);
    this.overviewForm.controls.controlWorkEndDate.disable();
    this.overviewForm.controls.previousFollowUpMeasuresFromLastReport.disable();
  }

  resetForm(partnerControlReport: ControlOverviewDTO): void {
    this.overviewForm.reset();
    this.overviewForm.controls.controlWorkStartDate.setValue(partnerControlReport.startDate);
    this.overviewForm.controls.requestsForClarifications.setValue(partnerControlReport.requestsForClarifications);
    this.overviewForm.controls.receiptOfSatisfactoryAnswers.setValue(partnerControlReport.receiptOfSatisfactoryAnswers);
    this.overviewForm.controls.controlWorkEndDate.setValue(partnerControlReport.endDate);
    this.overviewForm.controls.findingDescription.setValue(partnerControlReport.findingDescription);
    this.overviewForm.controls.followUpMeasuresFromLastReport.setValue(partnerControlReport.followUpMeasuresFromLastReport);
    this.overviewForm.controls.conclusion.setValue(partnerControlReport.conclusion);
    this.overviewForm.controls.followUpMeasuresForNextReport.setValue(partnerControlReport.followUpMeasuresForNextReport);
    this.overviewForm.controls.previousFollowUpMeasuresFromLastReport.setValue(partnerControlReport.previousFollowUpMeasuresFromLastReport);
    this.overviewForm.controls.changedLastCertifiedReportEndDate.setValue(partnerControlReport.changedLastCertifiedReportEndDate);
    this.overviewForm.controls.lastCertifiedReportNumber.setValue(partnerControlReport.lastCertifiedReportNumber);
  }

  private disableForms(userCanEdit: boolean): void {
    if (this.overviewForm.controls.controlWorkEndDate.value != null || !userCanEdit)
    {this.overviewForm.disable();}
  }

  saveForm(partnerId: number, reportId: number): void {
    this.projectPartnerReportControlOverviewService.updateControlOverview(partnerId, reportId, this.convertFormToControlOverviewDTO())
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.partner.report.control.tab.overviewAndFinalize.save.success')),
        catchError(error => this.formService.setError(error)),
      )
      .subscribe();
  }

  private convertFormToControlOverviewDTO(): ControlOverviewDTO {
    return {
      startDate: this.overviewForm.controls.controlWorkStartDate.value,
      requestsForClarifications: this.overviewForm.controls.requestsForClarifications.value,
      receiptOfSatisfactoryAnswers: this.overviewForm.controls.receiptOfSatisfactoryAnswers.value,
      endDate: this.overviewForm.controls.controlWorkEndDate.value,
      findingDescription: this.overviewForm.controls.findingDescription.value,
      followUpMeasuresFromLastReport: this.overviewForm.controls.followUpMeasuresFromLastReport.value,
      conclusion: this.overviewForm.controls.conclusion.value,
      followUpMeasuresForNextReport: this.overviewForm.controls.followUpMeasuresForNextReport.value,
      previousFollowUpMeasuresFromLastReport: this.overviewForm.controls.previousFollowUpMeasuresFromLastReport.value,
      changedLastCertifiedReportEndDate: this.overviewForm.controls.changedLastCertifiedReportEndDate.value,
      lastCertifiedReportNumber: this.overviewForm.controls.lastCertifiedReportNumber.value
    };
  }

  finalizeReport(partnerId: number, reportId: number): void {
    this.finalizationLoading = true;
    this.reportDetailPageStore.finalizeReport(partnerId, reportId)
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

  getDateInfo(value: string): any {
    return this.localeDatePipe.transform(value);
  }
}
