import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectAssessmentQualityDTO, OutputProjectQualityAssessment, ProjectDetailDTO} from '@cat/api';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {ProjectQualityCheckPageStore} from './project-quality-check-page-store.service';
import {map, tap} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {ProjectStepStatus} from '../project-step-status';

@Component({
  selector: 'app-project-application-quality-check',
  templateUrl: './project-application-quality-check.component.html',
  styleUrls: ['./project-application-quality-check.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ProjectQualityCheckPageStore]
})
export class ProjectApplicationQualityCheckComponent {
  step = this.activatedRoute.snapshot.params.step;
  stepStatus = new ProjectStepStatus(this.step);
  assessment = ProjectAssessmentQualityDTO.ResultEnum;
  projectId = this.activatedRoute.snapshot.params.projectId;
  options: string[] = [this.assessment.RECOMMENDEDFORFUNDING, this.assessment.RECOMMENDEDWITHCONDITIONS, this.assessment.NOTRECOMMENDED];

  data$: Observable<{
    project: ProjectDetailDTO,
    qualityAssessment: OutputProjectQualityAssessment
  }>;

  qualityCheckForm = this.formBuilder.group({
    result: ['', Validators.required],
    note: ['', Validators.maxLength(1000)]
  });

  notesErrors = {
    maxlength: 'quality.check.notes.size.too.long',
  };

  actionPending = false;

  constructor(private router: Router,
              private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private pageStore: ProjectQualityCheckPageStore,
              private projectStore: ProjectStore) {
    this.data$ = combineLatest([
      this.pageStore.project$,
      this.pageStore.qualityAssessment(this.step)]
    )
      .pipe(
        tap(([project, qualityAssessment]) => {
          if (qualityAssessment) {
            this.qualityCheckForm.controls.result.setValue(qualityAssessment.result);
            this.qualityCheckForm.controls.note.setValue(qualityAssessment.note);
          }
        }),
        map(([project, qualityAssessment]) => ({project, qualityAssessment}))
      );
  }

  onSubmit(): void {
    this.confirmQualityAssessment();
  }

  onCancel(): void {
    this.router.navigate(['app', 'project', 'detail', this.projectId]);
  }

  private confirmQualityAssessment(): void {
    this.actionPending = true;
    this.projectStore.setQualityAssessment(this.qualityCheckForm.value);
    this.actionPending = false;
  }

  getQualityCheckConfirmation(): ConfirmDialogData {
    let message = 'project.assessment.qualityCheck.dialog.message.not.recommended';
    if (this.qualityCheckForm.get('result')?.value === this.assessment.RECOMMENDEDFORFUNDING) {
      message = 'project.assessment.qualityCheck.dialog.message.recommended';
    }
    if (this.qualityCheckForm.get('result')?.value === this.assessment.RECOMMENDEDWITHCONDITIONS) {
      message = 'project.assessment.qualityCheck.dialog.message.recommended.conditions';
    }
    return {
      title: 'project.assessment.qualityCheck.dialog.title',
      message
    };
  }
}
