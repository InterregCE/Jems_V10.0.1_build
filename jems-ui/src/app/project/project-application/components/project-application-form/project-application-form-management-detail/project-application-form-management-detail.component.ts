import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {FormState} from '@common/components/forms/form-state';
import {Permission} from '../../../../../security/permissions/permission';
import {OutputProjectManagement, InputProjectManagement, InputProjectHorizontalPrinciples} from '@cat/api'
import {SelectionModel} from '@angular/cdk/collections';

@Component({
  selector: 'app-project-application-form-management-detail',
  templateUrl: './project-application-form-management-detail.component.html',
  styleUrls: ['./project-application-form-management-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormManagementDetailComponent extends ViewEditForm implements OnInit {
  Permission = Permission;

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

  coordinationErrors = {
    maxlength: 'project.application.form.management.entered.text.size.too.long'
  };
  qualityErrors = {
    maxlength: 'project.application.form.management.entered.text.size.too.long'
  };
  communicationErrors = {
    maxlength: 'project.application.form.management.entered.text.size.too.long'
  };
  financialErrors = {
    maxlength: 'project.application.form.management.entered.text.size.too.long'
  };
  criteriaErrors = {
    maxlength: 'project.application.form.management.entered.text.size.too.long'
  };
  principleErrors = {
    maxlength: 'project.application.form.management.entered.text.size.too.long'
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef,
              private sideNavService: SideNavService) {
    super(changeDetectorRef);
  }

  ngOnInit() {
    super.ngOnInit();
    this.changeFormState$.next(FormState.VIEW);
  }

  getForm(): FormGroup | null {
    return this.managementForm;
  }

  protected enterViewMode(): void {
    this.sideNavService.setAlertStatus(false);
    this.initFields();
  }

  protected enterEditMode(): void {
    this.sideNavService.setAlertStatus(true);
  }

  onSubmit(): void {
    this.updateData.emit({
      projectCoordination: this.managementForm.controls.coordination.value,
      projectQualityAssurance: this.managementForm.controls.quality.value,
      projectCommunication: this.managementForm.controls.communication.value,
      projectFinancialManagement: this.managementForm.controls.financial.value,
      projectCooperationCriteria: {
        projectJointDevelopment: this.selection.isSelected('criteria_development'),
        projectJointDevelopmentDescription: this.managementForm.controls.criteria_development.value,
        projectJointImplementation: this.selection.isSelected('criteria_implementation'),
        projectJointImplementationDescription: this.managementForm.controls.criteria_implementation.value,
        projectJointStaffing: this.selection.isSelected('criteria_staffing'),
        projectJointStaffingDescription: this.managementForm.controls.criteria_staffing.value,
        projectJointFinancing: this.selection.isSelected('criteria_financing'),
        projectJointFinancingDescription: this.managementForm.controls.criteria_financing.value,
      },
      projectHorizontalPrinciples: {
        sustainableDevelopmentCriteriaEffect: this.selectedContributionPrincipleDevelopment as InputProjectHorizontalPrinciples.SustainableDevelopmentCriteriaEffectEnum,
        sustainableDevelopmentDescription: this.managementForm.controls.principles_sustainable.value,
        equalOpportunitiesEffect: this.selectedContributionPrincipleOpportunities as InputProjectHorizontalPrinciples.EqualOpportunitiesEffectEnum,
        equalOpportunitiesDescription: this.managementForm.controls.principles_opportunities.value,
        sexualEqualityEffect: this.selectedContributionPrincipleEquality as InputProjectHorizontalPrinciples.SexualEqualityEffectEnum,
        sexualEqualityDescription: this.managementForm.controls.principles_equality.value
      }
    });
  }

  private initFields() {
    this.managementForm.controls.coordination.setValue(this.project?.projectCoordination);
    this.managementForm.controls.quality.setValue(this.project?.projectQualityAssurance);
    this.managementForm.controls.communication.setValue(this.project?.projectCommunication);
    this.managementForm.controls.financial.setValue(this.project?.projectFinancialManagement);
    if (this.project?.projectCooperationCriteria?.projectJointDevelopment){
      this.selection.select('criteria_development');
      this.managementForm.controls.criteria_development.setValue(this.project?.projectCooperationCriteria?.projectJointDevelopmentDescription);
    }
    if (this.project?.projectCooperationCriteria?.projectJointImplementation){
      this.selection.select('criteria_implementation');
      this.managementForm.controls.criteria_implementation.setValue(this.project?.projectCooperationCriteria?.projectJointImplementationDescription);
    }
    if (this.project?.projectCooperationCriteria?.projectJointStaffing){
      this.selection.select('criteria_staffing');
      this.managementForm.controls.criteria_staffing.setValue(this.project?.projectCooperationCriteria?.projectJointStaffingDescription);
    }
    if (this.project?.projectCooperationCriteria?.projectJointFinancing){
      this.selection.select('criteria_financing');
      this.managementForm.controls.criteria_financing.setValue(this.project?.projectCooperationCriteria?.projectJointFinancingDescription);
    }
    this.selectedContributionPrincipleDevelopment = this.project?.projectHorizontalPrinciples?.sustainableDevelopmentCriteriaEffect;
    this.selectedContributionPrincipleOpportunities = this.project?.projectHorizontalPrinciples?.equalOpportunitiesEffect;
    this.selectedContributionPrincipleEquality = this.project?.projectHorizontalPrinciples?.sexualEqualityEffect;
    this.managementForm.controls.principles_sustainable.setValue(this.project?.projectHorizontalPrinciples?.sustainableDevelopmentDescription);
    this.managementForm.controls.principles_opportunities.setValue(this.project?.projectHorizontalPrinciples?.equalOpportunitiesDescription);
    this.managementForm.controls.principles_equality.setValue(this.project?.projectHorizontalPrinciples?.sexualEqualityDescription);
  }

  changeSelection(key: string){
    this.selection.toggle(key);
    this.managementForm.get(key)?.setValue(null);
  }
}
