import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {
  ProjectCallSettingsDTO,
  ProjectContractingReportingScheduleDTO,
  ProjectPeriodDTO, ProjectReportDTO, ProjectReportUpdateDTO
} from '@cat/api';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {catchError, filter, map, take, tap} from 'rxjs/operators';
import { APPLICATION_FORM } from '@project/common/application-form-model';
import { ProjectReportDetailPageStore } from '../project-report-detail-page-store.service';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {
  ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';
import {LanguageStore} from '@common/services/language-store.service';
import {
  ContractReportingStore
} from '@project/project-application/contracting/contract-reporting/contract-reporting.store';
import {MatSelectChange} from '@angular/material/select/select';
import {Alert} from '@common/components/forms/alert';
import {CallStore} from '../../../../../../call/services/call-store.service';

@Component({
  selector: 'jems-project-report-identification-tab',
  templateUrl: './project-report-identification-tab.component.html',
  styleUrls: ['./project-report-identification-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectReportIdentificationTabComponent {
  APPLICATION_FORM = APPLICATION_FORM;
  ProjectReportDTO = ProjectReportDTO;
  Alert = Alert;
  TypeEnum = ProjectContractingReportingScheduleDTO.TypeEnum;
  public reportId = this.router.getParameter(this.activatedRoute, 'reportId');
  CALL_PATH = CallStore.CALL_DETAIL_PATH;

  data$: Observable<{
    projectReport: ProjectReportDTO;
    periods: ProjectPeriodDTO[];
    reportingDeadlines: ProjectContractingReportingScheduleDTO[];
    relatedCall: ProjectCallSettingsDTO;
    canUserAccessCall: boolean;
  }>;

  form: FormGroup = this.formBuilder.group({
    startDate: [''],
    endDate: [''],
    periodNumber: [null, Validators.required],
    deadlineId: [null, Validators.required],
    type:[null, Validators.required],
    reportingDate: ['', Validators.required],
    finalReport: [false],
  });

  dateNameArgs = {
    startDate: 'start date',
    endDate: 'end date'
  };

  inputErrorMessages = {
    matDatetimePickerParse: 'common.date.should.be.valid',
    matDatetimePickerMin: 'common.error.field.start.before.end',
    matDatetimePickerMax: 'common.error.field.end.after.start'
  };

  selectedType: ProjectReportDTO.TypeEnum;
  selectedPeriod: ProjectPeriodDTO | undefined = undefined;
  availablePeriods: ProjectPeriodDTO[] = [];
  availableDeadlines: ProjectContractingReportingScheduleDTO[] = [];
  displayReportTypeWarningMessage = false;
  invalidPeriodSelected = false;

  constructor(public pageStore: ProjectReportDetailPageStore,
              public formService: FormService,
              private formBuilder: FormBuilder,
              private projectStore: ProjectStore,
              private router: RoutingService,
              private activatedRoute: ActivatedRoute,
              private projectReportPageStore: ProjectReportPageStore,
              public languageStore: LanguageStore,
              public contractReportingStore: ContractReportingStore) {
    this.formService.init(this.form, this.reportId ? this.pageStore.reportEditable$ : this.projectReportPageStore.userCanEditReport$);
    this.data$ = combineLatest([
      pageStore.projectReport$,
      projectStore.projectPeriods$,
      contractReportingStore.contractReportingDeadlines$,
      projectStore.projectCallSettings$,
      pageStore.canUserAccessCall$
    ]).pipe(
      tap(([projectReport, availablePeriods, reportingDeadlines]) => {
          this.availablePeriods = [
            ...availablePeriods,
            { number: 255, start: 0, end: 0, startDate: '', endDate: '' }
          ];
          this.availableDeadlines = reportingDeadlines.filter(d => this.availablePeriods.map(p => p.number).includes(d.periodNumber));
      }),
      map(([projectReport, availablePeriods, reportingDeadlines, relatedCall, canUserAccessCall]) => ({
        projectReport,
        periods: this.availablePeriods,
        reportingDeadlines,
        relatedCall,
        canUserAccessCall
      })),
      tap((data) => this.resetForm(data.projectReport)),
    );

    this.form.get('period')?.valueChanges.pipe(
      filter(period => !!period),
      tap(periodNumber => this.selectedPeriod = this.availablePeriods.find(period => period.number === periodNumber)),
    ).subscribe();
  }

  resetForm(identification?: ProjectReportDTO) {
    if (!this.reportId) {
      this.formService.setCreation(true);
    }
    if (identification) {
      this.form.patchValue(identification);
      this.selectedType = identification.type;
    }
    if (identification?.periodDetail) {
      this.invalidPeriodSelected = this.reportId != null && !this.availablePeriods.map(p => p.number).includes(identification.periodDetail.number);
    }
    this.form.patchValue({
      periodNumber: this.invalidPeriodSelected ? 'N/A' : identification?.periodDetail?.number,
    });

    if (identification?.deadlineId === null || !this.reportId) {
      this.form.get('deadlineId')?.patchValue(0);
    } else if (identification?.deadlineId) {
      this.form.get('type')?.disable();
      this.form.get('periodNumber')?.disable();
      this.form.get('reportingDate')?.disable();
      this.form.get('finalReport')?.disable();
    }
    this.disableFieldsIfReopened(identification?.status);
    this.displayReportTypeWarningMessage = false;
  }

  saveBaseInformation(): void {
    const data = {
      ...this.form.value,
      deadlineId: this.form.get('deadlineId')?.value < 1 ? null : this.form.get('deadlineId')?.value,
      type: this.form.get('deadlineId')?.value < 1 ? this.form.get('type')?.value : null,
      periodNumber: this.form.get('deadlineId')?.value < 1 ? this.form.get('periodNumber')?.value : null,
      reportingDate: this.form.get('deadlineId')?.value < 1 ? this.form.get('reportingDate')?.value : null,
      finalReport: this.form.get('deadlineId')?.value < 1 ? this.form.get('finalReport')?.value : null,
    } as ProjectReportUpdateDTO;
    if (!this.reportId) {
      this.projectReportPageStore.createProjectReport(data)
        .pipe(
          take(1),
          tap(created => this.redirectToProjectReportDetail(created)),
          catchError(err => this.formService.setError(err))
        ).subscribe();
    } else {
      this.pageStore.saveIdentification(data)
        .pipe(
          take(1),
          tap(() => this.formService.setSuccess('project.application.project.report.identification.saved')),
          catchError(err => this.formService.setError(err))
        ).subscribe();
    }
  }

  discard(report?: ProjectReportDTO): void {
    if (!this.reportId) {
      this.redirectToProjectReportsOverview();
    } else {
      this.resetForm(report);
    }
  }

  getStartMonth(periodNumber: number): number {
    return this.availablePeriods.find(p => p.number === periodNumber)?.start ?? 0;
  }

  getEndMonth(periodNumber: number): number {
    return this.availablePeriods.find(p => p.number === periodNumber)?.end ?? 0;
  }

  deadlineChanged(change: MatSelectChange) {
    const selectedDeadline = this.availableDeadlines.find(d => d.id === change.value as number);
    if (this.reportId && ((selectedDeadline?.type === this.TypeEnum.Finance && this.selectedType != this.TypeEnum.Finance) ||
      (selectedDeadline?.type === this.TypeEnum.Content && this.selectedType != this.TypeEnum.Content))
    ) {
      this.displayReportTypeWarningMessage = true;
    } else {
      this.displayReportTypeWarningMessage = false;
    }

    this.form.patchValue({type: selectedDeadline?.type});
    this.form.patchValue({periodNumber: selectedDeadline?.periodNumber});
    this.form.patchValue({reportingDate: selectedDeadline?.date});
    this.form.patchValue({finalReport: selectedDeadline?.finalReport});
  }

  private redirectToProjectReportsOverview(): void {
    this.router.navigate(['..'], {relativeTo: this.activatedRoute});
  }

  private redirectToProjectReportDetail(report: any): void {
    this.router.navigate(
      ['..', report.id, 'identification'],
      {relativeTo: this.activatedRoute}
    );
  }

  private disableFieldsIfReopened(status: ProjectReportDTO.StatusEnum | undefined): void {
    const reopenedStatuses = [
      ProjectReportDTO.StatusEnum.ReOpenSubmittedLast,
      ProjectReportDTO.StatusEnum.ReOpenSubmittedLimited,
      ProjectReportDTO.StatusEnum.VerificationReOpenedLast,
      ProjectReportDTO.StatusEnum.VerificationReOpenedLimited
    ];
    if (status && reopenedStatuses.includes(status)) {
      this.form.get('deadlineId')?.disable();
      this.form.get('type')?.disable();
      this.form.get('finalReport')?.disable();
    }
  }

}
