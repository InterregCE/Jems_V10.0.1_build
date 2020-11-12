import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input, OnChanges,
  OnInit,
  Output, SimpleChanges
} from '@angular/core';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {Permission} from '../../../../../security/permissions/permission';
import {FormState} from '@common/components/forms/form-state';
import {OutputProjectPartnerDetail, InputProjectPartnerContribution} from '@cat/api';

@Component({
  selector: 'app-project-application-form-partner-contribution',
  templateUrl: './project-application-form-partner-contribution.component.html',
  styleUrls: ['./project-application-form-partner-contribution.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerContributionComponent extends ViewEditForm implements OnInit, OnChanges {
  Permission = Permission;

  @Input()
  partner: OutputProjectPartnerDetail;
  @Input()
  editable: boolean;

  @Output()
  update = new EventEmitter<InputProjectPartnerContribution>();

  partnerContributionForm: FormGroup = this.formBuilder.group({
    organizationRelevance: ['', Validators.maxLength(2000)],
    organizationRole: ['', Validators.maxLength(2000)],
    organizationExperience: ['', Validators.maxLength(2000)],
  });

  organizationRelevanceErrors = {
    maxlength: 'partner.organization.relevance.textarea.size.too.long'
  };
  organizationRoleErrors = {
    maxlength: 'partner.organization.role.textarea.size.too.long'
  };
  organizationExperienceErrors = {
    maxlength: 'partner.organization.experience.textarea.size.too.long'
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef,
              private activatedRoute: ActivatedRoute,
              private sideNavService: SideNavService) {
    super(changeDetectorRef);
  }

  protected enterViewMode(): void {
    this.sideNavService.setAlertStatus(false);
    this.initFields();
  }

  protected enterEditMode(): void {
    this.sideNavService.setAlertStatus(true);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.enterViewMode();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.partner) {
      this.changeFormState$.next(FormState.VIEW);
    }
  }

  getForm(): FormGroup | null {
    return this.partnerContributionForm;
  }

  onSubmit(): void {
    this.update.emit({
      organizationRelevance: this.partnerContributionForm.controls.organizationRelevance.value,
      organizationRole: this.partnerContributionForm.controls.organizationRole.value,
      organizationExperience: this.partnerContributionForm.controls.organizationExperience.value,
    });
  }

  onCancel(): void {
    this.changeFormState$.next(FormState.VIEW);
  }

  private initFields(): void {
    this.partnerContributionForm.controls.organizationRelevance.setValue(this.partner?.partnerContribution?.organizationRelevance);
    this.partnerContributionForm.controls.organizationRole.setValue(this.partner?.partnerContribution?.organizationRole);
    this.partnerContributionForm.controls.organizationExperience.setValue(this.partner?.partnerContribution?.organizationExperience);
  }

}
