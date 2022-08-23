import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {UntilDestroy} from '@ngneat/until-destroy';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {combineLatest, Observable} from 'rxjs';
import {catchError, filter, map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {
  ProjectContractingReportingScheduleDTO,
  ProjectPeriodForMonitoringDTO
} from '@cat/api';
import {ContractReportingStore} from '@project/project-application/contract-reporting/contract-reporting.store';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {
  ContractMonitoringExtensionStore
} from '@project/project-application/contract-monitoring/contract-monitoring-extension/contract-monitoring-extension.store';
import {Alert} from '@common/components/forms/alert';

@UntilDestroy()
@Component({
  selector: 'jems-contract-reporting',
  templateUrl: './contract-reporting.component.html',
  styleUrls: ['./contract-reporting.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService],
})
export class ContractReportingComponent implements OnInit {
  reportingDeadlinesForm: FormGroup;
  tableData: AbstractControl[] = [];
  columnsToDisplay: string[] = [];
  Alert = Alert;
  data$: Observable<{
    periods: ProjectPeriodForMonitoringDTO[];
    reportingDeadlines: ProjectContractingReportingScheduleDTO[];
    canView: boolean;
    canEdit: boolean;
    projectStartDate: string;
  }>;

  constructor(private formBuilder: FormBuilder,
              public contractReportingStore: ContractReportingStore,
              private contractMonitoringExtensionStore: ContractMonitoringExtensionStore,
              public formService: FormService,
              private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.data$ = combineLatest([
      this.contractReportingStore.availablePeriods$,
      this.contractReportingStore.contractReportingDeadlines$,
      this.contractReportingStore.userCanViewDeadlines$,
      this.contractReportingStore.userCanEditDeadlines$,
      this.contractMonitoringExtensionStore.projectContractingMonitoring$
    ])
      .pipe(
        map(([availablePeriods, contractReportingDeadlines, userCanViewDeadlines, userCanEditDeadlines, projectContractingMonitoring]) => ({
            periods: availablePeriods,
            reportingDeadlines: contractReportingDeadlines,
            canView: userCanViewDeadlines,
            canEdit: userCanEditDeadlines,
            projectStartDate: projectContractingMonitoring.startDate
          })
        ),
        tap(data => this.initForm(data.canEdit)),
        tap(data => this.resetForm(data.reportingDeadlines, data.canEdit, data.periods))
      );

  }

  addDeadlineData(): void {
    const item = this.formBuilder.group({
      deadlineReportType: [ProjectContractingReportingScheduleDTO.TypeEnum.Both, Validators.required],
      deadlinePeriod: ['', Validators.required],
      deadlineDate: ['', Validators.required],
      deadlineComment: ['', Validators.maxLength(1000)],
      deadlinePeriodStartDate: [''],
      deadlinePeriodEndDate: [''],
      isDeadlineApplicable: [true]
    });
    this.deadlines.push(item);
    this.tableData = [...this.deadlines.controls];
    this.formService.setDirty(true);
  }

  get deadlines(): FormArray {
    return this.reportingDeadlinesForm.get('deadlines') as FormArray;
  }

  resetForm(reportingDeadlines: ProjectContractingReportingScheduleDTO[], isEditable: boolean, periods: ProjectPeriodForMonitoringDTO[]) {
    this.deadlines.clear();
    for (const reportingDeadline of reportingDeadlines) {
      const isDeadlineApplicable = periods.some(p => p.number === reportingDeadline.periodNumber);
      const item = this.formBuilder.group({
        isDeadlineApplicable: [isDeadlineApplicable],
        deadlineReportType: [reportingDeadline.type, Validators.required],
        deadlinePeriod: [isDeadlineApplicable ? reportingDeadline.periodNumber : '', Validators.required],
        deadlineDate: [isDeadlineApplicable ? reportingDeadline.date : '', Validators.required],
        deadlineComment: [reportingDeadline.comment, Validators.maxLength(1000)],
        deadlinePeriodStartDate: [periods.find(p => p.number == reportingDeadline.periodNumber)?.startDate],
        deadlinePeriodEndDate: [periods.find(p => p.number == reportingDeadline.periodNumber)?.endDate],
      });
      this.deadlines.push(item);
    }
    if (!isEditable) {
      this.deadlines.disable();
    }
    this.tableData = [...this.deadlines.controls];
  }

  onSubmit() {
    this.contractReportingStore.save(this.convertFormToContractingMonitoringDTOs())
      .pipe(
        tap(() => this.formService.setSuccess('project.application.contract.reporting.form.save.successful')),
        catchError(err => this.formService.setError(err)),
      ).subscribe();
  }

  delete(index: number): void {
    Forms.confirm(
      this.dialog,
      {
        title: 'project.application.contract.reporting.action.delete.dialog.header',
        message: 'project.application.contract.reporting.action.delete.dialog.message',
      }).pipe(
      take(1),
      filter(answer => !!answer),
      tap(() => this.deadlines.removeAt(index)),
      tap(() => this.tableData = [...this.deadlines.controls]),
      switchMap(() => this.contractReportingStore.save(this.convertFormToContractingMonitoringDTOs())),
      tap(() => this.formService.setSuccess('project.application.contract.reporting.form.save.successful')),
      catchError(err => this.formService.setError(err)),
    ).subscribe();
  }

  updateDatePicker(index: number, periods: ProjectPeriodForMonitoringDTO[], periodNum: number): void {
    const period = periods.find(p => p.number == periodNum);
    this.deadlines.at(index).patchValue({deadlinePeriodStartDate: period?.startDate});
    this.deadlines.at(index).patchValue({deadlinePeriodEndDate: period?.endDate});
    this.formService.setDirty(true);
  }

  private initForm(isEditable: boolean): void {
    if (this.columnsToDisplay.length === 0) {
      this.columnsToDisplay.push('deadlineReportType', 'deadlinePeriod', 'deadlineDate', 'deadlineComment');
      if (isEditable) {
        this.columnsToDisplay.push('deadlineDelete');
      }
    }
    this.reportingDeadlinesForm = this.formBuilder.group({
      deadlines: this.formBuilder.array([], Validators.maxLength(50)),
    });
    this.formService.init(this.reportingDeadlinesForm, new Observable<boolean>().pipe(startWith(isEditable)));
  }

  private convertFormToContractingMonitoringDTOs(): ProjectContractingReportingScheduleDTO[] {
    const contractingMonitoringDTOs = [];
    for (const item of this.deadlines.controls) {
      if(item.value.deadlineReportType || item.value.deadlinePeriod || item.value.deadlineDate || item.value.deadlineComment) {
        contractingMonitoringDTOs.push({
          date: item.value.deadlineDate,
          type: item.value.deadlineReportType,
          periodNumber: item.value.deadlinePeriod,
          comment: item.value.deadlineComment,
        } as ProjectContractingReportingScheduleDTO);
      }
    }
    return contractingMonitoringDTOs;
  }

}
