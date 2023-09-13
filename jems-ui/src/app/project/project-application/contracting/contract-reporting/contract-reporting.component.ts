import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {catchError, filter, map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {ProjectContractingReportingScheduleDTO, ProjectPeriodDTO} from '@cat/api';
import {ContractReportingStore} from '@project/project-application/contracting/contract-reporting/contract-reporting.store';
import {MatDialog} from '@angular/material/dialog';
import {
  ContractMonitoringExtensionStore
} from '@project/project-application/contracting/contract-monitoring/contract-monitoring-extension/contract-monitoring-extension.store';
import {Alert} from '@common/components/forms/alert';
import {ContractingSectionLockStore} from '@project/project-application/contracting/contracting-section-lock.store';
import {ContractingSection} from '@project/project-application/contracting/contracting-section';
import {Forms} from '@common/utils/forms';
import {APIError} from '@common/models/APIError';
import {CustomTranslatePipe} from '@common/pipe/custom-translate-pipe';
import {TranslateService} from '@ngx-translate/core';

@UntilDestroy()
@Component({
  selector: 'jems-contract-reporting',
  templateUrl: './contract-reporting.component.html',
  styleUrls: ['./contract-reporting.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService],
})
export class ContractReportingComponent implements OnInit {

  isSectionLocked$: Observable<boolean>;
  lockedSubject$ = new BehaviorSubject<boolean>(true);
  reportingDeadlinesForm: FormGroup;
  tableData: AbstractControl[] = [];
  columnsToDisplay: string[] = [];
  Alert = Alert;
  error$ = new BehaviorSubject<APIError | null>(null);
  TypeEnum = ProjectContractingReportingScheduleDTO.TypeEnum;
  data$: Observable<{
    periods: ProjectPeriodDTO[];
    reportingDeadlines: ProjectContractingReportingScheduleDTO[];
    canView: boolean;
    canEdit: boolean;
    isSectionLocked: boolean;
    projectStartDate: string;
    userCanViewTimeplan: boolean;
  }>;

  constructor(private formBuilder: FormBuilder,
              public contractReportingStore: ContractReportingStore,
              private contractMonitoringExtensionStore: ContractMonitoringExtensionStore,
              public formService: FormService,
              private dialog: MatDialog,
              private contractingSectionLockStore: ContractingSectionLockStore,
              private customTranslatePipe: CustomTranslatePipe,
              private translateService: TranslateService) {

  }

  ngOnInit(): void {
    this.contractingSectionLockStore.lockedSections$.pipe(
      tap(lockedSections => this.lockedSubject$.next(lockedSections.includes(ContractingSection.ProjectReportingSchedule.toString()))),
      untilDestroyed(this)
    ).subscribe();

    this.isSectionLocked$ = this.lockedSubject$.asObservable();

    this.data$ = combineLatest([
      this.contractReportingStore.availablePeriods$,
      this.contractReportingStore.contractReportingDeadlines$,
      this.contractReportingStore.userCanViewDeadlines$,
      this.contractReportingStore.userCanEditDeadlines$,
      this.isSectionLocked$,
      this.contractReportingStore.contractingMonitoringStartDate$,
      this.contractReportingStore.userCanViewTimeplan$
    ])
      .pipe(
        map(([availablePeriods, contractReportingDeadlines, userCanViewDeadlines, userCanEditDeadlines, isLocked, contractingMonitoringStartDate, userCanViewTimeplan]:
               [ProjectPeriodDTO[], ProjectContractingReportingScheduleDTO[], boolean, boolean, boolean, string, boolean]) => ({
            periods: availablePeriods,
            reportingDeadlines: contractReportingDeadlines,
            canView: userCanViewDeadlines,
            canEdit: userCanEditDeadlines,
            isSectionLocked: isLocked,
            projectStartDate: contractingMonitoringStartDate,
            projectEndDate: this.projectEndDateString(availablePeriods),
            projectDuration: this.projectDurationString(availablePeriods),
            userCanViewTimeplan
          })
        ),
        tap(data => this.initForm(data.canEdit, data.isSectionLocked)),
        tap(data => this.resetForm(data.reportingDeadlines, data.canEdit, data.isSectionLocked, data.periods)),
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
      deadlineId: [0],
      deadlineNumber: [0],
      deadlineLinkedDraftProjectReportNumbers: [[]],
      deadlineLinkedSubmittedProjectReportNumbers: [[]],
      isDeadlineApplicable: [true],
      initialDeadlineReportType: [''],
      deadlineAnyLinkedProjectReportSubmitted: [false],
      isPotentialDataLossDueToUpdate: [false]
    });
    this.deadlines.push(item);
    this.tableData = [...this.deadlines.controls];
    this.formService.setDirty(true);
  }

  get deadlines(): FormArray {
    return this.reportingDeadlinesForm.get('deadlines') as FormArray;
  }

  resetForm(reportingDeadlines: ProjectContractingReportingScheduleDTO[], isEditable: boolean, isSectionLocked: boolean, periods: ProjectPeriodDTO[]) {
    this.deadlines.clear();
    for (const reportingDeadline of reportingDeadlines) {
      const isDeadlineApplicable = periods.some(p => p.number === reportingDeadline.periodNumber);
      const item = this.formBuilder.group({
        isDeadlineApplicable: [isDeadlineApplicable],
        initialDeadlineReportType: [reportingDeadline.type],
        deadlineId: [reportingDeadline.id],
        deadlineNumber: [reportingDeadline.number],
        deadlineLinkedDraftProjectReportNumbers: [reportingDeadline.linkedDraftProjectReportNumbers],
        deadlineLinkedSubmittedProjectReportNumbers: [reportingDeadline.linkedSubmittedProjectReportNumbers],
        deadlineReportType: [reportingDeadline.type, Validators.required],
        deadlinePeriod: [isDeadlineApplicable ? reportingDeadline.periodNumber : '', Validators.required],
        deadlineDate: [isDeadlineApplicable ? reportingDeadline.date : '', Validators.required],
        deadlineComment: [reportingDeadline.comment, Validators.maxLength(1000)],
        deadlinePeriodStartDate: [periods.find(p => p.number === reportingDeadline.periodNumber)?.startDate],
        deadlinePeriodEndDate: [periods.find(p => p.number === reportingDeadline.periodNumber)?.endDate],
        deadlineAnyLinkedProjectReportSubmitted: [reportingDeadline.linkedSubmittedProjectReportNumbers.length > 0],
        isPotentialDataLossDueToUpdate: [false]
      });
      this.deadlines.push(item);
    }
    if (!isEditable || isSectionLocked) {
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
    this.deadlines.removeAt(index);
    this.tableData = [...this.deadlines.controls];
    this.formService.setDirty(true);
  }

  updateDatePicker(index: number, periods: ProjectPeriodDTO[], periodNum: number): void {
    const period = periods.find(p => p.number === periodNum);
    this.deadlines.at(index).patchValue({deadlinePeriodStartDate: period?.startDate});
    this.deadlines.at(index).patchValue({deadlinePeriodEndDate: period?.endDate});
    this.formService.setDirty(true);
  }

  updateReportType(initialType: any, newValue: ProjectContractingReportingScheduleDTO.TypeEnum, isLinkedWithProjectReport: boolean, index: number): void {
    if (isLinkedWithProjectReport && initialType && newValue != initialType as ProjectContractingReportingScheduleDTO.TypeEnum) {
      this.deadlines.controls[index].get('isPotentialDataLossDueToUpdate')?.setValue(true);
    }
    this.formService.setDirty(true);
  }


  lock(event: any) {
    const sectionNameKey = `project.application.contract.section.name.${ContractingSection.ProjectReportingSchedule.toString()}`;
    Forms.confirm(
      this.dialog,
      {
        title: 'project.application.contract.section.lock.dialog.header',
        message: {
          i18nKey: 'project.application.contract.section.lock.dialog.message',
          i18nArguments: {name: this.translateService.instant(sectionNameKey)}
        }
      }).pipe(
      take(1),
      filter(confirm => confirm),
      switchMap(() => this.contractingSectionLockStore.lockSection(ContractingSection.ProjectReportingSchedule)),
      tap(locked => this.lockedSubject$.next(true)),
      catchError((error) => this.showErrorMessage(error.error)),
      untilDestroyed(this)
    ).subscribe();

  }

  unlock(event: any) {
    const sectionNameKey = `project.application.contract.section.name.${ContractingSection.ProjectReportingSchedule.toString()}`;
    Forms.confirm(
      this.dialog,
      {
        title: 'project.application.contract.section.unlock.dialog.header',
        message: {
          i18nKey: 'project.application.contract.section.unlock.dialog.message',
          i18nArguments: {name: this.translateService.instant(sectionNameKey)}
        }
      }).pipe(
      take(1),
      filter(confirm => confirm),
      switchMap(() => this.contractingSectionLockStore.unlockSection(ContractingSection.ProjectReportingSchedule)),
      tap(unlocked => this.lockedSubject$.next(false)),
      catchError((error) => this.showErrorMessage(error.error)),
      untilDestroyed(this)
    ).subscribe();
  }

  projectEndDateString(periods: ProjectPeriodDTO[]): string {
    const period = periods.find(p => p.number === (periods.length));
    return period ? period.endDate : '';
  }

  projectDurationString(periods: ProjectPeriodDTO[]): string {
    const period = periods.find(p => p.number === (periods.length));
    return period ? period.end.toString() : '';
  }

  linkedProjectReportsText(numbers: number[]): string {
    if (numbers.length === 0) {
      return '';
    }
    const prefix = this.customTranslatePipe.transform('project.application.contract.reporting.column.linked.project.reports.abbrevation');
    return numbers.map(n => prefix + n).join(', ');
  }

  private initForm(isEditable: boolean, isSectionLocked: boolean): void {
    this.resetDeadlinesTableColumns(isEditable, isSectionLocked);
    this.reportingDeadlinesForm = this.formBuilder.group({
      deadlines: this.formBuilder.array([], Validators.maxLength(50)),
    });
    this.formService.init(this.reportingDeadlinesForm, new Observable<boolean>().pipe(startWith(isEditable)));
  }

  private convertFormToContractingMonitoringDTOs(): ProjectContractingReportingScheduleDTO[] {
    const contractingMonitoringDTOs = [];
    for (const item of this.deadlines.controls) {
      if (item.value.deadlineReportType || item.value.deadlinePeriod || item.value.deadlineDate || item.value.deadlineComment) {
        contractingMonitoringDTOs.push({
          date: item.value.deadlineDate,
          type: item.value.deadlineReportType,
          periodNumber: item.value.deadlinePeriod,
          comment: item.value.deadlineComment,
          id: item.value.deadlineId,
          number: item.value.deadlineNumber,
          linkedSubmittedProjectReportNumbers: item.value.deadlineLinkedSubmittedProjectReportNumbers,
          linkedDraftProjectReportNumbers: item.value.deadlineLinkedDraftProjectReportNumbers
        } as ProjectContractingReportingScheduleDTO);
      }
    }
    return contractingMonitoringDTOs;
  }


  private resetDeadlinesTableColumns(isEditable: boolean, isLocked: boolean) {
    this.columnsToDisplay = [];
    this.columnsToDisplay.push('deadlineNumber', 'deadlineReportType', 'deadlinePeriod', 'deadlineDate', 'deadlineComment', 'deadlineLinkedReportNumbers');
    if (isEditable && !isLocked) {
      this.columnsToDisplay.push('deadlineDelete');
    }
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      this.error$.next(null);
    }, 30000);
    return of(null);
  }
}
