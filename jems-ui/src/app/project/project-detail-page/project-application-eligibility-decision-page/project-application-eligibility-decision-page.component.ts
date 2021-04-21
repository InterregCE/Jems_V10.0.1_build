import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Permission} from '../../../security/permissions/permission';
import {ProjectApplicationFormSidenavService} from '../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {FormBuilder, Validators} from '@angular/forms';
import {ApplicationActionInfoDTO, ProjectDetailDTO, ProjectStatusDTO} from '@cat/api';
import {map, tap} from 'rxjs/operators';
import {ProjectEligibilityDecisionStore} from './project-eligibility-decision-store.service';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {combineLatest} from 'rxjs';

@Component({
  selector: 'app-project-application-eligibility-decision-page',
  templateUrl: './project-application-eligibility-decision-page.component.html',
  styleUrls: ['./project-application-eligibility-decision-page.component.scss'],
  providers: [ProjectEligibilityDecisionStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationEligibilityDecisionPageComponent {
  Permission = Permission;

  projectId = this.activatedRoute.snapshot.params.projectId;
  data$ = combineLatest([this.eligibilityPageStore.project$, this.eligibilityPageStore.eligibilityDecision$])
    .pipe(
      tap(([project, eligibilityDecision]) => this.resetForm(project, eligibilityDecision)),
      map(([project, eligibilityDecision]) => ({project, eligibilityDecision}))
    );

  ELIGIBLE = 'ELIGIBLE';
  INELIGIBLE = 'INELIGIBLE';
  options: string[] = [this.ELIGIBLE, this.INELIGIBLE];

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

  selectedAssessment: string;
  actionPending = false;

  constructor(public eligibilityPageStore: ProjectEligibilityDecisionStore,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private sidenavService: ProjectApplicationFormSidenavService) {
  }

  private resetForm(project: ProjectDetailDTO, eligibilityDecision: ProjectStatusDTO): void {
    this.setEligibilityDecisionValue(project, eligibilityDecision);
    this.notesForm.controls.notes.setValue(eligibilityDecision?.note);
    this.notesForm.controls.decisionDate.setValue(eligibilityDecision?.decisionDate);
  }

  redirectToProjectDetail(): void {
    this.router.navigate(['app', 'project', 'detail', this.projectId]);
  }

  assessmentChangeHandler(event: any): void {
    this.selectedAssessment = event.value;
  }

  submitEligibilityDecision(): void {
    const statusInfo: ApplicationActionInfoDTO = {
      note: this.notesForm?.controls?.notes?.value,
      date: this.notesForm?.controls?.decisionDate?.value.format('YYYY-MM-DD')
    };
    (this.selectedAssessment === this.ELIGIBLE
      ? this.eligibilityPageStore.setApplicationAsEligible(this.projectId, statusInfo)
      : this.eligibilityPageStore.setApplicationAsIneligible(this.projectId, statusInfo))
      .pipe(
        tap(() => this.actionPending = false),
        tap(() => this.redirectToProjectDetail())
      ).subscribe();
  }

  private setEligibilityDecisionValue(project: ProjectDetailDTO, eligibilityDecision: ProjectStatusDTO): void {
    if (eligibilityDecision) {
      if (eligibilityDecision?.status === ProjectStatusDTO.StatusEnum.INELIGIBLE) {
        this.notesForm.controls.assessment.setValue(this.INELIGIBLE);
      } else {
        this.notesForm.controls.assessment.setValue(this.ELIGIBLE);
      }
    }
  }

  getEligibilityDecisionConfirmation(): ConfirmDialogData {
    let message = 'project.assessment.eligibilityDecision.dialog.message.ineligible';
    if (this.selectedAssessment === this.ELIGIBLE) {
      message = 'project.assessment.eligibilityDecision.dialog.message.eligible';
    }
    return {
      title: 'project.assessment.eligibilityDecision.dialog.title',
      message
    };
  }

}
