import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges
} from '@angular/core';
import {InputProjectOverallObjective, OutputProgrammePriorityPolicySimpleDTO} from '@cat/api';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {TranslateService} from '@ngx-translate/core';
import {BaseComponent} from '@common/components/base-component';
import {HttpErrorResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {FormService} from '@common/components/section/form/form.service';
import {takeUntil, tap} from 'rxjs/operators';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {Alert} from '@common/components/forms/alert';

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
  // TODO: remove these and adapt the component to save independently
  @Input()
  error$: Observable<HttpErrorResponse | null>;
  @Input()
  success$: Observable<any>;

  @Input()
  editable: boolean;
  @Input()
  project: InputProjectOverallObjective;
  @Input()
  specificObjective: OutputProgrammePriorityPolicySimpleDTO;
  @Output()
  updateData = new EventEmitter<InputProjectOverallObjective>();

  overallObjectiveForm: FormGroup = this.formBuilder.group({
    projectSpecificObjective: ['', Validators.required],
    projectOverallObjective: [[], Validators.maxLength(500)]
  });

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private translate: TranslateService,
              private projectStore: ProjectStore) {
    super();
  }

  ngOnInit(): void {
    this.formService.init(this.overallObjectiveForm, this.projectStore.projectEditable$);
    this.error$
      .pipe(
        takeUntil(this.destroyed$),
        tap(err => this.formService.setError(err))
      )
      .subscribe();
    this.success$
      .pipe(
        takeUntil(this.destroyed$),
        tap(() => this.formService.setSuccess('project.application.form.overall.objective.save.success'))
      )
      .subscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.project || changes.specificObjective) {
      this.resetForm();
    }
  }

  onSubmit(): void {
    this.updateData.emit({
      overallObjective: this.overallObjectiveForm.get('projectOverallObjective')?.value
    });
  }

  resetForm(): void {
    if (this.specificObjective) {
      this.overallObjectiveForm.controls.projectSpecificObjective.setValue(
        this.specificObjective.code
          .concat(': ')
          .concat(this.translate.instant('programme.policy.' + this.specificObjective.programmeObjectivePolicy))
      );
    }
    this.overallObjectiveForm.get('projectOverallObjective')?.setValue(this.project?.overallObjective || []);
  }
}
