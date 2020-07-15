import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';
import {Forms} from '../../../../../common/utils/forms';
import {take, takeUntil, tap} from 'rxjs/internal/operators';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {InputProjectQualityAssessment, ProjectStatusService, OutputProject} from '@cat/api';

@Component({
  selector: 'app-project-application-quality-check',
  templateUrl: './project-application-quality-check.component.html',
  styleUrls: ['./project-application-quality-check.component.scss'],
  providers: [
    ProjectStore
  ]
})
export class ProjectApplicationQualityCheckComponent extends AbstractForm implements OnInit {
  RECOMMEND = 'Project is RECOMMENDED for funding.';
  RECOMMEND_WITH_CONDITIONS = 'Project is RECOMMENDED WITH CONDITIONS.';
  NOT_RECOMMEND = 'Project is NOT RECOMMENDED for funding.';
  projectId = this.activatedRoute.snapshot.params.projectId;
  options: string[] = [this.RECOMMEND, this.RECOMMEND_WITH_CONDITIONS, this.NOT_RECOMMEND];
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
    protected changeDetectorRef: ChangeDetectorRef)
  {
    super(changeDetectorRef);
    this.projectStore.init(this.projectId);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.project$ = this.projectStore.getProject();
    this.project$.subscribe((project) => {
      if (project.qualityAssessment) {
        this.setQualityCheckValue(project);
        this.notesForm.controls.notes.setValue(project.qualityAssessment.note);
      }
    })
  }

  getForm(): FormGroup | null {
    return null;
  }

  onSubmit(): void {
    this.confirmQualityAssessment();
  }

  onCancel(): void {
    this.router.navigate(['project', this.projectId]);
  }

  assessmentChangeHandler (event: any) {
    this.selectedAssessment = event.value;
  }

  private confirmQualityAssessment(): void {
    console.log(this.selectedAssessment);
    Forms.confirmDialog(
      this.dialog,
      'project.assessment.qualityCheck.dialog.title',
      this.getQualityCheckMesage()
    ).pipe(
      take(1),
      takeUntil(this.destroyed$)
    ).subscribe(selectQuality => {
      if (selectQuality) {
        const qualityCheckResult = {
          result: this.getQualityCheckValue(),
          note: this.notesForm?.controls?.notes?.value,
        } as InputProjectQualityAssessment;
        this.projectStatusService.setQualityAssessment(this.projectId, qualityCheckResult)
          .pipe(
            takeUntil(this.destroyed$),
            tap(() => this.router.navigate(['project', this.projectId]))
          ).subscribe();
      }
    });
  }

  private getQualityCheckValue(): InputProjectQualityAssessment.ResultEnum {
    if (this.selectedAssessment === this.RECOMMEND) {
      return InputProjectQualityAssessment.ResultEnum.RECOMMENDEDFORFUNDING;
    }
    if (this.selectedAssessment === this.RECOMMEND_WITH_CONDITIONS) {
      return InputProjectQualityAssessment.ResultEnum.RECOMMENDEDWITHCONDITIONS;
    }
    return InputProjectQualityAssessment.ResultEnum.NOTRECOMMENDED;
  }

  private setQualityCheckValue(project: OutputProject): void {
    if (project.qualityAssessment.result === InputProjectQualityAssessment.ResultEnum.RECOMMENDEDFORFUNDING) {
      this.notesForm.controls.assessment.setValue(this.RECOMMEND);
      return;
    }
    if (project.qualityAssessment.result === InputProjectQualityAssessment.ResultEnum.RECOMMENDEDWITHCONDITIONS) {
      this.notesForm.controls.assessment.setValue(this.RECOMMEND_WITH_CONDITIONS);
      return;
    }
    this.notesForm.controls.assessment.setValue(this.NOT_RECOMMEND);
  }

  private getQualityCheckMesage(): string {
    if (this.selectedAssessment === this.RECOMMEND) {
      return 'project.assessment.qualityCheck.dialog.message.recommended';
    }
    if (this.selectedAssessment === this.RECOMMEND_WITH_CONDITIONS) {
      return 'project.assessment.qualityCheck.dialog.message.recommended.conditions';
    }
    return 'project.assessment.qualityCheck.dialog.message.not.recommended';
  }
}
