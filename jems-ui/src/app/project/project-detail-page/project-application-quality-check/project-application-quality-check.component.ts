import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {InputProjectQualityAssessment, OutputProjectQualityAssessment, ProjectDetailDTO} from '@cat/api';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {ProjectQualityCheckPageStore} from './project-quality-check-page-store.service';
import {map, tap} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {ProjectApplicationFormSidenavService} from '../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';

@Component({
  selector: 'app-project-application-quality-check',
  templateUrl: './project-application-quality-check.component.html',
  styleUrls: ['./project-application-quality-check.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ProjectQualityCheckPageStore]
})
export class ProjectApplicationQualityCheckComponent {
  RECOMMEND = 'RECOMMEND';
  RECOMMEND_WITH_CONDITIONS = 'RECOMMEND_WITH_CONDITIONS';
  NOT_RECOMMEND = 'NOT_RECOMMENDED';

  projectId = this.activatedRoute.snapshot.params.projectId;
  options: string[] = [this.RECOMMEND, this.RECOMMEND_WITH_CONDITIONS, this.NOT_RECOMMEND];

  data$: Observable<{
    project: ProjectDetailDTO,
    qualityAssessment: OutputProjectQualityAssessment
  }>;

  selectedAssessment: string;

  notesForm = this.formBuilder.group({
    assessment: ['', Validators.required],
    notes: ['', Validators.maxLength(1000)]
  });

  notesErrors = {
    maxlength: 'quality.check.notes.size.too.long',
  };

  actionPending = false;

  constructor(private router: Router,
              private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private pageStore: ProjectQualityCheckPageStore,
              private projectStore: ProjectStore,
              private sidenavService: ProjectApplicationFormSidenavService) {
    this.data$ = combineLatest([this.pageStore.project$, this.pageStore.qualityAssessment$])
      .pipe(
        tap(([project, qualityAssessment]) => {
          if (qualityAssessment) {
            this.setQualityCheckValue(qualityAssessment);
            this.notesForm.controls.notes.setValue(qualityAssessment.note);
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

  assessmentChangeHandler(event: any): void {
    this.selectedAssessment = event.value;
  }

  private confirmQualityAssessment(): void {
    this.projectStore.setQualityAssessment({
      result: this.getQualityCheckValue(),
      note: this.notesForm?.controls?.notes?.value,
    });
    this.actionPending = false;
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

  private setQualityCheckValue(qualityAssessment: OutputProjectQualityAssessment): void {
    if (qualityAssessment.result === InputProjectQualityAssessment.ResultEnum.RECOMMENDEDFORFUNDING) {
      this.notesForm.controls.assessment.setValue(this.RECOMMEND);
      return;
    }
    if (qualityAssessment.result === InputProjectQualityAssessment.ResultEnum.RECOMMENDEDWITHCONDITIONS) {
      this.notesForm.controls.assessment.setValue(this.RECOMMEND_WITH_CONDITIONS);
      return;
    }
    this.notesForm.controls.assessment.setValue(this.NOT_RECOMMEND);
  }

  getQualityCheckConfirmation(): ConfirmDialogData {
    let message = 'project.assessment.qualityCheck.dialog.message.not.recommended';
    if (this.selectedAssessment === this.RECOMMEND) {
      message = 'project.assessment.qualityCheck.dialog.message.recommended';
    }
    if (this.selectedAssessment === this.RECOMMEND_WITH_CONDITIONS) {
      message = 'project.assessment.qualityCheck.dialog.message.recommended.conditions';
    }
    return {
      title: 'project.assessment.qualityCheck.dialog.title',
      message
    };
  }
}
