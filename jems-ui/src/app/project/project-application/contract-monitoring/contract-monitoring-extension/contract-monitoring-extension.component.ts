import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import { FormService } from '@common/components/section/form/form.service';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs';
import {catchError, map, startWith, take, tap} from 'rxjs/operators';
import {
  ContractMonitoringExtensionStore
} from '@project/project-application/contract-monitoring/contract-monitoring-extension/contract-monitoring-extension.store';
import {ProjectContractingMonitoringAddDateDTO, ProjectContractingMonitoringDTO} from '@cat/api';
import {ActivatedRoute} from '@angular/router';

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
  }>;
  isAdditionalDataActivated = false;

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              protected changeDetectorRef: ChangeDetectorRef,
              private activatedRoute: ActivatedRoute,
              private contractMonitoringExtensionStore: ContractMonitoringExtensionStore) {
    this.projectId = this.activatedRoute.snapshot.params.projectId;
    this.data$ = this.contractMonitoringExtensionStore.projectContractingMonitoring$
    .pipe(
      map(projectContractingMonitoring => ({
        projectContractingMonitoring,
      })),
      tap(data => this.resetForm(data.projectContractingMonitoring))
    );
    this.initForm();
  }

  private initForm(): void {
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
    });
    this.formService.init(this.decisionForm, new Observable<boolean>().pipe(startWith(true)));
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

  resetForm(projectContractingMonitoring: ProjectContractingMonitoringDTO): void {
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
    this.tableData = [...this.additionalEntryIntoForceItems.controls];
  }

  onSubmit(): void {
    this.contractMonitoringExtensionStore.save(this.getUpdatedProjectContractingMonitoring())
      .pipe(
        take(1),
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
}
