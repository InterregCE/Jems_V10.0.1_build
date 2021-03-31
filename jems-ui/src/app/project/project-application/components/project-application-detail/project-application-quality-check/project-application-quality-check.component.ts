import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';
import {Forms} from '../../../../../common/utils/forms';
import {filter, take, takeUntil} from 'rxjs/internal/operators';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {InputProjectQualityAssessment, OutputProject} from '@cat/api';
import {TranslateService} from '@ngx-translate/core';
import {ProjectApplicationFormSidenavService} from '../../../containers/project-application-form-page/services/project-application-form-sidenav.service';

@Component({
  selector: 'app-project-application-quality-check',
  templateUrl: './project-application-quality-check.component.html',
  styleUrls: ['./project-application-quality-check.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationQualityCheckComponent extends AbstractForm implements OnInit {
  RECOMMEND = 'RECOMMEND';
  RECOMMEND_WITH_CONDITIONS = 'RECOMMEND_WITH_CONDITIONS';
  NOT_RECOMMEND = 'NOT_RECOMMENDED';
  // TODO move to container, use as Input()
  projectId = this.activatedRoute.snapshot.params.projectId;
  options: string[] = [this.RECOMMEND, this.RECOMMEND_WITH_CONDITIONS, this.NOT_RECOMMEND];
  project$: Observable<OutputProject>;
  selectedAssessment: string;

  notesForm = this.formBuilder.group({
    assessment: ['', Validators.required],
    notes: ['', Validators.maxLength(1000)]
  });

  notesErrors = {
    maxlength: 'quality.check.notes.size.too.long',
  };

  constructor(
    private dialog: MatDialog,
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
    // TODO move to container, use as Input()
    this.project$ = this.projectStore.getProject();
    this.project$.subscribe((project) => {
      if (project.qualityAssessment) {
        this.setQualityCheckValue(project);
        this.notesForm.controls.notes.setValue(project.qualityAssessment.note);
      }
    });
  }

  getForm(): FormGroup | null {
    return null;
  }

  onSubmit(): void {
    this.confirmQualityAssessment();
  }

  onCancel(): void {
    // TODO move to container, use as Input()
    this.router.navigate(['app', 'project', 'detail', this.projectId]);
  }

  assessmentChangeHandler(event: any): void {
    this.selectedAssessment = event.value;
  }

  private confirmQualityAssessment(): void {
    Forms.confirmDialog(
      this.dialog,
      'project.assessment.qualityCheck.dialog.title',
      this.getQualityCheckMesage()
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(selectQuality => !!selectQuality)
    ).subscribe(() =>
      this.projectStore.setQualityAssessment({
        result: this.getQualityCheckValue(),
        note: this.notesForm?.controls?.notes?.value,
      })
    );
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
