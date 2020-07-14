import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {Forms} from '../../../../../common/utils/forms';
import {take, takeUntil} from 'rxjs/internal/operators';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';
import {Observable} from 'rxjs';
import {InputProjectStatus, OutputProject} from '@cat/api';

@Component({
  selector: 'app-project-eligibility-decision',
  templateUrl: './project-application-eligibility-decision.component.html',
  styleUrls: ['./project-application-eligibility-decision.component.scss'],
  providers: [
    ProjectStore
  ]
})
export class ProjectApplicationEligibilityDecisionComponent extends AbstractForm implements OnInit {
  projectId = this.activatedRoute.snapshot.params.projectId;
  options: string[] = ['Eligible', 'Ineligible'];
  project$: Observable<OutputProject>;
  selectedAssessment: string;

  notesForm = this.formBuilder.group({
    assessment: ['', Validators.required],
    notes: ['', Validators.maxLength(1000)]
  });

  constructor(
    private dialog: MatDialog,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private projectStore: ProjectStore,
    protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
    this.projectStore.init(this.projectId);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.project$ = this.projectStore.getProject();
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
      'Are you sure you want to submit the eligibility decision as '
      + this.selectedAssessment
      + '? Operation cannot be reversed.'
    ).pipe(
      take(1),
      takeUntil(this.destroyed$)
    ).subscribe(selectEligibility => {
      if (selectEligibility) {
        this.projectStore.changeStatus(this.getNewEligibilityStatus(this.selectedAssessment));
        this.router.navigate(['project', this.projectId]);
      }
    });
  }

  private getNewEligibilityStatus(selectedDecision: string): InputProjectStatus.StatusEnum {
    return selectedDecision === 'Eligible' ? InputProjectStatus.StatusEnum.ELIGIBLE
      : InputProjectStatus.StatusEnum.INELIGIBLE;
  }

}
