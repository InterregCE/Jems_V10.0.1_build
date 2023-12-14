import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {Alert} from '@common/components/forms/alert';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {APIError} from '@common/models/APIError';
import {ProjectReportDTO, ProjectReportVerificationClarificationDTO, ProjectReportVerificationService} from '@cat/api';
import {catchError, map, startWith, take, tap} from 'rxjs/operators';
import {
  ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';
import {
  ProjectVerificationReportFinalizeStore
} from '@project/project-application/report/project-verification-report/project-verification-report-finalize-tab/project-verification-report-finalize-store.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';

@Component({
  selector: 'jems-project-verification-report-finalize-clarification-request-list',
  templateUrl: './project-verification-report-finalize-clarification-request-list.component.html',
  styleUrls: ['./project-verification-report-finalize-clarification-request-list.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectVerificationReportFinalizeClarificationRequestListComponent {
  clarificationForm: FormGroup;
  tableData: AbstractControl[] = [];
  columnsToDisplay: string[] = [];
  Alert = Alert;
  error$ = new BehaviorSubject<APIError | null>(null);
  data$: Observable<{
    clarifications: ProjectReportVerificationClarificationDTO[];
    canView: boolean;
    canEdit: boolean;
    projectId: number;
    reportId: number;
    finalizationAllowed: boolean;
    reportFinalised: boolean;
  }>;

  constructor(private formBuilder: FormBuilder,
              private reportPageStore: ProjectReportPageStore,
              private projectReportVerificationService: ProjectReportVerificationService,
              private projectVerificationReportFinalizeStore: ProjectVerificationReportFinalizeStore,
              private reportDetailPageStore: ProjectReportDetailPageStore,
              public formService: FormService) {
    this.data$ = combineLatest([
      this.projectVerificationReportFinalizeStore.clarifications$,
      this.reportDetailPageStore.projectReport$,
      this.reportPageStore.userCanEditVerification$,
      this.reportPageStore.userCanViewVerification$,
      this.reportPageStore.userCanFinalizeVerification$
    ])
      .pipe(
        map(([clarifications, report, userCanEdit, userCanView, userCanFinalize]: any[]) => ({
            clarifications,
            canView: userCanView,
            canEdit: userCanEdit,
            projectId: report.projectId,
            reportId: report.id,
            finalizationAllowed: userCanFinalize,
            reportFinalised: report.status === ProjectReportDTO.StatusEnum.Finalized,
          })
        ),
        tap(data => this.initForm(!data.reportFinalised && data.canEdit)),
        tap(data => this.resetForm(data.clarifications, (!data.reportFinalised && data.canEdit))),
      );
  }

  get clarifications(): FormArray {
    return this.clarificationForm.get('clarifications') as FormArray;
  }

  addClarificationData(): void {
    const item = this.formBuilder.group({
      requestDate: ['', Validators.required],
      answerDate: [''],
      comment: ['', Validators.maxLength(3000)]
    });
    this.clarifications.push(item);
    this.tableData = [...this.clarifications.controls];
    this.formService.setDirty(true);
  }

  resetForm(clarifications: ProjectReportVerificationClarificationDTO[], isEditable: boolean) {
    this.clarifications.clear();
    for (const clarification of clarifications) {
      const item = this.formBuilder.group({
        requestDate: [clarification.requestDate, Validators.required],
        answerDate: [clarification.answerDate],
        comment: [clarification.comment],
      });
      this.clarifications.push(item);
    }
    if (!isEditable) {
      this.clarifications.disable();
    }
    this.tableData = [...this.clarifications.controls];
  }

  onSubmit(projectId: number, reportId: number): void {
    this.projectReportVerificationService.updateReportVerificationClarifications(projectId, reportId, this.convertFormToClarificationDTO())
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.project.verification.work.tab.finalise.clarification.save.success')),
        catchError(error => this.formService.setError(error)),
      )
      .subscribe();
  }

  delete(index: number): void {
    this.clarifications.removeAt(index);
    this.tableData = [...this.clarifications.controls];
    this.formService.setDirty(true);
  }

  private convertFormToClarificationDTO(): ProjectReportVerificationClarificationDTO[] {
    const clarificationDTOs = [];
    for (const item of this.clarifications.controls) {
      clarificationDTOs.push({
        requestDate: item.value.requestDate,
        answerDate: item.value.answerDate,
        comment: item.value.comment,
      } as ProjectReportVerificationClarificationDTO);
    }
    return clarificationDTOs;
  }

  private initForm(isEditable: boolean): void {
    this.resetTableColumns(isEditable);
    this.clarificationForm = this.formBuilder.group({
      clarifications: this.formBuilder.array([]),
    });
    this.formService.init(this.clarificationForm, new Observable<boolean>().pipe(startWith(isEditable)));
  }

  private resetTableColumns(isEditable: boolean) {
    this.columnsToDisplay = [];
    this.columnsToDisplay.push('requestDate', 'answerDate', 'comment');
    if (isEditable) {
      this.columnsToDisplay.push('delete');
    }
  }
}
