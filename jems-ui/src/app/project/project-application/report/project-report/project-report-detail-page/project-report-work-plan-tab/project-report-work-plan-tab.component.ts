import {Component} from '@angular/core';
import {combineLatest, Observable, of} from 'rxjs';
import {
  ProjectReportWorkPackageActivityDeliverableDTO,
  ProjectReportWorkPackageActivityDTO,
  ProjectReportWorkPackageDTO,
  ProjectReportWorkPackageOutputDTO,
  UpdateProjectReportWorkPackageActivityDeliverableDTO,
  UpdateProjectReportWorkPackageActivityDTO,
  UpdateProjectReportWorkPackageDTO,
  UpdateProjectReportWorkPackageOutputDTO
} from '@cat/api';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {AbstractControl, FormArray, FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {catchError, finalize, map, take, tap} from 'rxjs/operators';
import {
  ProjectReportWorkPlanTabStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-work-plan-tab/project-report-work-plan-tab.store';
import {
  ProjectReportWorkPlanTabConstants
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-work-plan-tab/project-report-work-plan-tab.constants';
import {NumberService} from '@common/services/number.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';

@Component({
  selector: 'jems-project-report-work-plan-tab',
  templateUrl: './project-report-work-plan-tab.component.html',
  styleUrls: ['./project-report-work-plan-tab.component.scss'],
  providers: [FormService]
})
export class ProjectReportWorkPlanTabComponent {

  MIN_VALUE = -999_999_999.99;
  MAX_VALUE = 999_999_999.99;
  APPLICATION_FORM = APPLICATION_FORM;
  constants = ProjectReportWorkPlanTabConstants;
  specificStatus = Object.keys(ProjectReportWorkPackageDTO.SpecificStatusEnum);
  communicationStatus = Object.keys(ProjectReportWorkPackageDTO.CommunicationStatusEnum);
  activityStatus = Object.keys(ProjectReportWorkPackageActivityDTO.StatusEnum);

  form = this.formBuilder.group({
    workPackages: this.formBuilder.array([])
  });

  data$: Observable<{
    reportEditable: boolean;
    workPackages: ProjectReportWorkPackageDTO[];
  }>;

  isUploadDone: boolean;

  constructor(
    private readonly projectStore: ProjectStore,
    private readonly projectReportDetailPageStore: ProjectReportDetailPageStore,
    private readonly workPlanTabStore: ProjectReportWorkPlanTabStore,
    private formBuilder: FormBuilder,
    public formService: FormService,
  ) {

    this.data$ = combineLatest([
      this.projectReportDetailPageStore.reportEditable$,
      this.workPlanTabStore.workPackages$,
    ])
      .pipe(
        map(([reportEditable, workPackages]) => ({
          reportEditable,
          workPackages
        })),
        tap(data => this.formService.init(this.form, of(data.reportEditable))),
        tap(data => this.resetForm(data.workPackages, data.reportEditable))
      );
  }

  get workPackages(): FormArray {
    return this.form.get('workPackages') as FormArray;
  }

  activities(workPackageIndex: number): FormArray {
    return this.workPackages.at(workPackageIndex).get(this.constants.ACTIVITIES.name) as FormArray;
  }

  deliverables(workPackageIndex: number, activityIndex: number): FormArray {
    return this.activities(workPackageIndex).at(activityIndex).get(this.constants.DELIVERABLES.name) as FormArray;
  }

  outputs(workPackageIndex: number): FormArray {
    return this.workPackages.at(workPackageIndex).get(this.constants.OUTPUTS.name) as FormArray;
  }

  activityFileMetadata(workPackageIndex: number, activityIndex: number): FormControl {
    return this.activities(workPackageIndex).at(activityIndex).get(this.constants.ACTIVITY_ATTACHMENT.name) as FormControl;
  }

  deliverableFileMetadata(workPackageIndex: number, activityIndex: number, deliverableIndex: number): FormControl {
    return this.deliverables(workPackageIndex, activityIndex).at(deliverableIndex).get(this.constants.DELIVERABLE_ATTACHMENT.name) as FormControl;
  }

  outputFileMetadata(workPackageIndex: number, outputIndex: number): FormControl {
    return this.outputs(workPackageIndex).at(outputIndex).get(this.constants.OUTPUT_ATTACHMENT.name) as FormControl;
  }

  saveForm() {
    this.workPlanTabStore.updateWorkPlan(this.convertWorkPackageFormToDTO())
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.work.package.tab.activities.saved')),
        catchError(error => this.formService.setError(error)),
      ).subscribe();
  }


  private convertWorkPackageFormToDTO(): UpdateProjectReportWorkPackageDTO[] {
    return this.workPackages.getRawValue().map(workPackageForm => ({
      id: workPackageForm.id,
      completed: workPackageForm.completed,
      specificStatus: workPackageForm.specificStatus,
      specificExplanation: workPackageForm.specificExplanation,
      communicationStatus: workPackageForm.communicationStatus,
      communicationExplanation: workPackageForm.communicationExplanation,
      description: workPackageForm.description,
      activities: this.convertActivitiesFormToDTO(workPackageForm.activities),
      outputs: this.convertOutputsFormToDTO(workPackageForm.outputs)
    } as UpdateProjectReportWorkPackageDTO));
  }

  private convertActivitiesFormToDTO(activities: any[]): UpdateProjectReportWorkPackageActivityDTO[] {
    return activities.map(activitiesForm => ({
      id: activitiesForm.id,
      status: activitiesForm.status,
      progress: activitiesForm.progress,
      deliverables: this.convertDeliverablesFormToDTO(activitiesForm.deliverables)
    } as UpdateProjectReportWorkPackageActivityDTO));
  }

  private convertDeliverablesFormToDTO(deliverables: any[]): UpdateProjectReportWorkPackageActivityDeliverableDTO[] {
    return deliverables.map(deliverablesForm => ({
      id: deliverablesForm.id,
      currentReport: deliverablesForm.currentReport,
      progress: deliverablesForm.progress
    } as UpdateProjectReportWorkPackageActivityDeliverableDTO));
  }

  private convertOutputsFormToDTO(outputs: any[]): UpdateProjectReportWorkPackageOutputDTO[] {
    return outputs.map(outputsForm => ({
      id: outputsForm.id,
      currentReport: outputsForm.currentReport,
      progress: outputsForm.progress
    } as UpdateProjectReportWorkPackageOutputDTO));
  }

  resetForm(workPackagesDTO: ProjectReportWorkPackageDTO[], reportEditable: boolean) {
    this.workPackages.clear();
    workPackagesDTO.map((dto: ProjectReportWorkPackageDTO) => this.extractWorkPackageFormFromDTO(dto))
      .forEach(workPackages => this.workPackages.push(workPackages));

    if (!reportEditable) {
      this.formService.setEditable(false);
    }
  }

  private extractWorkPackageFormFromDTO(dto: ProjectReportWorkPackageDTO): FormGroup {
    const activities = dto.activities.map((activityDTO: ProjectReportWorkPackageActivityDTO) => this.extractActivitiesFormFromDTO(activityDTO));
    const outputs = dto.outputs.map((outputDTO: ProjectReportWorkPackageOutputDTO) => this.extractOutputsFormFromDTO(outputDTO));

    return this.formBuilder.group({
      id: this.formBuilder.control(dto.id),
      number: this.formBuilder.control(dto.number),
      deactivated: this.formBuilder.control(dto.deactivated ?? false),
      completed: this.formBuilder.control(dto.completed),
      specificObjective: this.formBuilder.control(this.disableControl(dto.specificObjective)),
      specificStatus: this.formBuilder.control(dto.specificStatus),
      specificExplanation: this.formBuilder.control(dto.specificExplanation),
      communicationObjective: this.formBuilder.control(this.disableControl(dto.communicationObjective)),
      communicationStatus: this.formBuilder.control(dto.communicationStatus),
      communicationExplanation: this.formBuilder.control(dto.communicationExplanation),
      description: this.formBuilder.control(dto.description ?? []),
      activities: this.formBuilder.array(activities ?? []),
      outputs: this.formBuilder.array(outputs ?? []),
    });
  }

  private extractActivitiesFormFromDTO(dto: ProjectReportWorkPackageActivityDTO): FormGroup {
    const deliverables = dto.deliverables.map((deliverableDTO: ProjectReportWorkPackageActivityDeliverableDTO) => this.extractDeliverablesFormFromDTO(deliverableDTO));

    return this.formBuilder.group({
      id: this.formBuilder.control(dto.id),
      number: this.formBuilder.control(dto.number),
      title: this.formBuilder.control(this.disableControl(dto.title ?? '')),
      deactivated: this.formBuilder.control(dto.deactivated ?? false),
      startPeriod: this.formBuilder.control(dto.startPeriod ?? ''),
      endPeriod: this.formBuilder.control(dto.endPeriod ?? ''),
      status: this.formBuilder.control(dto.status),
      progress: this.formBuilder.control(dto.progress ?? [], this.constants.ACTIVITY_PROGRESS.validators),
      attachment: this.formBuilder.control(dto.attachment),
      deliverables: this.formBuilder.array(deliverables ?? []),
    });
  }

  private extractDeliverablesFormFromDTO(dto: ProjectReportWorkPackageActivityDeliverableDTO): FormGroup {
    return this.formBuilder.group({
      id: this.formBuilder.control(dto.id),
      number: this.formBuilder.control(dto.number),
      title: this.formBuilder.control(this.disableControl(dto.title ?? '')),
      deactivated: this.formBuilder.control(dto.deactivated ?? false),
      period: this.formBuilder.control(this.disableControl(dto.period ?? '')),
      currentReport: this.formBuilder.control(dto.currentReport ?? 0),
      previouslyReported: this.formBuilder.control(this.disableControl(dto.previouslyReported ?? 0)),
      totalReportedSoFar: this.formBuilder.control(this.disableControl((dto.currentReport ?? 0) + (dto.previouslyReported ?? 0))),
      progress: this.formBuilder.control(dto.progress ?? ''),
      attachment: this.formBuilder.control(dto.attachment),
    });
  }

  private extractOutputsFormFromDTO(dto: ProjectReportWorkPackageOutputDTO): FormGroup {
    return this.formBuilder.group({
      id: this.formBuilder.control(dto.id),
      title: this.formBuilder.control(this.disableControl(dto.title)),
      number: this.formBuilder.control(dto.number),
      deactivated: this.formBuilder.control(dto.deactivated ?? ''),
      outputIndicator: this.formBuilder.control(this.disableControl(dto.outputIndicator ?? '')),
      period: this.formBuilder.control(this.disableControl(dto.period ?? '')),
      targetValue: this.formBuilder.control(this.disableControl(dto.targetValue ?? 0)),
      currentReport: this.formBuilder.control(dto.currentReport ?? 0),
      previouslyReported: this.formBuilder.control(this.disableControl(dto.previouslyReported ?? 0)),
      totalReportedSoFar: this.formBuilder.control(this.disableControl((dto.currentReport ?? 0) + (dto.previouslyReported ?? 0))),
      progress: this.formBuilder.control(dto.progress ?? ''),
      attachment: this.formBuilder.control(dto.attachment)
    });
  }

  getPeriodArguments(period: any): { [key: string]: number } {
    return {
      periodNumber: period?.number,
      start: period?.start,
      end: period?.end
    };
  }

  onDownloadFile(fileId: number) {
    this.workPlanTabStore.downloadFile(fileId)
      .pipe(take(1))
      .subscribe();
  }

  onUploadActivity(target: any, workPackageIndex: number, activityIndex: number) {
    if (!target) {
      return;
    }

    const workPackageId = this.workPackages.at(workPackageIndex).get(this.constants.WORK_PACKAGE_ID.name)?.value;
    const activityId = this.activities(workPackageIndex).at(activityIndex).get(this.constants.ACTIVITY_ID.name)?.value;

    this.isUploadDone = false;
    this.workPlanTabStore.uploadActivityFile(target.files[0], workPackageId, activityId)
      .pipe(
        take(1),
        catchError(err => this.formService.setError(err)),
        finalize(() => this.isUploadDone = true)
      )
      .subscribe(attachment => this.activityFileMetadata(workPackageIndex, activityIndex)?.patchValue(attachment));
  }


  onUploadDeliverable(target: any, workPackageIndex: number, activityIndex: number, deliverableIndex: number) {
    if (!target) {
      return;
    }

    const workPackageId = this.workPackages.at(workPackageIndex).get(this.constants.WORK_PACKAGE_ID.name)?.value;
    const activityId = this.activities(workPackageIndex).at(activityIndex).get(this.constants.ACTIVITY_ID.name)?.value;
    const deliverableId = this.deliverables(workPackageIndex, activityIndex).at(deliverableIndex).get(this.constants.OUTPUT_ID.name)?.value;

    this.isUploadDone = false;
    this.workPlanTabStore.uploadDeliverableFile(target.files[0], workPackageId, activityId, deliverableId)
      .pipe(
        take(1),
        catchError(err => this.formService.setError(err)),
        finalize(() => this.isUploadDone = true)
      )
      .subscribe(attachment => this.deliverableFileMetadata(workPackageIndex, activityIndex, deliverableIndex)?.patchValue(attachment));
  }

  onUploadOutput(target: any, workPackageIndex: number, outputIndex: number) {
    if (!target) {
      return;
    }

    const workPackageId = this.workPackages.at(workPackageIndex).get(this.constants.WORK_PACKAGE_ID.name)?.value;
    const outputId = this.outputs(workPackageIndex).at(outputIndex).get(this.constants.OUTPUT_ID.name)?.value;

    this.isUploadDone = false;
    this.workPlanTabStore.uploadOutputFile(target.files[0], workPackageId, outputId)
      .pipe(
        take(1),
        catchError(err => this.formService.setError(err)),
        finalize(() => this.isUploadDone = true)
      )
      .subscribe(attachment => this.outputFileMetadata(workPackageIndex, outputIndex)?.patchValue(attachment));
  }

  onDeleteActivity(fileId: number, workPackageIndex: number, activityIndex: number) {
    this.workPlanTabStore.deleteFile(fileId)
      .pipe(take(1))
      .subscribe(() => this.activityFileMetadata(workPackageIndex, activityIndex)?.patchValue(null));
  }

  onDeleteDeliverable(fileId: number, workPackageIndex: number, activityIndex: number, deliverableIndex: number) {
    this.workPlanTabStore.deleteFile(fileId)
      .pipe(take(1))
      .subscribe(() => this.deliverableFileMetadata(workPackageIndex, activityIndex, deliverableIndex)?.patchValue(null));
  }

  onDeleteOutput(fileId: number, workPackageIndex: number, outputIndex: number) {
    this.workPlanTabStore.deleteFile(fileId)
      .pipe(take(1))
      .subscribe(() => this.outputFileMetadata(workPackageIndex, outputIndex)?.patchValue(null));
  }

  deliverableTotalChanged(deliverable: AbstractControl) {
    const currentReport = deliverable.get(this.constants.DELIVERABLE_CURRENT_REPORT.name)?.value;
    const previouslyReported = deliverable.get(this.constants.DELIVERABLE_PREVIOUSLY_REPORTED.name)?.value;
    const totalReportedSoFar = deliverable.get(this.constants.DELIVERABLE_TOTAL_REPORTED_SO_FAR.name);

    totalReportedSoFar?.patchValue(NumberService.sum([currentReport, previouslyReported]));
  }

  outputTotalChanged(output: AbstractControl) {
    const currentReport = output.get(this.constants.OUTPUT_CURRENT_REPORT.name)?.value;
    const previouslyReported = output.get(this.constants.OUTPUT_PREVIOUSLY_REPORTED.name)?.value;
    const totalReportedSoFar = output.get(this.constants.OUTPUT_TOTAL_REPORTED_SO_FAR.name);

    totalReportedSoFar?.patchValue(NumberService.sum([currentReport, previouslyReported]));
  }

  private disableControl(value: any) {
    return {value, disabled: true};
  }
}
