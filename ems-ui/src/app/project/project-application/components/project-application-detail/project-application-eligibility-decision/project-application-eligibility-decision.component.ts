import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {Forms} from '../../../../../common/utils/forms';
import {take, takeUntil} from 'rxjs/internal/operators';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';
import {Observable} from 'rxjs';
import {InputProjectStatus, OutputProject, OutputProjectStatus} from '@cat/api';

@Component({
  selector: 'app-project-eligibility-decision',
  templateUrl: './project-application-eligibility-decision.component.html',
  styleUrls: ['./project-application-eligibility-decision.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationEligibilityDecisionComponent extends AbstractForm implements OnInit {
  OutputProjectStatus = OutputProjectStatus;
  projectId = this.activatedRoute.snapshot.params.projectId;
  ELIGIBLE = 'Project is ELIGIBLE.'
  INELIGIBLE = 'Project is INELIGIBLE.'
  options: string[] = [this.ELIGIBLE, this.INELIGIBLE];
  project$: Observable<OutputProject>;
  selectedAssessment: string;

  notesForm = this.formBuilder.group({
    assessment: ['', Validators.required],
    notes: ['', Validators.maxLength(1000)],
    decisionDate: ['', Validators.required]
  });

  constructor(
    private dialog: MatDialog,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private projectStore: ProjectStore,
    protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    // TODO move to container, use as Input()
    this.project$ = this.projectStore.getProject();
    this.project$.subscribe((project) => {
      if (project.projectStatus.status === OutputProjectStatus.StatusEnum.ELIGIBLE
        || project.projectStatus.status === OutputProjectStatus.StatusEnum.INELIGIBLE) {
          this.setEligibilityDecisionValue(project);
          this.notesForm.controls.notes.setValue(project.eligibilityDecision.note);
          this.notesForm.controls.decisionDate.setValue(project.eligibilityDecision.decisionDate);
      }
    })
  }

  getForm(): FormGroup | null {
    return null;
  }

  onSubmit(): void {
    this.confirmEligibilityDecision();
  }

  onCancel(): void {
    this.router.navigate(['project', this.projectId]);
  }

  assessmentChangeHandler(event: any) {
    this.selectedAssessment = event.value;
  }

  private confirmEligibilityDecision(): void {
    console.log(this.selectedAssessment);
    Forms.confirmDialog(
      this.dialog,
      'project.assessment.eligibilityDecision.dialog.title',
      this.getEligibilityDecisionMessage()
    ).pipe(
      take(1),
      takeUntil(this.destroyed$)
    ).subscribe(selectEligibility => {
      if (selectEligibility) {
        // TODO move service + router call to container
        this.projectStore.changeStatus({
          status: this.getEligibilityDecisionValue(),
          note: this.notesForm?.controls?.notes?.value,
          date: this.notesForm?.controls?.decisionDate?.value.format('YYYY-MM-DD')
        })
      }
    });
  }

  private getEligibilityDecisionValue(): OutputProjectStatus.StatusEnum {
    return this.selectedAssessment === this.INELIGIBLE
      ? OutputProjectStatus.StatusEnum.INELIGIBLE
      : OutputProjectStatus.StatusEnum.ELIGIBLE
  }

  private setEligibilityDecisionValue(project: OutputProject): void {
    if (project.eligibilityDecision.status === OutputProjectStatus.StatusEnum.INELIGIBLE) {
      this.notesForm.controls.assessment.setValue(this.INELIGIBLE);
      return;
    }
    this.notesForm.controls.assessment.setValue(this.ELIGIBLE);
  }

  private getEligibilityDecisionMessage(): string {
    if (this.selectedAssessment === this.ELIGIBLE) {
      return 'project.assessment.eligibilityDecision.dialog.message.eligible';
    }
    return 'project.assessment.eligibilityDecision.dialog.message.ineligible';
  }
}
