import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {combineLatest, Observable} from 'rxjs';
import {catchError, map, startWith, tap} from 'rxjs/operators';
import {
  ContractMonitoringExtensionStore
} from '@project/project-application/contracting/contract-monitoring/contract-monitoring-extension/contract-monitoring-extension.store';
import {
  ContractingDimensionCodeDTO,
  InputTranslation,
  ProgrammeChecklistDetailDTO,
  ProgrammeLumpSumDTO,
  ProgrammeSpecificObjectiveDTO,
  ProjectContractingMonitoringAddDateDTO,
  ProjectContractingMonitoringDTO,
  ProjectPartnerLumpSumDTO,
  ProjectPartnerSummaryDTO,
  ProjectPeriodForMonitoringDTO,
  ProjectStatusDTO,
  UserRoleDTO
} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {ProjectLumpSumsStore} from '@project/lump-sums/project-lump-sums-page/project-lump-sums-store.service';
import {TranslateService} from '@ngx-translate/core';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectOverviewTablesPageStore
} from '@project/project-overview-tables-page/project-overview-tables-page-store.service';
import {
  ProjectPartnerStore
} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {
  ContractReportingStore
} from '@project/project-application/contracting/contract-reporting/contract-reporting.store';

@Component({
  selector: 'jems-contract-monitoring-extension',
  templateUrl: './contract-monitoring-extension.component.html',
  styleUrls: ['./contract-monitoring-extension.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService, ProjectOverviewTablesPageStore]
})
export class ContractMonitoringExtensionComponent {

  TypologyProv94Enum = ProjectContractingMonitoringDTO.TypologyProv94Enum;
  TypologyProv95Enum = ProjectContractingMonitoringDTO.TypologyProv95Enum;
  TypologyPartnershipEnum = ProjectContractingMonitoringDTO.TypologyPartnershipEnum;
  TypologyStrategicEnum = ProjectContractingMonitoringDTO.TypologyStrategicEnum;
  projectId: number;
  ChecklistType = ProgrammeChecklistDetailDTO.TypeEnum;
  PermissionsEnum = UserRoleDTO.PermissionsEnum;

  decisionForm: FormGroup = this.formBuilder.group({
    startDate: [''],
    endDate: [''],
    entryIntoForceDate: [''],
    entryIntoForceComment: ['', Validators.maxLength(200)],
    additionalEntryIntoForceItems: this.formBuilder.array([], Validators.maxLength(25)),
    typologyProv94: [],
    typologyProv94Comment: ['', Validators.maxLength(1000)],
    typologyProv95: [],
    typologyProv95Comment: ['', Validators.maxLength(1000)],
    typologyStrategic: [],
    typologyStrategicComment: ['', Validators.maxLength(1000)],
    typologyPartnership: [],
    typologyPartnershipComment: ['', Validators.maxLength(1000)],
    lumpSums: this.formBuilder.array([]),
    dimensionCodesItems: this.formBuilder.array([], Validators.maxLength(25))
  });

  tableData: AbstractControl[] = [];
  columnsToDisplay = [
    'additionalEntryIntoForceDate',
    'additionalEntryIntoForceComment'
  ];
  data$: Observable<{
    projectContractingMonitoring: ProjectContractingMonitoringDTO;
    contractMonitoringViewable: boolean;
    contractMonitoringEditable: boolean;
    projectCallLumpSums: ProgrammeLumpSumDTO[];
    periods: ProjectPeriodForMonitoringDTO[];
    status: ProjectStatusDTO;
    dimensionCodes:  {[p: string]: string[]};
    projectBudget: number;
    projectPartnersNuts: {country: string; nuts3Region: string}[];
  }>;
  isAdditionalDataActivated = false;

  dimensionCodesDTO: ContractingDimensionCodeDTO[];

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              protected changeDetectorRef: ChangeDetectorRef,
              private activatedRoute: ActivatedRoute,
              private contractMonitoringExtensionStore: ContractMonitoringExtensionStore,
              private projectLumpSumsStore: ProjectLumpSumsStore,
              private projectStore: ProjectStore,
              private translateService: TranslateService,
              private partnerStore: ProjectPartnerStore,
              private contractReportingStore: ContractReportingStore) {

    const permissions$ = combineLatest(([
      this.contractMonitoringExtensionStore.contractMonitoringViewable$,
      this.contractMonitoringExtensionStore.contractMonitoringEditable$
      ])
    ).pipe(
      map(([canView, canEdit]) => ({canView, canEdit}))
    );

    const lumpSumData$ = combineLatest(([
      this.projectLumpSumsStore.projectCallLumpSums$,
      this.contractReportingStore.availablePeriods$
    ])).pipe(
      map(([lumpSums, projectPeriods]) => ({lumpSums, projectPeriods}))
    );

    const codesOfInterventionData$ = combineLatest(([
      this.projectStore.projectCallObjectives$,
      this.contractMonitoringExtensionStore.projectContractingMonitoringBudget$,
      this.partnerStore.partnerSummariesOfLastApprovedProjectVersion$
    ])).pipe(
      map(([projectCallObjectives, projectBudget, partnerSummaries]) => ({
        projectCallObjectives,
        projectBudget,
        partnerSummaries
      }))
    );

    this.projectId = this.activatedRoute.snapshot.params.projectId;
    this.data$ = combineLatest([
      this.contractMonitoringExtensionStore.projectContractingMonitoring$,
      this.projectStore.currentVersionOfProjectStatus$,
      permissions$,
      lumpSumData$,
      codesOfInterventionData$
    ]).pipe(
      map(([projectContractingMonitoring, status, permissions, lumpsumData, codesOfInterventionData]) => ({
        projectContractingMonitoring,
        contractMonitoringViewable: permissions.canView,
        contractMonitoringEditable: permissions.canEdit,
        projectCallLumpSums: lumpsumData.lumpSums,
        periods: lumpsumData.projectPeriods,
        status,
        dimensionCodes: this.getProjectDimensionCodes(codesOfInterventionData.projectCallObjectives.objectivesWithPolicies),
        projectBudget: codesOfInterventionData.projectBudget,
        projectPartnersNuts: this.getPartnerNutsRegionCodes(codesOfInterventionData.partnerSummaries)
      })),
      tap(data => this.dimensionCodesDTO = data.projectContractingMonitoring.dimensionCodes),
      tap(data => this.initForm(data.contractMonitoringEditable)),
      tap(data => this.resetForm(data.projectContractingMonitoring, data.contractMonitoringEditable, data.status.status)),
    );
  }

  private initForm(isEditable: boolean): void {
    this.formService.init(this.decisionForm, new Observable<boolean>().pipe(startWith(isEditable)));
    this.decisionForm.controls.endDate.disable();
  }

  addAdditionalEntryIntoForceData(): void {
    this.isAdditionalDataActivated = false;
    const item = this.formBuilder.group({
      additionalEntryIntoForceDate: ['', Validators.required],
      additionalEntryIntoForceComment: ['', Validators.maxLength(200)],
    });
    this.additionalEntryIntoForceItems.push(item);
    this.tableData = [...this.additionalEntryIntoForceItems.controls];
    this.formService.setDirty(true);
  }

  get additionalEntryIntoForceItems(): FormArray {
    return this.decisionForm.get('additionalEntryIntoForceItems') as FormArray;
  }

  get dimensionCodesFormItems(): FormArray {
    return this.decisionForm.get('dimensionCodesItems') as FormArray;
  }

  resetForm(projectContractingMonitoring: ProjectContractingMonitoringDTO, isEditable: boolean, status: string): void {
    this.isAdditionalDataActivated = false;
    this.additionalEntryIntoForceItems.clear();
    this.dimensionCodesFormItems.clear();
    this.decisionForm.controls.startDate.setValue(projectContractingMonitoring.startDate);
    this.decisionForm.controls.endDate.setValue(projectContractingMonitoring.endDate);
    this.decisionForm.controls.entryIntoForceDate.setValue(projectContractingMonitoring.entryIntoForceDate);
    this.decisionForm.controls.entryIntoForceComment.setValue(projectContractingMonitoring.entryIntoForceComment);
    this.decisionForm.controls.typologyProv94.setValue(projectContractingMonitoring.typologyProv94);
    this.decisionForm.controls.typologyProv94Comment.setValue(projectContractingMonitoring.typologyProv94Comment);
    this.decisionForm.controls.typologyProv95.setValue(projectContractingMonitoring.typologyProv95);
    this.decisionForm.controls.typologyProv95Comment.setValue(projectContractingMonitoring.typologyProv95Comment);
    this.decisionForm.controls.typologyStrategic.setValue(projectContractingMonitoring.typologyStrategic);
    this.decisionForm.controls.typologyStrategicComment.setValue(projectContractingMonitoring.typologyStrategicComment);
    this.decisionForm.controls.typologyPartnership.setValue(projectContractingMonitoring.typologyPartnership);
    this.decisionForm.controls.typologyPartnershipComment.setValue(projectContractingMonitoring.typologyPartnershipComment);
    this.dimensionCodesDTO = projectContractingMonitoring.dimensionCodes ? [... projectContractingMonitoring.dimensionCodes] : [];

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

    if (!isEditable
      || status === ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTING
      || status === ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTINGSUBMITTED
      || status === ProjectStatusDTO.StatusEnum.INMODIFICATION
      || status === ProjectStatusDTO.StatusEnum.MODIFICATIONSUBMITTED) {
      this.lumpSumsForm.disable();
    }
    this.formService.setError(null);
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
      dimensionCodes: this.dimensionCodesToSave(),
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

  private dimensionCodesToSave(): ContractingDimensionCodeDTO[] {
    return this.decisionForm.controls?.dimensionCodesItems.value.map(
      (element: any) => ({
          id: element.id as number,
          projectId: this.projectId,
          programmeObjectiveDimension: element.dimension,
          dimensionCode: element.dimensionCode,
          projectBudgetAmountShare: element.projectBudgetAmountShare
        })
    );
  }

  get lumpSumsForm(): FormArray {
    return this.decisionForm.get('lumpSums') as FormArray;
  }

  get fastTrackLumpSumsControls() {
    return this.lumpSumsForm.controls.filter(lumpSum => lumpSum.value.fastTrack);
  }

  getLumpSum(id: number, lumpSums: ProgrammeLumpSumDTO[]): InputTranslation[] | null {
    const lumpSum = lumpSums.find(it => it.id === id);
    return lumpSum ? lumpSum.name : null;
  }

  getPeriodLabel(periodId: number, periods: ProjectPeriodForMonitoringDTO[]): string {
    let period: any = periods.find(it => it.number === periodId);
    if (!period && periodId !== 0 && periodId !== 255) {
      return '';
    }
    if (periodId === 0) {
      return this.translateService.instant('project.application.form.section.part.e.period.preparation');
    }

    if (periodId === 255) {
      return this.translateService.instant('project.application.form.section.part.e.period.closure');
    }
    period = {...period, periodNumber: period.number};
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
    return this.lumpSumsForm.controls.indexOf(this.fastTrackLumpSumsControls.filter(it => it.value.orderNr === lumpSum.value.orderNr)[0]);
  }

  tableChanged(): void {
    this.formService.setDirty(true);
  }

  private getProjectDimensionCodes(objectivesWithPolicies: {[p: string]: ProgrammeSpecificObjectiveDTO[]}): {[p: string]: string[]} {
    if(this.projectStore.project.programmePriority){
      const dimensionCodes = objectivesWithPolicies[this.projectStore.project.programmePriority.code]
        .find(objective => objective.programmeObjectivePolicy === this.projectStore.project.specificObjective.programmeObjectivePolicy)?.dimensionCodes;
      return dimensionCodes ? dimensionCodes : {};
    }

    return {};
  }


  private getPartnerNutsRegionCodes(partnerSummaries: ProjectPartnerSummaryDTO[]): {country: string; nuts3Region: string}[] {
    const nutsRegions: {country: string; nuts3Region: string}[] = [];
    partnerSummaries.map(partnerSummary => ({
      country: partnerSummary.country,
      nuts3Region: partnerSummary.region
    })).forEach(
      nutsRegion => {
        if (nutsRegions.find(r => r.nuts3Region === nutsRegion.nuts3Region && r.country === nutsRegion.country)) {
          return;
        }else {
          nutsRegions.push(nutsRegion);
        }
      }
    );

    return nutsRegions;
  }
}
