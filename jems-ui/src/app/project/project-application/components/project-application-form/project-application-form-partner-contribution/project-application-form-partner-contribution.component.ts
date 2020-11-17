import {ChangeDetectionStrategy, Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {OutputProjectPartnerDetail} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectPartnerStore} from '../../../containers/project-application-form-page/services/project-partner-store.service';
import {catchError, take, tap} from 'rxjs/operators';

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
              private formService: FormService) {
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
    this.partnerStore.updatePartnerContribution(this.partnerContributionForm.value)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.partner.motivation.save.success')),
        catchError(error => this.formService.setError(error))
      ).subscribe();
  }

  resetForm(): void {
    if (!this.partner?.partnerContribution) {
      this.partnerContributionForm.reset();
      return;
    }
    this.partnerContributionForm.patchValue(this.partner?.partnerContribution);
  }

}
