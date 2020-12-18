import {ChangeDetectionStrategy, Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {OutputProjectPartnerDetail, ProjectPartnerMotivationDTO} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectPartnerStore} from '../../../containers/project-application-form-page/services/project-partner-store.service';
import {catchError, take, tap} from 'rxjs/operators';
import {MultiLanguageInput} from '@common/components/forms/multi-language/multi-language-input';
import {MultiLanguageInputService} from '../../../../../common/services/multi-language-input.service';

@Component({
  selector: 'app-project-application-form-partner-contribution',
  templateUrl: './project-application-form-partner-contribution.component.html',
  styleUrls: ['./project-application-form-partner-contribution.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerContributionComponent implements OnInit, OnChanges {
  @Input()
  partner: OutputProjectPartnerDetail;
  @Input()
  editable: boolean;

  organizationRelevance: MultiLanguageInput;
  organizationRole: MultiLanguageInput
  organizationExperience: MultiLanguageInput;

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
              private partnerStore: ProjectPartnerStore,
              private formService: FormService,
              public languageService: MultiLanguageInputService) {
  }

  ngOnInit(): void {
    this.formService.init(this.partnerContributionForm);
    this.formService.setAdditionalValidators([this.formValid.bind(this)]);
    this.resetForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.partner) {
      this.resetForm();
    }
  }

  onSubmit(): void {
    const partnerContribution = {
      organizationRelevance: this.organizationRelevance.inputs,
      organizationRole: this.organizationRole.inputs,
      organizationExperience: this.organizationExperience.inputs,
    } as ProjectPartnerMotivationDTO;
    this.partnerStore.updatePartnerMotivation(partnerContribution)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.partner.motivation.save.success')),
        catchError(error => this.formService.setError(error))
      ).subscribe();
  }

  resetForm(): void {
    if (!this.partner?.motivation) {
      this.partnerContributionForm.reset();
      return;
    }
    this.organizationRelevance = this.languageService.initInput(this.partner?.motivation.organizationRelevance, this.partnerContributionForm.controls.organizationRelevance);
    this.organizationRole = this.languageService.initInput(this.partner?.motivation.organizationRole, this.partnerContributionForm.controls.organizationRole);
    this.organizationExperience = this.languageService.initInput(this.partner?.motivation.organizationExperience, this.partnerContributionForm.controls.organizationExperience);
  }

  private formValid(): boolean {
    return this.organizationRelevance.isValid()
      && this.organizationRole.isValid()
      && this.organizationExperience.isValid();
  }

}
