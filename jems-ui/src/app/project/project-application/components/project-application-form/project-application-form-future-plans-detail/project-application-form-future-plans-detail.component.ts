import {
  ChangeDetectionStrategy,
  Component,
  Input, OnChanges,
  OnInit,
  SimpleChanges
} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {OutputProjectLongTermPlans, ProjectDescriptionService} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';
import {FormService} from '@common/components/section/form/form.service';
import {catchError, tap} from 'rxjs/operators';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {Log} from '@common/utils/log';

@Component({
  selector: 'jems-project-application-form-future-plans-detail',
  templateUrl: './project-application-form-future-plans-detail.component.html',
  styleUrls: ['./project-application-form-future-plans-detail.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormFuturePlansDetailComponent extends BaseComponent implements OnInit, OnChanges {

  APPLICATION_FORM = APPLICATION_FORM;

  @Input()
  projectId: number;
  @Input()
  outputProjectLongTermPlans: OutputProjectLongTermPlans;

  futurePlansForm: FormGroup = this.formBuilder.group({
    ownership: ['', Validators.maxLength(5000)],
    durability: ['', Validators.maxLength(5000)],
    transferability: ['', Validators.maxLength(5000)]
  });

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private projectStore: ProjectStore,
              private projectDescriptionService: ProjectDescriptionService) {
    super();
  }

  ngOnInit(): void {
    this.formService.init(this.futurePlansForm, this.projectStore.projectEditable$);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.outputProjectLongTermPlans) {
      this.resetForm();
    }
  }

  onSubmit(): void {
    this.projectDescriptionService.updateProjectLongTermPlans(this.projectId, this.createOutputProjectLongTermPlans())
      .pipe(
        tap(saved => Log.info('Updated project long-term plans:', this, saved)),
        tap(() => this.formService.setSuccess('project.application.form.save.success')),
        catchError(error => this.formService.setError(error))
      ).subscribe();
  }

  createOutputProjectLongTermPlans(): OutputProjectLongTermPlans {
    return {
      projectOwnership: this.futurePlansForm.controls.ownership.value,
      projectDurability: this.futurePlansForm.controls.durability.value,
      projectTransferability: this.futurePlansForm.controls.transferability.value
    };
  }

  resetForm(): void {
    this.futurePlansForm.controls.ownership.setValue(this.outputProjectLongTermPlans?.projectOwnership);
    this.futurePlansForm.controls.durability.setValue(this.outputProjectLongTermPlans?.projectDurability);
    this.futurePlansForm.controls.transferability.setValue(this.outputProjectLongTermPlans?.projectTransferability);
  }

}
