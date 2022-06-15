import {ChangeDetectionStrategy, Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {
  InputProjectOverallObjective,
  OutputProgrammePriorityPolicySimpleDTO,
  ProjectDescriptionService
} from '@cat/api';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {BaseComponent} from '@common/components/base-component';
import {FormService} from '@common/components/section/form/form.service';
import {catchError, tap} from 'rxjs/operators';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {Alert} from '@common/components/forms/alert';
import {Log} from '@common/utils/log';

@Component({
  selector: 'jems-project-application-form-overall-objective-detail',
  templateUrl: './project-application-form-overall-objective-detail.component.html',
  styleUrls: ['./project-application-form-overall-objective-detail.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormOverallObjectiveDetailComponent extends BaseComponent implements OnInit, OnChanges {

  Alert = Alert;
  APPLICATION_FORM = APPLICATION_FORM;

  @Input()
  projectId: number;
  @Input()
  editable: boolean;
  @Input()
  inputProjectOverallObjective: InputProjectOverallObjective;
  @Input()
  specificObjective: OutputProgrammePriorityPolicySimpleDTO;

  overallObjectiveForm: FormGroup = this.formBuilder.group({
    projectSpecificObjective: ['', Validators.required],
    projectOverallObjective: [[], Validators.maxLength(500)]
  });

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private translate: TranslateService,
              private projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private projectDescriptionService: ProjectDescriptionService) {
    super();
  }

  ngOnInit(): void {
    this.formService.init(this.overallObjectiveForm, this.projectStore.projectEditable$);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.inputProjectOverallObjective || changes.specificObjective) {
      this.resetForm();
    }
  }

  onSubmit(): void {
    this.projectDescriptionService.updateProjectOverallObjective(
      this.projectId,
      {
        overallObjective: this.overallObjectiveForm.get('projectOverallObjective')?.value
      } as InputProjectOverallObjective
    ).pipe(
        tap(saved => Log.info('Updated project overall objective:', this, saved)),
        tap(() => this.formService.setSuccess('project.application.form.save.success')),
        catchError(error => this.formService.setError(error))
      ).subscribe();
  }

  resetForm(): void {
    if (this.specificObjective) {
      this.overallObjectiveForm.controls.projectSpecificObjective.setValue(
        this.specificObjective.code
          .concat(': ')
          .concat(this.translate.instant('programme.policy.' + this.specificObjective.programmeObjectivePolicy))
      );
    }
    this.overallObjectiveForm.get('projectOverallObjective')?.setValue(
      this.inputProjectOverallObjective?.overallObjective || []);
  }
}
