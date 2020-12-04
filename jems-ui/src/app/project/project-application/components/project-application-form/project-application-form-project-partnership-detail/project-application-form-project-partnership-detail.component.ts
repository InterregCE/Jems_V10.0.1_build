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
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {InputProjectPartnership} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';
import {FormService} from '@common/components/section/form/form.service';
import {Observable} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {takeUntil, tap} from 'rxjs/operators';
import {MultiLanguageInput} from '@common/components/forms/multi-language/multi-language-input';
import {MultiLanguageInputService} from '../../../../../common/services/multi-language-input.service';

@Component({
  selector: 'app-project-application-form-project-partnership-detail',
  templateUrl: './project-application-form-project-partnership-detail.component.html',
  styleUrls: ['./project-application-form-project-partnership-detail.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormProjectPartnershipDetailComponent extends BaseComponent implements OnInit, OnChanges {
  // TODO: remove these and adapt the component to save independently
  @Input()
  error$: Observable<HttpErrorResponse | null>;
  @Input()
  success$: Observable<any>;

  @Input()
  editable: boolean;
  @Input()
  project: InputProjectPartnership;
  @Output()
  updateData = new EventEmitter<InputProjectPartnership>();

  projectPartnership: MultiLanguageInput;

  projectPartnershipForm: FormGroup = this.formBuilder.group({
    projectPartnership: ['', Validators.maxLength(5000)]
  });

  projectPartnershipErrors = {
    maxlength: 'project.application.form.project.partnership.entered.text.size.too.long'
  };

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              public languageService: MultiLanguageInputService) {
    super();
  }

  ngOnInit(): void {
    this.resetForm();
    this.formService.init(this.projectPartnershipForm);
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
        tap(() => this.formService.setSuccess('project.application.form.project.partnership.save.success'))
      )
      .subscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.project) {
      this.resetForm();
    }
  }

  getForm(): FormGroup | null {
    return this.projectPartnershipForm;
  }

  onSubmit(): void {
    this.updateData.emit({
      partnership: this.projectPartnership.inputs
    });
  }

  resetForm(): void {
    this.projectPartnership = this.languageService.initInput(
      this.project?.partnership, this.projectPartnershipForm.controls.projectPartnership
    );
  }

  private formValid(): boolean {
    return this.projectPartnership.isValid();
  }
}
