import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {ProjectPartnerDetailDTO} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectPartnerStore} from '../../../containers/project-application-form-page/services/project-partner-store.service';
import {catchError, filter, take, tap} from 'rxjs/operators';
import { APPLICATION_FORM } from '@project/common/application-form-model';
import {Observable} from 'rxjs';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {RoutingService} from '@common/services/routing.service';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import {ActivatedRoute} from '@angular/router';

@UntilDestroy()
@Component({
  selector: 'jems-project-application-form-partner-contribution',
  templateUrl: './project-application-form-partner-contribution.component.html',
  styleUrls: ['./project-application-form-partner-contribution.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerContributionComponent {
  APPLICATION_FORM = APPLICATION_FORM;

  partner$: Observable<ProjectPartnerDetailDTO>;

  partnerContributionForm = this.formBuilder.group({
    organizationRelevance: [],
    organizationRole: [],
    organizationExperience: [],
  });

  constructor(private formBuilder: FormBuilder,
              private partnerStore: ProjectPartnerStore,
              private formService: FormService,
              private activatedRoute: ActivatedRoute,
              private router: RoutingService,
              private visibilityStatusService: FormVisibilityStatusService
              ) {
    visibilityStatusService.isVisible$((APPLICATION_FORM.SECTION_B.MOTIVATION)).pipe(
      untilDestroyed(this),
      filter(isVisible => !isVisible),
      tap(() => this.router.navigate(['../identity'], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'})),
    ).subscribe();
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

  resetForm(partner: ProjectPartnerDetailDTO): void {
    this.partnerContributionForm.patchValue(partner?.motivation || {
      organizationRelevance: [],
      organizationRole: [],
      organizationExperience: [],
    });
  }

  get controls(): any {
    return this.partnerContributionForm.controls;
  }
}
