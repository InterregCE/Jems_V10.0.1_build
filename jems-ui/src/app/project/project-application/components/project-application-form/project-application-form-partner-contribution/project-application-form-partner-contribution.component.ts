import {ChangeDetectionStrategy, Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {OutputProjectPartnerDetail, ProjectPartnerMotivationDTO} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectPartnerStore} from '../../../containers/project-application-form-page/services/project-partner-store.service';
import {catchError, take, tap} from 'rxjs/operators';
import {MultiLanguageInput} from '@common/components/forms/multi-language/multi-language-input';

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

  organizationRelevance: MultiLanguageInput;
  organizationRole: MultiLanguageInput;
  organizationExperience: MultiLanguageInput;

  partnerContributionForm: FormGroup;

  constructor(private formBuilder: FormBuilder,
              private partnerStore: ProjectPartnerStore,
              private formService: FormService) {
  }

  ngOnInit(): void {
    this.initForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.partner) {
      this.initForm();
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

  initForm(): void {
    this.partnerContributionForm = this.formBuilder.group({
      organizationRelevance: [this.partner?.motivation?.organizationRelevance || []],
      organizationRole: [this.partner?.motivation?.organizationRole || []],
      organizationExperience: [this.partner?.motivation?.organizationExperience || []],
    });
    this.formService.init(this.partnerContributionForm, this.partnerStore.isProjectEditable$);
  }

  get controls(): any {
    return this.partnerContributionForm.controls;
  }
}
