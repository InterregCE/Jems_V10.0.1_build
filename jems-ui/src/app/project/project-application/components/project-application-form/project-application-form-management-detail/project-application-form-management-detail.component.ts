import {
  ChangeDetectionStrategy,
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges
} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  InputProjectHorizontalPrinciples,
  InputProjectManagement,
  OutputProjectManagement, ProjectDescriptionService
} from '@cat/api';
import {SelectionModel} from '@angular/cdk/collections';
import {FormService} from '@common/components/section/form/form.service';
import {BaseComponent} from '@common/components/base-component';
import {catchError, tap} from 'rxjs/operators';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {Log} from '@common/utils/log';

@Component({
  selector: 'jems-project-application-form-management-detail',
  templateUrl: './project-application-form-management-detail.component.html',
  styleUrls: ['./project-application-form-management-detail.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormManagementDetailComponent extends BaseComponent implements OnInit, OnChanges {
  APPLICATION_FORM = APPLICATION_FORM;

  @Input()
  projectId: number;
  @Input()
  editable: boolean;
  @Input()
  projectManagement: OutputProjectManagement;

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
              private projectStore: ProjectStore,
              private projectDescriptionService: ProjectDescriptionService) {
    super();
  }

  ngOnInit(): void {
    this.formService.init(this.managementForm, this.projectStore.projectEditable$);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.projectManagement) {
      this.resetForm();
    }
  }

  onSubmit(): void {
    this.projectDescriptionService.updateProjectManagement(this.projectId, this.createInputProjectManagement())
      .pipe(
        tap(saved => Log.info('Updated project management:', this, saved)),
        tap(() => this.formService.setSuccess('project.application.form.save.success')),
        catchError(error => this.formService.setError(error))
      ).subscribe();
  }

  createInputProjectManagement(): InputProjectManagement {
    return {
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
    };
  }

  resetForm(): void {
    this.managementForm.controls.coordination.setValue(this.projectManagement?.projectCoordination);
    this.managementForm.controls.quality.setValue(this.projectManagement?.projectQualityAssurance);
    this.managementForm.controls.communication.setValue(this.projectManagement?.projectCommunication);
    this.managementForm.controls.financial.setValue(this.projectManagement?.projectFinancialManagement);

    this.managementForm.controls.criteria_development.setValue(this.projectManagement?.projectJointDevelopmentDescription);
    if (this.projectManagement?.projectCooperationCriteria?.projectJointDevelopment) {
      this.selection.select('criteria_development');
      this.enableSelection('criteria_development');
    } else {
      this.selection.deselect('criteria_development');
      this.managementForm.get('criteria_development')?.disable();
    }

    this.managementForm.controls.criteria_implementation.setValue(this.projectManagement?.projectJointImplementationDescription);
    if (this.projectManagement?.projectCooperationCriteria?.projectJointImplementation) {
      this.selection.select('criteria_implementation');
      this.enableSelection('criteria_implementation');
    } else {
      this.selection.deselect('criteria_implementation');
      this.managementForm.get('criteria_implementation')?.disable();
    }

    this.managementForm.controls.criteria_staffing.setValue(this.projectManagement?.projectJointStaffingDescription);
    if (this.projectManagement?.projectCooperationCriteria?.projectJointStaffing) {
      this.selection.select('criteria_staffing');
      this.enableSelection('criteria_staffing');
    } else {
      this.selection.deselect('criteria_staffing');
      this.managementForm.get('criteria_staffing')?.disable();
    }

    this.managementForm.controls.criteria_financing.setValue(this.projectManagement?.projectJointFinancingDescription);
    if (this.projectManagement?.projectCooperationCriteria?.projectJointFinancing) {
      this.selection.select('criteria_financing');
      this.enableSelection('criteria_financing');
    } else {
      this.selection.deselect('criteria_financing');
      this.managementForm.get('criteria_financing')?.disable();
    }

    this.selectedContributionPrincipleDevelopment = this.projectManagement?.projectHorizontalPrinciples?.sustainableDevelopmentCriteriaEffect;
    this.selectedContributionPrincipleOpportunities = this.projectManagement?.projectHorizontalPrinciples?.equalOpportunitiesEffect;
    this.selectedContributionPrincipleEquality = this.projectManagement?.projectHorizontalPrinciples?.sexualEqualityEffect;
    this.managementForm.controls.principles_sustainable.setValue(this.projectManagement?.sustainableDevelopmentDescription);
    this.enableSelection('principles_sustainable');
    this.managementForm.controls.principles_opportunities.setValue(this.projectManagement?.equalOpportunitiesDescription);
    this.enableSelection('principles_opportunities');
    this.managementForm.controls.principles_equality.setValue(this.projectManagement?.sexualEqualityDescription);
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

  disabled(input: string): boolean {
    return !this.editable || !this.selection.isSelected(input);
  }
}
