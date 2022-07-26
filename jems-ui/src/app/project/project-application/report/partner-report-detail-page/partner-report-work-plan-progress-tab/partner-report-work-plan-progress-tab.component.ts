import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormArray, FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {Observable} from 'rxjs';
import {
  ProjectPartnerReportWorkPackageActivityDeliverableDTO,
  ProjectPartnerReportWorkPackageActivityDTO,
  ProjectPartnerReportWorkPackageDTO,
  ProjectPartnerReportWorkPackageOutputDTO,
} from '@cat/api';
import {catchError, take, tap} from 'rxjs/operators';
import {
  PartnerReportWorkPlanPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-work-plan-progress-tab/partner-report-work-plan-page-store.service';
import {
  PartnerReportWorkplanConstants
} from '@project/project-application/report/partner-report-detail-page/partner-report-work-plan-progress-tab/partner-report-work-plan-progress.constants';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {
  PartnerFileManagementStore
} from '@project/project-application/report/partner-report-detail-page/partner-file-management-store';

@Component({
  selector: 'jems-partner-report-work-plan-progress-tab',
  templateUrl: './partner-report-work-plan-progress-tab.component.html',
  styleUrls: ['./partner-report-work-plan-progress-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerReportWorkPlanProgressTabComponent {

  constants = PartnerReportWorkplanConstants;
  savedWorkPackages$: Observable<ProjectPartnerReportWorkPackageDTO[]>;
  isReportEditable$: Observable<boolean>;

  workPlanForm: FormGroup = this.formBuilder.group({
    workPackages: this.formBuilder.array([ this.formBuilder.group({
      id: this.formBuilder.control(''),
      description: this.formBuilder.control([], this.constants.WORK_PACKAGE_DESCRIPTION.validators),
      activities: this.formBuilder.array([]),
      outputs: this.formBuilder.array([]),
    })])
  });

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private projectSidenavService: ProjectApplicationFormSidenavService,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore,
              private pageStore: PartnerReportWorkPlanPageStore,
              private partnerFileManagementStore: PartnerFileManagementStore) {

    this.savedWorkPackages$ = this.pageStore.partnerWorkPackages$
      .pipe(
        tap(workPackages => this.resetForm(workPackages))
      );
    this.isReportEditable$ = this.partnerReportDetailPageStore.reportEditable$;
    this.formService.init(this.workPlanForm, this.partnerReportDetailPageStore.reportEditable$);
  }

  get workPackages(): FormArray {
    return this.workPlanForm.get(this.constants.WORK_PACKAGES.name) as FormArray;
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
    return this.activities(workPackageIndex).at(activityIndex).get(this.constants.ACTIVITY_FILE.name) as FormControl;
  }

  outputFileMetadata(workPackageIndex: number, outputIndex: number): FormControl {
    return this.outputs(workPackageIndex).at(outputIndex).get(this.constants.OUTPUT_FILE.name) as FormControl;
  }

  deliverableFileMetadata(workPackageIndex: number, activityIndex: number, deliverableIndex: number): FormControl {
    return this.deliverables(workPackageIndex, activityIndex).at(deliverableIndex).get(this.constants.DELIVERABLE_FILE.name) as FormControl;
  }

  resetForm(workPackages: ProjectPartnerReportWorkPackageDTO[]) {
    this.workPackages.clear();
    workPackages.forEach((workPackage: ProjectPartnerReportWorkPackageDTO, index: number) =>
      this.addWorkPackage(workPackage, index)
    );
    this.formService.resetEditable();
  }

  addWorkPackage(workPackage: ProjectPartnerReportWorkPackageDTO, workPackageIndex: number): void {
    this.workPackages.push(
      this.formBuilder.group({
        id: this.formBuilder.control(workPackage.id),
        description: this.formBuilder.control(workPackage.description, this.constants.WORK_PACKAGE_DESCRIPTION.validators),
        activities: this.formBuilder.array([]),
        outputs: this.formBuilder.array([]),
      })
    );

    workPackage.activities.forEach((activity: ProjectPartnerReportWorkPackageActivityDTO, activityIndex: number) => {
      this.addActivity(workPackageIndex, activity);
      activity.deliverables?.forEach(deliverable => this.addDeliverable(workPackageIndex, activityIndex, deliverable));
    });

    workPackage.outputs.forEach(output => this.addOutput(workPackageIndex, output));
  }

  onUploadDeliverable(target: any, activityId: number, deliverableId: number,  activityIndex: number,
                      workPackageIndex: number, deliverableIndex: number, workPackageId: number): void {
    if (!target) {
      return;
    }
    this.pageStore.uploadDeliverableFile(target?.files[0], activityId, deliverableId, workPackageId)
      .pipe(
        take(1),
        catchError(err => this.formService.setError(err))
      )
      .subscribe(value => {
        this.deliverableFileMetadata(workPackageIndex, activityIndex, deliverableIndex)?.patchValue(value);
      });
  }

  onDeleteDeliverable(fileId: number, activityIndex: number, workPackageIndex: number, deliverableIndex: number): void {
    this.partnerFileManagementStore.deleteFile(fileId)
      .pipe(take(1))
      .subscribe(value => this.deliverableFileMetadata(workPackageIndex, activityIndex, deliverableIndex)
        ?.patchValue(null));
  }

  onUploadActivity(target: any, activityId: number, activityIndex: number, workPackageIndex: number, workPackageId: number): void {
    if (!target) {
      return;
    }
    this.pageStore.uploadActivityFile(target?.files[0], activityId, workPackageId)
      .pipe(
        take(1),
        catchError(err => this.formService.setError(err))
      )
      .subscribe(value => {
        this.activityFileMetadata(workPackageIndex, activityIndex)?.patchValue(value);
      });
  }

  onDeleteActivity(fileId: number, activityIndex: number, workPackageIndex: number): void {
    this.partnerFileManagementStore.deleteFile(fileId)
      .pipe(take(1))
      .subscribe(value => this.activityFileMetadata(workPackageIndex, activityIndex)?.patchValue(null));
  }

  onUploadOutput(target: any, outputId: number, outputIndex: number, workPackageIndex: number, workPackageId: number): void {
    if (!target) {
      return;
    }
    this.pageStore.uploadOutputFile(target?.files[0], outputId, workPackageId)
      .pipe(
        take(1),
        catchError(err => this.formService.setError(err))
      )
      .subscribe(value => {
        this.outputFileMetadata(workPackageIndex, outputIndex)?.patchValue(value);
      });
  }

  onDeleteOutput(fileId: number, outputIndex: number, workPackageIndex: number): void {
    this.partnerFileManagementStore.deleteFile(fileId)
      .pipe(take(1))
      .subscribe(value => this.outputFileMetadata(workPackageIndex, outputIndex)?.patchValue(null));
  }

  onDownloadFile(fileId: number): void {
    this.partnerFileManagementStore.downloadFile(fileId)
      .pipe(take(1))
      .subscribe();
  }

  private addActivity(workPackageIndex: number, existing?: ProjectPartnerReportWorkPackageActivityDTO): void {
    this.activities(workPackageIndex).push(this.formBuilder.group(
      {
        id: existing?.id,
        title: this.formBuilder.control(existing?.title || []),
        progress: this.formBuilder.control(existing?.progress || [], this.constants.ACTIVITY_PROGRESS.validators),
        deliverables: this.formBuilder.array([]),
        fileMetadata: this.formBuilder.control(existing?.attachment || '')
      })
    );
  }

  private addDeliverable(workPackageIndex: number, activityIndex: number, existing?: ProjectPartnerReportWorkPackageActivityDeliverableDTO): void {
    this.deliverables(workPackageIndex, activityIndex).push(this.formBuilder.group({
      id: existing?.id,
      title: this.formBuilder.control(existing?.title || []),
      contribution: this.formBuilder.control(existing?.contribution || false),
      fileMetadata: this.formBuilder.control(existing?.attachment || '')
    }));
  }

  private addOutput(workPackageIndex: number, existing?: ProjectPartnerReportWorkPackageOutputDTO): void {
    this.outputs(workPackageIndex).push(this.formBuilder.group({
      id: existing?.id,
      title: this.formBuilder.control(existing?.title || []),
      contribution: this.formBuilder.control(existing?.contribution || false),
      fileMetadata: this.formBuilder.control(existing?.attachment || '')
    }));
  }

  saveWorkPlan(): void {
    this.pageStore.saveWorkPackages(this.workPackages.value)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.work.package.tab.activities.saved')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }
}
