import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectAssessmentEligibilityDTO, OutputProjectEligibilityAssessment, ProjectDetailDTO} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {ProjectEligibilityCheckPageStore} from './project-eligibility-check-page-store.service';
import {map, tap} from 'rxjs/operators';
import {ProjectStepStatus} from '../project-step-status';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.data';

@Component({
  selector: 'app-project-application-eligibility-check',
  templateUrl: './project-application-eligibility-check.component.html',
  styleUrls: ['./project-application-eligibility-check.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ProjectEligibilityCheckPageStore]
})
export class ProjectApplicationEligibilityCheckComponent {

  projectId = this.activatedRoute.snapshot.params.projectId;
  step = this.activatedRoute.snapshot.params.step;
  stepStatus = new ProjectStepStatus(this.step);

  options: string[] = [this.stepStatus.eligible, this.stepStatus.ineligible];
  selectedAssessment: string;
  actionPending = false;

  notesForm = this.formBuilder.group({
    assessment: ['', Validators.required],
    notes: ['', Validators.maxLength(1000)]
  });

  notesErrors = {
    maxlength: 'eligibility.check.notes.size.too.long',
  };

  data$: Observable<{
    project: ProjectDetailDTO;
    eligibilityAssessment: OutputProjectEligibilityAssessment;
  }>;

  constructor(private router: Router,
              private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private projectStore: ProjectStore,
              private pageStore: ProjectEligibilityCheckPageStore,
              protected changeDetectorRef: ChangeDetectorRef) {
    this.data$ = combineLatest([
      this.pageStore.project$,
      this.pageStore.eligibilityAssessment(this.step)
    ])
      .pipe(
        tap(([project, eligibilityAssessment]) => {
          if (eligibilityAssessment) {
            this.setEligibilityCheckValue(eligibilityAssessment);
            this.notesForm.controls.notes.setValue(eligibilityAssessment.note);
          }
        }),
        map(([project, eligibilityAssessment]) => ({project, eligibilityAssessment}))
      );
  }

  onCancel(): void {
    this.router.navigate(['app', 'project', 'detail', this.projectId, 'assessmentAndDecision']);
  }

  assessmentChangeHandler(event: any): void {
    this.selectedAssessment = event.value;
  }

  confirmEligibilityAssessment(): void {
    this.actionPending = true;
    this.projectStore.setEligibilityAssessment({
      result: this.getEligibilityCheckValue(),
      note: this.notesForm?.controls?.notes?.value,
    });
    this.actionPending = false;
  }

  private getEligibilityCheckValue(): ProjectAssessmentEligibilityDTO.ResultEnum {
    return this.selectedAssessment === this.stepStatus.ineligible
      ? ProjectAssessmentEligibilityDTO.ResultEnum.FAILED
      : ProjectAssessmentEligibilityDTO.ResultEnum.PASSED;
  }

  private setEligibilityCheckValue(eligibilityAssessment: OutputProjectEligibilityAssessment): void {
    if (eligibilityAssessment.result === ProjectAssessmentEligibilityDTO.ResultEnum.FAILED) {
      this.notesForm.controls.assessment.setValue(this.stepStatus.ineligible);
      return;
    }
    this.notesForm.controls.assessment.setValue(this.stepStatus.eligible);
  }

  getEligibilityCheckConfirmation(): ConfirmDialogData {
    return {
      title: 'project.assessment.eligibilityCheck.dialog.title',
      message: this.selectedAssessment === this.stepStatus.eligible
        ? 'project.assessment.eligibilityCheck.dialog.message.eligible'
        : 'project.assessment.eligibilityCheck.dialog.message.ineligible'
    };
  }
}
