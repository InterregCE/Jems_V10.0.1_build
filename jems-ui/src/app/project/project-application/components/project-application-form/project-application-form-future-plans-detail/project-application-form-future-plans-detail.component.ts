import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input, OnChanges,
  OnInit,
  Output, SimpleChanges
} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {OutputProjectLongTermPlans, InputProjectLongTermPlans} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';
import {FormService} from '@common/components/section/form/form.service';
import {Observable} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {takeUntil, tap} from 'rxjs/operators';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';
import { APPLICATION_FORM } from '@project/application-form-model';

@Component({
  selector: 'app-project-application-form-future-plans-detail',
  templateUrl: './project-application-form-future-plans-detail.component.html',
  styleUrls: ['./project-application-form-future-plans-detail.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormFuturePlansDetailComponent extends BaseComponent implements OnInit, OnChanges {

  APPLICATION_FORM = APPLICATION_FORM;
  // TODO: remove these and adapt the component to save independently
  @Input()
  error$: Observable<HttpErrorResponse | null>;
  @Input()
  success$: Observable<any>;

  @Input()
  project: OutputProjectLongTermPlans;
  @Output()
  updateData = new EventEmitter<InputProjectLongTermPlans>();

  futurePlansForm: FormGroup = this.formBuilder.group({
    ownership: ['', Validators.maxLength(5000)],
    durability: ['', Validators.maxLength(5000)],
    transferability: ['', Validators.maxLength(5000)]
  });

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private projectStore: ProjectStore) {
    super();
  }

  ngOnInit(): void {
    this.formService.init(this.futurePlansForm, this.projectStore.projectEditable$);
    this.error$
      .pipe(
        takeUntil(this.destroyed$),
        tap(err => this.formService.setError(err))
      )
      .subscribe();
    this.success$
      .pipe(
        takeUntil(this.destroyed$),
        tap(() => this.formService.setSuccess('project.application.form.future.plans.save.success'))
      )
      .subscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.project) {
      this.resetForm();
    }
  }

  onSubmit(): void {
    this.updateData.emit({
      projectOwnership: this.futurePlansForm.controls.ownership.value,
      projectDurability: this.futurePlansForm.controls.durability.value,
      projectTransferability: this.futurePlansForm.controls.transferability.value
    });
  }

  resetForm(): void {
    this.futurePlansForm.controls.ownership.setValue(this.project?.projectOwnership);
    this.futurePlansForm.controls.durability.setValue(this.project?.projectDurability);
    this.futurePlansForm.controls.transferability.setValue(this.project?.projectTransferability);
  }

}
