import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {OutputProjectPartnerDetail} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectPartnerStore} from '../../../containers/project-application-form-page/services/project-partner-store.service';
import {catchError, take, tap} from 'rxjs/operators';
import { APPLICATION_FORM } from '@project/common/application-form-model';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-project-application-form-partner-contribution',
  templateUrl: './project-application-form-partner-contribution.component.html',
  styleUrls: ['./project-application-form-partner-contribution.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerContributionComponent {
  APPLICATION_FORM = APPLICATION_FORM;

  partner$: Observable<OutputProjectPartnerDetail>;

  partnerContributionForm = this.formBuilder.group({
    organizationRelevance: [],
    organizationRole: [],
    organizationExperience: [],
  });

  constructor(private formBuilder: FormBuilder,
              private partnerStore: ProjectPartnerStore,
              private formService: FormService) {
    this.formService.init(this.partnerContributionForm, this.partnerStore.isProjectEditable$);
    this.partner$ = this.partnerStore.partner$
      .pipe(
        tap(partner => this.resetForm(partner))
      );
  }

  onSubmit(): void {
    this.partnerStore.updatePartnerMotivation(this.partnerContributionForm.value)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.partner.motivation.save.success')),
        catchError(error => this.formService.setError(error))
      ).subscribe();
  }

  resetForm(partner: OutputProjectPartnerDetail): void {
    this.partnerContributionForm.patchValue(partner?.motivation || {});
  }

  get controls(): any {
    return this.partnerContributionForm.controls;
  }
}
