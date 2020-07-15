import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';
import {Forms} from '../../../../../common/utils/forms';
import {take, takeUntil, tap} from 'rxjs/internal/operators';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {InputProjectEligibilityAssessment, ProjectStatusService, OutputProject} from '@cat/api';

@Component({
  selector: 'app-project-application-eligibility-check',
  templateUrl: './project-application-eligibility-check.component.html',
  styleUrls: ['./project-application-eligibility-check.component.scss'],
  providers: [
    ProjectStore
  ]
})
export class ProjectApplicationEligibilityCheckComponent extends AbstractForm implements OnInit {
  ELIGIBLE = 'Project has PASSED ELIGIBILITY assessment.';
  INELIGIBLE = 'Project has FAILED ELIGIBILITY assessment.'
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
    private projectStatusService: ProjectStatusService,
    protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
    this.projectStore.init(this.projectId);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.project$ = this.projectStore.getProject();
    this.project$.subscribe((project) => {
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
    console.log(this.selectedAssessment);
    Forms.confirmDialog(
      this.dialog,
      'project.assessment.eligibilityCheck.dialog.title',
      this.getEligibilityCheckMesage()
    ).pipe(
      take(1),
      takeUntil(this.destroyed$)
    ).subscribe(selectEligibility => {
      if (selectEligibility) {
        const eligibilityCheckResult = {
          result: this.getEligibilityCheckValue(),
          note: this.notesForm?.controls?.notes?.value,
        } as InputProjectEligibilityAssessment;
        this.projectStatusService.setEligibilityAssessment(this.projectId, eligibilityCheckResult)
          .pipe(
            takeUntil(this.destroyed$),
            tap(() => this.router.navigate(['project', this.projectId]))
          ).subscribe();
      }
    });
  }

  private getEligibilityCheckValue(): InputProjectEligibilityAssessment.ResultEnum {
    if (this.selectedAssessment === this.INELIGIBLE) {
      return InputProjectEligibilityAssessment.ResultEnum.FAILED;
    }
    return InputProjectEligibilityAssessment.ResultEnum.PASSED;

  }

  private setEligibilityCheckValue(project: OutputProject): void {
    if (project.eligibilityAssessment.result === InputProjectEligibilityAssessment.ResultEnum.FAILED) {
      this.notesForm.controls.assessment.setValue(this.INELIGIBLE);
      return;
    }
    this.notesForm.controls.assessment.setValue(this.ELIGIBLE);
  }

  private getEligibilityCheckMesage(): string {
    if (this.selectedAssessment === this.ELIGIBLE) {
      return 'project.assessment.eligibilityCheck.dialog.message.eligible';
    }
    return 'project.assessment.eligibilityCheck.dialog.message.ineligible';
  }
}
