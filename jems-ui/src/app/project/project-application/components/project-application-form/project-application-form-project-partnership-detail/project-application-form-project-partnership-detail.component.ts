import {
  ChangeDetectionStrategy,
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges
} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {InputProjectPartnership, ProjectDescriptionService} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';
import {FormService} from '@common/components/section/form/form.service';
import {catchError, tap} from 'rxjs/operators';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';
import {Log} from '@common/utils/log';

@Component({
  selector: 'jems-project-application-form-project-partnership-detail',
  templateUrl: './project-application-form-project-partnership-detail.component.html',
  styleUrls: ['./project-application-form-project-partnership-detail.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormProjectPartnershipDetailComponent extends BaseComponent implements OnInit, OnChanges {

  @Input()
  projectId: number;
  @Input()
  inputProjectPartnership: InputProjectPartnership;

  projectPartnershipForm: FormGroup = this.formBuilder.group({
    partnership: ['', Validators.maxLength(5000)]
  });

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private projectStore: ProjectStore,
              private projectDescriptionService: ProjectDescriptionService) {
    super();
  }

  ngOnInit(): void {
    this.formService.init(this.projectPartnershipForm, this.projectStore.projectEditable$);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.inputProjectPartnership) {
      this.resetForm();
    }
  }

  onSubmit(): void {
    this.projectDescriptionService.updateProjectPartnership(this.projectId,
      <InputProjectPartnership>{
        partnership: this.projectPartnershipForm.get('partnership')?.value
      })
      .pipe(
        tap(saved => Log.info('Updated project partnership:', this, saved)),
        tap(() => this.formService.setSuccess('project.application.form.save.success')),
        catchError(error => this.formService.setError(error))
      ).subscribe();
  }

  resetForm(): void {
    this.projectPartnershipForm.get('partnership')?.setValue(this.inputProjectPartnership?.partnership || []);
  }

}
