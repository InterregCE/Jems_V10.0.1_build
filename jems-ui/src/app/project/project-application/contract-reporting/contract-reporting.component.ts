import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {UntilDestroy} from '@ngneat/until-destroy';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {combineLatest, Observable} from 'rxjs';
import {catchError, map, startWith, tap} from 'rxjs/operators';
import {ProjectContractingReportingScheduleDTO, ProjectPeriodDTO} from '@cat/api';
import {ContractReportingStore} from '@project/project-application/contract-reporting/contract-reporting.store';

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
  columnsToDisplay = [
    'deadlineReportType',
    'deadlinePeriod',
    'deadlineDate',
    'deadlineComment'
  ];
  data$: Observable<{
    periods: ProjectPeriodDTO[];
    reportingDeadlines: ProjectContractingReportingScheduleDTO[];
    canView: boolean;
    canEdit: boolean;
  }>;

  constructor(private formBuilder: FormBuilder,
              private contractReportingStore: ContractReportingStore,
              public formService: FormService) {
  }

  ngOnInit(): void {
    this.data$ = combineLatest([
      this.contractReportingStore.projectForm$,
      this.contractReportingStore.contractReportingDeadlines$,
      this.contractReportingStore.userCanViewDeadlines$,
      this.contractReportingStore.userCanEditDeadlines$,
    ])
      .pipe(
        map(([projectForm, contractReportingDeadlines, userCanViewDeadlines, userCanEditDeadlines]) => ({
            periods: projectForm.periods,
            reportingDeadlines: contractReportingDeadlines,
            canView: userCanViewDeadlines,
            canEdit: userCanEditDeadlines
          })
        ),
        tap(data => this.initForm(data.canEdit)),
        tap(data => this.resetForm(data.reportingDeadlines, data.canEdit))
      );

  }

  addDeadlineData(): void {
    const item = this.formBuilder.group({
      deadlineReportType: [''],
      deadlinePeriod: [''],
      deadlineDate: [''],
      deadlineComment: ['', Validators.maxLength(1000)],
    });
    this.deadlines.push(item);
    this.tableData = [...this.deadlines.controls];
    this.formService.setDirty(true);
  }

  get deadlines(): FormArray {
    return this.reportingDeadlinesForm.get('deadlines') as FormArray;
  }

  resetForm(reportingDeadlines: ProjectContractingReportingScheduleDTO[], isEditable: boolean) {
    this.deadlines.clear();
    for (const reportingDeadline of reportingDeadlines) {
      const item = this.formBuilder.group({
        deadlineReportType: [reportingDeadline.type],
        deadlinePeriod: [reportingDeadline.periodNumber],
        deadlineDate: [reportingDeadline.date],
        deadlineComment: [reportingDeadline.comment, Validators.maxLength(1000)],
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

  private initForm(isEditable: boolean): void {
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
