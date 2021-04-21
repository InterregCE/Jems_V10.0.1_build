import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {InputProjectEligibilityAssessment, OutputProjectEligibilityAssessment, ProjectDetailDTO} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {ProjectApplicationFormSidenavService} from '../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {ProjectEligibilityCheckPageStore} from './project-eligibility-check-page-store.service';
import {map, tap} from 'rxjs/operators';

@Component({
  selector: 'app-project-application-eligibility-check',
  templateUrl: './project-application-eligibility-check.component.html',
  styleUrls: ['./project-application-eligibility-check.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ProjectEligibilityCheckPageStore]
})
export class ProjectApplicationEligibilityCheckComponent {
  ELIGIBLE = 'ELIGIBLE';
  INELIGIBLE = 'INELIGIBLE';

  projectId = this.activatedRoute.snapshot.params.projectId;
  options: string[] = [this.ELIGIBLE, this.INELIGIBLE];
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
    project: ProjectDetailDTO,
    eligibilityAssessment: OutputProjectEligibilityAssessment
  }>;

  constructor(private router: Router,
              private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private projectStore: ProjectStore,
              private pageStore: ProjectEligibilityCheckPageStore,
              protected changeDetectorRef: ChangeDetectorRef,
              private sidenavService: ProjectApplicationFormSidenavService) {
    this.data$ = combineLatest([this.pageStore.project$, this.pageStore.eligibilityAssessment$])
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
    this.router.navigate(['app', 'project', 'detail', this.projectId]);
  }

  assessmentChangeHandler(event: any): void {
    this.selectedAssessment = event.value;
  }

  confirmEligibilityAssessment(): void {
    this.projectStore.setEligibilityAssessment({
      result: this.getEligibilityCheckValue(),
      note: this.notesForm?.controls?.notes?.value,
    });
    this.actionPending = false;
  }

  private getEligibilityCheckValue(): InputProjectEligibilityAssessment.ResultEnum {
    return this.selectedAssessment === this.INELIGIBLE
      ? InputProjectEligibilityAssessment.ResultEnum.FAILED
      : InputProjectEligibilityAssessment.ResultEnum.PASSED;
  }

  private setEligibilityCheckValue(eligibilityAssessment: OutputProjectEligibilityAssessment): void {
    if (eligibilityAssessment.result === InputProjectEligibilityAssessment.ResultEnum.FAILED) {
      this.notesForm.controls.assessment.setValue(this.INELIGIBLE);
      return;
    }
    this.notesForm.controls.assessment.setValue(this.ELIGIBLE);
  }

  getEligibilityCheckConfirmation(): ConfirmDialogData {
    return {
      title: 'project.assessment.eligibilityCheck.dialog.title',
      message: this.selectedAssessment === this.ELIGIBLE
        ? 'project.assessment.eligibilityCheck.dialog.message.eligible'
        : 'project.assessment.eligibilityCheck.dialog.message.ineligible'
    };
  }
}
