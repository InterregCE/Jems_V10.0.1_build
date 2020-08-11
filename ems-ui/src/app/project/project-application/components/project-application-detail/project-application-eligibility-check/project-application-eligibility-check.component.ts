import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';
import {Forms} from '../../../../../common/utils/forms';
import {filter, take, takeUntil} from 'rxjs/internal/operators';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {InputProjectEligibilityAssessment, OutputProject} from '@cat/api';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-project-application-eligibility-check',
  templateUrl: './project-application-eligibility-check.component.html',
  styleUrls: ['./project-application-eligibility-check.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationEligibilityCheckComponent extends AbstractForm implements OnInit {
  ELIGIBLE = 'ELIGIBLE';
  INELIGIBLE = 'INELIGIBLE'
  // TODO move id to a container
  projectId = this.activatedRoute.snapshot.params.projectId;
  options: string[] = [this.ELIGIBLE, this.INELIGIBLE];
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
  }

  ngOnInit(): void {
    super.ngOnInit();
    // TODO move the project$ to a container and use it here as Input()
    this.project$ = this.projectStore.getProject();
    this.project$
      .pipe(takeUntil(this.destroyed$))
      .subscribe((project) => {
        if (project.eligibilityAssessment) {
          this.setEligibilityCheckValue(project);
          this.notesForm.controls.notes.setValue(project.eligibilityAssessment.note);
        }
      })
  }

  getForm(): FormGroup | null {
    return null;
  }

  onSubmit(): void {
    this.confirmEligibilityAssessment();
  }

  onCancel(): void {
    this.router.navigate(['project', this.projectId]);
  }

  assessmentChangeHandler(event: any) {
    this.selectedAssessment = event.value;
  }

  private confirmEligibilityAssessment(): void {
    Forms.confirmDialog(
      this.dialog,
      'project.assessment.eligibilityCheck.dialog.title',
      this.getEligibilityCheckMesage()
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(selectEligibility => !!selectEligibility)
    ).subscribe(() =>
      this.projectStore.setEligibilityAssessment({
        result: this.getEligibilityCheckValue(),
        note: this.notesForm?.controls?.notes?.value,
      })
    );
  }

  private getEligibilityCheckValue(): InputProjectEligibilityAssessment.ResultEnum {
    return this.selectedAssessment === this.INELIGIBLE
      ? InputProjectEligibilityAssessment.ResultEnum.FAILED
      : InputProjectEligibilityAssessment.ResultEnum.PASSED;
  }

  private setEligibilityCheckValue(project: OutputProject): void {
    if (project.eligibilityAssessment.result === InputProjectEligibilityAssessment.ResultEnum.FAILED) {
      this.notesForm.controls.assessment.setValue(this.INELIGIBLE);
      return;
    }
    this.notesForm.controls.assessment.setValue(this.ELIGIBLE);
  }

  private getEligibilityCheckMesage(): string {
    return this.selectedAssessment === this.ELIGIBLE
      ? 'project.assessment.eligibilityCheck.dialog.message.eligible'
      : 'project.assessment.eligibilityCheck.dialog.message.ineligible';
  }
}
