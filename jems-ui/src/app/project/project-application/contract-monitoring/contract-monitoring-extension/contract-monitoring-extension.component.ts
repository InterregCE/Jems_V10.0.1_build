import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {combineLatest, Observable} from 'rxjs';
import {catchError, map, startWith, tap} from 'rxjs/operators';
import {ContractMonitoringExtensionStore} from '@project/project-application/contract-monitoring/contract-monitoring-extension/contract-monitoring-extension.store';
import {
  InputTranslation,
  ProjectContractingMonitoringAddDateDTO,
  ProjectContractingMonitoringDTO,
  ProjectPartnerLumpSumDTO,
  ProjectStatusDTO
} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {ProgrammeLumpSum} from '@project/model/lump-sums/programmeLumpSum';
import {ProjectLumpSumsStore} from '@project/lump-sums/project-lump-sums-page/project-lump-sums-store.service';
import {ProjectPeriod} from '@project/model/ProjectPeriod';
import {TranslateService} from '@ngx-translate/core';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';

@Component({
  selector: 'jems-contract-monitoring-extension',
  templateUrl: './contract-monitoring-extension.component.html',
  styleUrls: ['./contract-monitoring-extension.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService]
})
export class ContractMonitoringExtensionComponent {

  TypologyProv94Enum = ProjectContractingMonitoringDTO.TypologyProv94Enum;
  TypologyProv95Enum = ProjectContractingMonitoringDTO.TypologyProv95Enum;
  TypologyPartnershipEnum = ProjectContractingMonitoringDTO.TypologyPartnershipEnum;
  TypologyStrategicEnum = ProjectContractingMonitoringDTO.TypologyStrategicEnum;
  projectId: number;
  decisionForm: FormGroup;
  tableData: AbstractControl[] = [];
  columnsToDisplay = [
    'additionalEntryIntoForceDate',
    'additionalEntryIntoForceComment'
  ];
  data$: Observable<{
    projectContractingMonitoring: ProjectContractingMonitoringDTO;
    contractMonitoringViewable: boolean;
    contractMonitoringEditable: boolean;
    projectCallLumpSums: ProgrammeLumpSum[];
    periods: ProjectPeriod[];
    status: ProjectStatusDTO;
  }>;
  isAdditionalDataActivated = false;

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              protected changeDetectorRef: ChangeDetectorRef,
              private activatedRoute: ActivatedRoute,
              private contractMonitoringExtensionStore: ContractMonitoringExtensionStore,
              private projectLumpSumsStore: ProjectLumpSumsStore,
              private projectStore: ProjectStore,
              private translateService: TranslateService,) {
    this.projectId = this.activatedRoute.snapshot.params.projectId;
    this.data$ = combineLatest([
      this.contractMonitoringExtensionStore.projectContractingMonitoring$,
      this.contractMonitoringExtensionStore.contractMonitoringViewable$,
      this.contractMonitoringExtensionStore.contractMonitoringEditable$,
      this.projectLumpSumsStore.projectCallLumpSums$,
      this.projectLumpSumsStore.projectPeriods$,
      this.projectStore.currentVersionOfProjectStatus$
    ]).pipe(
      map(([projectContractingMonitoring, contractMonitoringViewable, contractMonitoringEditable, projectCallLumpSums, periods, status]) => ({
        projectContractingMonitoring,
        contractMonitoringViewable,
        contractMonitoringEditable,
        projectCallLumpSums,
        periods,
        status
      })),
      tap(data => this.initForm(data.contractMonitoringEditable)),
      tap(data => this.resetForm(data.projectContractingMonitoring, data.contractMonitoringEditable, data.status.status))
    );
  }

  private initForm(isEditable: boolean): void {
    this.decisionForm = this.formBuilder.group({
      startDate: [''],
      endDate: [''],
      entryIntoForceDate: [''],
      entryIntoForceComment: ['', Validators.maxLength(200)],
      additionalEntryIntoForceItems: this.formBuilder.array([], Validators.maxLength(10)),
      typologyProv94: [],
      typologyProv94Comment: [Validators.maxLength(1000)],
      typologyProv95: [],
      typologyProv95Comment: [Validators.maxLength(1000)],
      typologyStrategic: [],
      typologyStrategicComment: [Validators.maxLength(1000)],
      typologyPartnership: [],
      typologyPartnershipComment: [Validators.maxLength(1000)],
      lumpSums: this.formBuilder.array([]),
    });
    this.formService.init(this.decisionForm, new Observable<boolean>().pipe(startWith(isEditable)));
    this.decisionForm.controls.endDate.disable();
  }

  addAdditionalEntryIntoForceData(): void {
    this.isAdditionalDataActivated = false;
    const item = this.formBuilder.group({
      additionalEntryIntoForceDate: [''],
      additionalEntryIntoForceComment: ['', Validators.maxLength(200)],
    });
    this.additionalEntryIntoForceItems.push(item);
    this.tableData = [...this.additionalEntryIntoForceItems.controls];
    this.formService.setDirty(true);
  }

  get additionalEntryIntoForceItems(): FormArray {
    return this.decisionForm.get('additionalEntryIntoForceItems') as FormArray;
  }

  resetForm(projectContractingMonitoring: ProjectContractingMonitoringDTO, isEditable: boolean, status: string): void {
    this.isAdditionalDataActivated = false;
    this.additionalEntryIntoForceItems.clear();
    this.decisionForm.controls.startDate.setValue(projectContractingMonitoring.startDate);
    this.decisionForm.controls.endDate.setValue(projectContractingMonitoring.endDate);
    this.decisionForm.controls.typologyProv94.setValue(projectContractingMonitoring.typologyProv94);
    this.decisionForm.controls.typologyProv94Comment.setValue(projectContractingMonitoring.typologyProv94Comment);
    this.decisionForm.controls.typologyProv95.setValue(projectContractingMonitoring.typologyProv95);
    this.decisionForm.controls.typologyProv95Comment.setValue(projectContractingMonitoring.typologyProv95Comment);
    this.decisionForm.controls.typologyStrategic.setValue(projectContractingMonitoring.typologyStrategic);
    this.decisionForm.controls.typologyStrategicComment.setValue(projectContractingMonitoring.typologyStrategicComment);
    this.decisionForm.controls.typologyPartnership.setValue(projectContractingMonitoring.typologyPartnership);
    this.decisionForm.controls.typologyPartnershipComment.setValue(projectContractingMonitoring.typologyPartnershipComment);

    this.lumpSumsForm.clear();
    projectContractingMonitoring.fastTrackLumpSums.forEach(lumpSum => {
      this.lumpSumsForm.push(this.formBuilder.group({
        orderNr: this.formBuilder.control(lumpSum?.orderNr),
        programmeLumpSumId: this.formBuilder.control(lumpSum?.programmeLumpSumId),
        period: this.formBuilder.control(lumpSum?.period),
        lumpSumContributions: this.formBuilder.control(lumpSum?.lumpSumContributions),
        comment: this.formBuilder.control(lumpSum?.comment || '', Validators.maxLength(200)),
        readyForPayment: this.formBuilder.control(lumpSum?.readyForPayment || false),
        fastTrack: this.formBuilder.control(lumpSum?.fastTrack || false),
        installmentsAlreadyCreated: this.formBuilder.control(lumpSum?.installmentsAlreadyCreated || false)
      }));
    });

    if (projectContractingMonitoring.addDates?.length) {
      this.decisionForm.controls.entryIntoForceComment.setValue(projectContractingMonitoring.addDates[0].comment);
      this.decisionForm.controls.entryIntoForceDate.setValue(projectContractingMonitoring.addDates[0].entryIntoForceDate);
      for(let i = 1; i < projectContractingMonitoring.addDates.length ; i++) {
        const item = this.formBuilder.group({
          additionalEntryIntoForceDate: [projectContractingMonitoring.addDates[i].entryIntoForceDate],
          additionalEntryIntoForceComment: [projectContractingMonitoring.addDates[i].comment, Validators.maxLength(200)],
        });
        this.additionalEntryIntoForceItems.push(item);
      }
    }
    if (!isEditable) {
      this.additionalEntryIntoForceItems.disable();
    }
    this.tableData = [...this.additionalEntryIntoForceItems.controls];

    if (status === ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTING
      || status === ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTINGSUBMITTED
      || status === ProjectStatusDTO.StatusEnum.INMODIFICATION
      || status === ProjectStatusDTO.StatusEnum.MODIFICATIONSUBMITTED) {
      this.lumpSumsForm.disable();
    }
  }

  onSubmit(): void {
    this.contractMonitoringExtensionStore.save(this.getUpdatedProjectContractingMonitoring())
      .pipe(
        tap(data => this.decisionForm.controls.endDate.setValue(data.endDate)),
        tap(data => this.contractMonitoringExtensionStore.savedProjectContractingMonitoring$.next(data)),
        tap(() => this.formService.setSuccess('project.application.contract.monitoring.project.form')),
        catchError(err => this.formService.setError(err)),
      ).subscribe();
  }

  private getUpdatedProjectContractingMonitoring(): ProjectContractingMonitoringDTO {
    return {
      projectId: this.projectId,
      startDate: this.decisionForm.controls.startDate.value,
      endDate: this.decisionForm.controls.endDate.value,
      addDates: this.toAddDatesDTO(),
      typologyProv94: this.decisionForm.controls.typologyProv94.value,
      typologyProv94Comment: this.decisionForm.controls.typologyProv94Comment.value,
      typologyProv95: this.decisionForm.controls.typologyProv95.value,
      typologyProv95Comment: this.decisionForm.controls.typologyProv95Comment.value,
      typologyStrategic: this.decisionForm.controls.typologyStrategic.value,
      typologyStrategicComment: this.decisionForm.controls.typologyStrategicComment.value,
      typologyPartnership: this.decisionForm.controls.typologyPartnership.value,
      typologyPartnershipComment: this.decisionForm.controls.typologyPartnershipComment.value,
      fastTrackLumpSums: this.decisionForm.controls.lumpSums.value.map((lumpSum: any) => ({
        orderNr: lumpSum.orderNr,
        programmeLumpSumId: lumpSum.programmeLumpSumId,
        period: lumpSum.period,
        lumpSumContributions: lumpSum.lumpSumContributions,
        comment: lumpSum.comment,
        readyForPayment: lumpSum.readyForPayment,
        fastTrack: lumpSum.fastTrack,
        installmentsAlreadyCreated: lumpSum.installmentsAlreadyCreated
      }))
    } as ProjectContractingMonitoringDTO;
  }

  private toAddDatesDTO(): ProjectContractingMonitoringAddDateDTO[] {
    const addDatesDTOs = [];
    if (this.decisionForm.controls.entryIntoForceComment || this.decisionForm.controls.entryIntoForceDate) {
      addDatesDTOs.push({
        comment: this.decisionForm.controls.entryIntoForceComment.value,
        entryIntoForceDate: this.decisionForm.controls.entryIntoForceDate.value,
      } as ProjectContractingMonitoringAddDateDTO);
    }
    for (const item of this.additionalEntryIntoForceItems.controls) {
      if(item.value.additionalEntryIntoForceComment || item.value.additionalEntryIntoForceDate) {
        addDatesDTOs.push({
          comment: item.value.additionalEntryIntoForceComment,
          entryIntoForceDate: item.value.additionalEntryIntoForceDate,
        } as ProjectContractingMonitoringAddDateDTO);
      }
    }
    return addDatesDTOs;
  }

  get lumpSumsForm(): FormArray {
    return this.decisionForm.get('lumpSums') as FormArray;
  }

  get fastTrackLumpSumsControls() {
    return this.lumpSumsForm.controls.filter(lumpSum => lumpSum.value.fastTrack);
  }

  getLumpSum(id: number, lumpSums: ProgrammeLumpSum[]): InputTranslation[] | null {
    const lumpSum = lumpSums.find(it => it.id === id);
    return lumpSum ? lumpSum.name : null;
  }

  getPeriodLabel(periodId: number, periods: ProjectPeriod[]): string {
    const period = periods.find(it => it.periodNumber === periodId);
    if (!period && periodId !== 0 && periodId !== 255) {
      return '';
    }
    if (periodId === 0) {
      return this.translateService.instant('project.application.form.section.part.e.period.preparation');
    }

    if (periodId === 255) {
      return this.translateService.instant('project.application.form.section.part.e.period.closure');
    }
    return this.translateService.instant('project.application.form.work.package.output.delivery.period.entry', period);
  }

  getAmount(contributions: ProjectPartnerLumpSumDTO[]): number {
    return contributions.reduce((accumulator, lumpSumContribution) => {
      return accumulator + lumpSumContribution.amount;
    }, 0);
  }

  areThereFastTrackLumpSums(): boolean {
    return this.fastTrackLumpSumsControls.length > 0;
  }

  getIndexForCurrentLumpSum(lumpSum: AbstractControl) {
    return this.lumpSumsForm.controls.indexOf(this.fastTrackLumpSumsControls.filter(it => it.value.orderNr == lumpSum.value.orderNr)[0]);
  }
}
