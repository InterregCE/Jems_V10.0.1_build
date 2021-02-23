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
import {MultiLanguageInputService} from '../../../../../common/services/multi-language-input.service';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';

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
  project: InputProjectPartnership;
  @Output()
  updateData = new EventEmitter<InputProjectPartnership>();

  projectPartnershipForm: FormGroup = this.formBuilder.group({
    partnership: ['', Validators.maxLength(5000)]
  });

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              public languageService: MultiLanguageInputService,
              private projectStore: ProjectStore) {
    super();
  }

  ngOnInit(): void {
    this.resetForm();
    this.formService.init(this.projectPartnershipForm, this.projectStore.projectEditable$);
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

  onSubmit(): void {
    this.updateData.emit(this.projectPartnershipForm.value);
  }

  resetForm(): void {
    this.projectPartnershipForm.get('partnership')?.setValue(this.project?.partnership || []);
  }

}
