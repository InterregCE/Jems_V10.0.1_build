import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {InputProjectOverallObjective, OutputProgrammePriorityPolicySimple} from '@cat/api';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {TranslateService} from '@ngx-translate/core';
import {BaseComponent} from '@common/components/base-component';
import {HttpErrorResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {FormService} from '@common/components/section/form/form.service';
import {takeUntil, tap} from 'rxjs/operators';
import {MultiLanguageInput} from '@common/components/forms/multi-language/multi-language-input';
import {MultiLanguageInputService} from '../../../../../common/services/multi-language-input.service';

@Component({
  selector: 'app-project-application-form-overall-objective-detail',
  templateUrl: './project-application-form-overall-objective-detail.component.html',
  styleUrls: ['./project-application-form-overall-objective-detail.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormOverallObjectiveDetailComponent extends BaseComponent implements OnInit {

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
  specificObjective: OutputProgrammePriorityPolicySimple;
  @Output()
  updateData = new EventEmitter<InputProjectOverallObjective>();

  projectOverallObjective: MultiLanguageInput;

  overallObjectiveForm: FormGroup = this.formBuilder.group({
    projectSpecificObjective: ['', Validators.required],
    projectOverallObjective: ['', Validators.maxLength(500)]
  });

  projectOverallObjectiveError = {
    maxlength: 'project.application.form.overall.objective.entered.text.size.too.long'
  };

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private translate: TranslateService,
              public languageService: MultiLanguageInputService) {
    super();
  }

  ngOnInit(): void {
    this.resetForm();
    this.formService.init(this.overallObjectiveForm);
    this.formService.setAdditionalValidators([this.formValid.bind(this)]);
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

  onSubmit(): void {
    this.updateData.emit({
      overallObjective: this.projectOverallObjective.inputs
    });
  }

  resetForm(): void {
    if (this.specificObjective) {
      this.overallObjectiveForm.controls.projectSpecificObjective.setValue(this.translate.instant('programme.policy.' + this.specificObjective.programmeObjectivePolicy));
    }
    this.projectOverallObjective = this.languageService.initInput(
      this.project?.overallObjective, this.overallObjectiveForm.controls.projectOverallObjective
    );
  }

  private formValid(): boolean {
    return this.projectOverallObjective.isValid();
  }
}
