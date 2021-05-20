import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {OutputProjectPartnerDetail, ProjectPartnerMotivationDTO} from '@cat/api';
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
export class ProjectApplicationFormPartnerContributionComponent implements OnChanges {
  @Input()
  partner: OutputProjectPartnerDetail;

  partnerContributionForm = this.formBuilder.group({
    organizationRelevance: [],
    organizationRole: [],
    organizationExperience: [],
  });

  constructor(private formBuilder: FormBuilder,
              private partnerStore: ProjectPartnerStore,
              private formService: FormService) {
    this.formService.init(this.partnerContributionForm, this.partnerStore.isProjectEditable$);
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
    this.partnerContributionForm.get('organizationRelevance')?.setValue(this.partner?.motivation?.organizationRelevance || []);
    this.partnerContributionForm.get('organizationRole')?.setValue(this.partner?.motivation?.organizationRole || []);
    this.partnerContributionForm.get('organizationExperience')?.setValue(this.partner?.motivation?.organizationExperience || []);
  }

  get controls(): any {
    return this.partnerContributionForm.controls;
  }
}
