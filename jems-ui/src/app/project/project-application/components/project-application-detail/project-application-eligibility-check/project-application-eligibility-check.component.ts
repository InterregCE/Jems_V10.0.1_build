import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';
import {takeUntil} from 'rxjs/internal/operators';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {InputProjectEligibilityAssessment, ProjectDetailDTO} from '@cat/api';
import {Observable} from 'rxjs';
import {TranslateService} from '@ngx-translate/core';
import {ProjectApplicationFormSidenavService} from '../../../containers/project-application-form-page/services/project-application-form-sidenav.service';

@Component({
  selector: 'app-project-application-eligibility-check',
  templateUrl: './project-application-eligibility-check.component.html',
  styleUrls: ['./project-application-eligibility-check.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationEligibilityCheckComponent extends AbstractForm implements OnInit {
  ELIGIBLE = 'ELIGIBLE';
  INELIGIBLE = 'INELIGIBLE';
  // TODO move id to a container
  projectId = this.activatedRoute.snapshot.params.projectId;
  options: string[] = [this.ELIGIBLE, this.INELIGIBLE];
  project$: Observable<ProjectDetailDTO>;
  selectedAssessment: string;
  actionPending = false;

  notesForm = this.formBuilder.group({
    assessment: ['', Validators.required],
    notes: ['', Validators.maxLength(1000)]
  });

  notesErrors = {
    maxlength: 'eligibility.check.notes.size.too.long',
  };


  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private projectStore: ProjectStore,
    protected changeDetectorRef: ChangeDetectorRef,
    protected translationService: TranslateService,
    private sidenavService: ProjectApplicationFormSidenavService) {
    super(changeDetectorRef, translationService);
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
      });
  }

  getForm(): FormGroup | null {
    return null;
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

  private setEligibilityCheckValue(project: ProjectDetailDTO): void {
    if (project.eligibilityAssessment.result === InputProjectEligibilityAssessment.ResultEnum.FAILED) {
      this.notesForm.controls.assessment.setValue(this.INELIGIBLE);
      return;
    }
    this.notesForm.controls.assessment.setValue(this.ELIGIBLE);
  }

  getEligibilityCheckMesage(): string {
    return this.selectedAssessment === this.ELIGIBLE
      ? 'project.assessment.eligibilityCheck.dialog.message.eligible'
      : 'project.assessment.eligibilityCheck.dialog.message.ineligible';
  }
}
