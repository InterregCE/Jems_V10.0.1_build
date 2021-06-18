import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';
import {ApplicationActionInfoDTO, ProjectDetailDTO, ProjectStatusDTO, UserRoleDTO} from '@cat/api';
import {map, tap} from 'rxjs/operators';
import {ProjectEligibilityDecisionStore} from './project-eligibility-decision-store.service';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {combineLatest} from 'rxjs';
import {ProjectStepStatus} from '../project-step-status';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'app-project-application-eligibility-decision-page',
  templateUrl: './project-application-eligibility-decision-page.component.html',
  styleUrls: ['./project-application-eligibility-decision-page.component.scss'],
  providers: [ProjectEligibilityDecisionStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationEligibilityDecisionPageComponent {
  PermissionsEnum = PermissionsEnum;

  projectId = this.activatedRoute.snapshot.params.projectId;
  step = this.activatedRoute.snapshot.params.step;
  stepStatus = new ProjectStepStatus(this.step);

  data$ = combineLatest([
    this.eligibilityPageStore.project$,
    this.eligibilityPageStore.eligibilityDecision(this.step)]
  )
    .pipe(
      tap(([project, eligibilityDecision]) => this.resetForm(project, eligibilityDecision)),
      map(([project, eligibilityDecision]) => ({project, eligibilityDecision}))
    );

  options: string[] = [this.stepStatus.eligible, this.stepStatus.ineligible];

  today = new Date();
  dateErrors = {
    required: 'common.error.field.required',
    matDatepickerMax: 'project.decision.date.must.be.in.the.past',
    matDatepickerParse: 'common.date.should.be.valid'
  };

  notesForm = this.formBuilder.group({
    assessment: ['', Validators.required],
    notes: ['', Validators.maxLength(1000)],
    decisionDate: ['', Validators.required]
  });

  notesErrors = {
    maxlength: 'eligibility.decision.notes.size.too.long',
  };

  actionPending = false;

  constructor(public eligibilityPageStore: ProjectEligibilityDecisionStore,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder) {
  }

  private resetForm(project: ProjectDetailDTO, eligibilityDecision: ProjectStatusDTO): void {
    this.setEligibilityDecisionValue(project, eligibilityDecision);
    this.notesForm.controls.notes.setValue(eligibilityDecision?.note);
    this.notesForm.controls.decisionDate.setValue(eligibilityDecision?.decisionDate);
  }

  redirectToProjectDetail(): void {
    this.router.navigate(['app', 'project', 'detail', this.projectId]);
  }

  submitEligibilityDecision(): void {
    this.actionPending = true;
    const statusInfo: ApplicationActionInfoDTO = {
      note: this.notesForm?.controls?.notes?.value,
      date: this.notesForm?.controls?.decisionDate?.value.format('YYYY-MM-DD')
    };
    (this.notesForm.get('assessment')?.value === this.stepStatus.eligible
      ? this.eligibilityPageStore.setApplicationAsEligible(this.projectId, statusInfo)
      : this.eligibilityPageStore.setApplicationAsIneligible(this.projectId, statusInfo))
      .pipe(
        tap(() => this.actionPending = false),
        tap(() => this.redirectToProjectDetail())
      ).subscribe();
  }

  private setEligibilityDecisionValue(project: ProjectDetailDTO, eligibilityDecision: ProjectStatusDTO): void {
    this.notesForm.controls.assessment.setValue(eligibilityDecision?.status);
  }

  getEligibilityDecisionConfirmation(): ConfirmDialogData {
    let message = 'project.assessment.eligibilityDecision.dialog.message.ineligible';
    if (this.notesForm.get('assessment')?.value === this.stepStatus.eligible) {
      message = 'project.assessment.eligibilityDecision.dialog.message.eligible';
    }
    return {
      title: 'project.assessment.eligibilityDecision.dialog.title',
      message
    };
  }

}
