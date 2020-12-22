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

  partnerContributionForm: FormGroup = this.formBuilder.group({
    organizationRelevance: [this.languageService.multiLanguageFormFieldDefaultValue()],
    organizationRole: [this.languageService.multiLanguageFormFieldDefaultValue()],
    organizationExperience: [this.languageService.multiLanguageFormFieldDefaultValue()],
  });

  constructor(private formBuilder: FormBuilder,
              private partnerStore: ProjectPartnerStore,
              private formService: FormService,
              public languageService: MultiLanguageInputService) {
  }

  ngOnInit(): void {
    this.formService.init(this.partnerContributionForm);
    this.resetForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.partner) {
      this.resetForm();
    }
  }

  onSubmit(): void {
    const partnerContribution = {
      organizationRelevance: this.controls.organizationRelevance.value,
      organizationRole: this.controls.organizationRole.value,
      organizationExperience: this.controls.organizationExperience.value,
    } as ProjectPartnerMotivationDTO;
    this.partnerStore.updatePartnerMotivation(partnerContribution)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.partner.motivation.save.success')),
        catchError(error => this.formService.setError(error))
      ).subscribe();
  }

  resetForm(): void {
    this.partnerContributionForm.controls.organizationRelevance.setValue(this.partner?.motivation?.organizationRelevance);
    this.partnerContributionForm.controls.organizationRole.setValue(this.partner?.motivation?.organizationRole);
    this.partnerContributionForm.controls.organizationExperience.setValue(this.partner?.motivation?.organizationExperience);
  }

  get controls(): any {
    return this.partnerContributionForm.controls;
  }

}
