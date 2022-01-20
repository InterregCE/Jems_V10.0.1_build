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
import {InputProjectHorizontalPrinciples, InputProjectManagement, OutputProjectManagement} from '@cat/api';
import {SelectionModel} from '@angular/cdk/collections';
import {FormService} from '@common/components/section/form/form.service';
import {BaseComponent} from '@common/components/base-component';
import {Observable} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {takeUntil, tap} from 'rxjs/operators';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';
import { APPLICATION_FORM } from '@project/common/application-form-model';

@Component({
  selector: 'jems-project-application-form-management-detail',
  templateUrl: './project-application-form-management-detail.component.html',
  styleUrls: ['./project-application-form-management-detail.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormManagementDetailComponent extends BaseComponent implements OnInit, OnChanges {

  APPLICATION_FORM = APPLICATION_FORM;
  // TODO: remove these and adapt the component to save independently
  @Input()
  error$: Observable<HttpErrorResponse | null>;
  @Input()
  success$: Observable<any>;

  @Input()
  editable: boolean;
  @Input()
  project: OutputProjectManagement;
  @Output()
  updateData = new EventEmitter<InputProjectManagement>();

  selection = new SelectionModel<string>(true, []);
  selectedContributionPrincipleDevelopment = '';
  selectedContributionPrincipleOpportunities = '';
  selectedContributionPrincipleEquality = '';

  managementForm: FormGroup = this.formBuilder.group({
    coordination: ['', Validators.maxLength(5000)],
    quality: ['', Validators.maxLength(5000)],
    communication: ['', Validators.maxLength(5000)],
    financial: ['', Validators.maxLength(5000)],
    criteria_development: ['', Validators.maxLength(2000)],
    criteria_implementation: ['', Validators.maxLength(2000)],
    criteria_staffing: ['', Validators.maxLength(2000)],
    criteria_financing: ['', Validators.maxLength(2000)],
    principles_sustainable: ['', Validators.maxLength(2000)],
    principles_opportunities: ['', Validators.maxLength(2000)],
    principles_equality: ['', Validators.maxLength(2000)]
  });

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private projectStore: ProjectStore) {
    super();
  }

  ngOnInit(): void {
    this.formService.init(this.managementForm, this.projectStore.projectEditable$);
    this.error$
      .pipe(
        takeUntil(this.destroyed$),
        tap(err => this.formService.setError(err))
      )
      .subscribe();
    this.success$
      .pipe(
        takeUntil(this.destroyed$),
        tap(() => this.formService.setSuccess('project.application.form.management.save.success'))
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
      projectCoordination: this.managementForm.controls.coordination.value || [],
      projectQualityAssurance: this.managementForm.controls.quality.value || [],
      projectCommunication: this.managementForm.controls.communication.value || [],
      projectFinancialManagement: this.managementForm.controls.financial.value || [],
      projectCooperationCriteria: {
        projectJointDevelopment: this.selection.isSelected('criteria_development'),
        projectJointImplementation: this.selection.isSelected('criteria_implementation'),
        projectJointStaffing: this.selection.isSelected('criteria_staffing'),
        projectJointFinancing: this.selection.isSelected('criteria_financing'),
      },
      projectJointDevelopmentDescription: this.managementForm.controls.criteria_development.value || [],
      projectJointImplementationDescription: this.managementForm.controls.criteria_implementation.value || [],
      projectJointStaffingDescription: this.managementForm.controls.criteria_staffing.value || [],
      projectJointFinancingDescription: this.managementForm.controls.criteria_financing.value || [],
      projectHorizontalPrinciples: {
        sustainableDevelopmentCriteriaEffect: this.selectedContributionPrincipleDevelopment as InputProjectHorizontalPrinciples.SustainableDevelopmentCriteriaEffectEnum,
        equalOpportunitiesEffect: this.selectedContributionPrincipleOpportunities as InputProjectHorizontalPrinciples.EqualOpportunitiesEffectEnum,
        sexualEqualityEffect: this.selectedContributionPrincipleEquality as InputProjectHorizontalPrinciples.SexualEqualityEffectEnum,
      },
      sustainableDevelopmentDescription: this.managementForm.controls.principles_sustainable.value || [],
      equalOpportunitiesDescription: this.managementForm.controls.principles_opportunities.value || [],
      sexualEqualityDescription: this.managementForm.controls.principles_equality.value || []
    });
  }

  resetForm(): void {
    this.managementForm.controls.coordination.setValue(this.project?.projectCoordination);
    this.managementForm.controls.quality.setValue(this.project?.projectQualityAssurance);
    this.managementForm.controls.communication.setValue(this.project?.projectCommunication);
    this.managementForm.controls.financial.setValue(this.project?.projectFinancialManagement);

    this.managementForm.controls.criteria_development.setValue(this.project?.projectJointDevelopmentDescription);
    if (this.project?.projectCooperationCriteria?.projectJointDevelopment) {
      this.selection.select('criteria_development');
      this.enableSelection('criteria_development');
    } else {
      this.selection.deselect('criteria_development');
      this.managementForm.get('criteria_development')?.disable();
    }

    this.managementForm.controls.criteria_implementation.setValue(this.project?.projectJointImplementationDescription);
    if (this.project?.projectCooperationCriteria?.projectJointImplementation) {
      this.selection.select('criteria_implementation');
      this.enableSelection('criteria_implementation');
    } else {
      this.selection.deselect('criteria_implementation');
      this.managementForm.get('criteria_implementation')?.disable();
    }

    this.managementForm.controls.criteria_staffing.setValue(this.project?.projectJointStaffingDescription);
    if (this.project?.projectCooperationCriteria?.projectJointStaffing) {
      this.selection.select('criteria_staffing');
      this.enableSelection('criteria_staffing');
    } else {
      this.selection.deselect('criteria_staffing');
      this.managementForm.get('criteria_staffing')?.disable();
    }

    this.managementForm.controls.criteria_financing.setValue(this.project?.projectJointFinancingDescription);
    if (this.project?.projectCooperationCriteria?.projectJointFinancing) {
      this.selection.select('criteria_financing');
      this.enableSelection('criteria_financing');
    } else {
      this.selection.deselect('criteria_financing');
      this.managementForm.get('criteria_financing')?.disable();
    }

    this.selectedContributionPrincipleDevelopment = this.project?.projectHorizontalPrinciples?.sustainableDevelopmentCriteriaEffect;
    this.selectedContributionPrincipleOpportunities = this.project?.projectHorizontalPrinciples?.equalOpportunitiesEffect;
    this.selectedContributionPrincipleEquality = this.project?.projectHorizontalPrinciples?.sexualEqualityEffect;
    this.managementForm.controls.principles_sustainable.setValue(this.project?.sustainableDevelopmentDescription);
    this.enableSelection('principles_sustainable');
    this.managementForm.controls.principles_opportunities.setValue(this.project?.equalOpportunitiesDescription);
    this.enableSelection('principles_opportunities');
    this.managementForm.controls.principles_equality.setValue(this.project?.sexualEqualityDescription);
    this.enableSelection('principles_equality');
  }

  changeSelection(key: string): void {
    this.selection.toggle(key);
    this.managementForm.get(key)?.setValue(null);
    if (this.selection.isSelected(key)) {
      this.managementForm.get(key)?.enable();
    } else {
      this.managementForm.get(key)?.disable();
    }
    this.formChanged();
  }

  formChanged(): void {
    this.formService.setDirty(true);
  }

  enableSelection(key: string): void {
    if (this.editable) {
      this.managementForm.get(key)?.enable();
    } else {
      this.managementForm.get(key)?.disable();
    }
  }
}
