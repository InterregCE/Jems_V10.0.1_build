import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {PrivilegesPageStore} from '@project/project-application/privileges-page/privileges-page-store.service';
import {FormService} from '@common/components/section/form/form.service';
import {PartnerUserCollaboratorDTO, ProjectPartnerSummaryDTO} from '@cat/api';
import {ProjectApplicationFormSidenavService} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {FormArray, FormBuilder, Validators} from '@angular/forms';
import {TranslateService} from '@ngx-translate/core';
import {catchError, tap} from 'rxjs/operators';
import {APIError} from '@common/models/APIError';
import {of} from 'rxjs';
import {Alert} from '@common/components/forms/alert';

@Component({
  selector: 'jems-partner-team-privileges-expansion-panel',
  templateUrl: './partner-team-privileges-expansion-panel.component.html',
  styleUrls: ['./partner-team-privileges-expansion-panel.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerTeamPrivilegesExpansionPanelComponent implements OnInit {
  @Input()
  partner: ProjectPartnerSummaryDTO;
  @Input()
  collaborators: PartnerUserCollaboratorDTO[];

  PARTNER_LEVEL = PartnerUserCollaboratorDTO.LevelEnum;
  Alert = Alert;

  partnerForm = this.formBuilder.group({
    partnerCollaborators: this.formBuilder.array([], [])
  });

  errorMessages = {
    atLeastOneManageUser: 'project.application.form.section.privileges.manage.user',
    uniqueEmails: 'project.application.form.section.privileges.unique.emails'
  };

  constructor(private pageStore: PrivilegesPageStore,
              public formService: FormService,
              private projectSidenavService: ProjectApplicationFormSidenavService,
              private formBuilder: FormBuilder,
              private translateService: TranslateService,
              private changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.formService.init(this.partnerForm, this.pageStore.projectCollaboratorsEditable$);
    this.resetPartnerForm(this.collaborators);
    this.formService.resetEditable();
  }

  savePartnerCollaborators(partnerId: number): void {
    this.pageStore.saveProjectPartnerCollaborators(partnerId, this.partnerCollaborators.value)
      .pipe(
        tap(() => this.formService.setSuccess('project.application.form.section.privileges.saved')),
        catchError(error => {
          const apiError = error.error as APIError;
          if (apiError?.formErrors) {
            Object.keys(apiError.formErrors).forEach(field => {
              const control = this.partnerCollaborators.controls
                .find(collaborator => collaborator.get('userEmail')?.value.toLowerCase() === field)?.get('userEmail');
              control?.setErrors({error: this.translateService.instant(apiError.formErrors[field].i18nKey)});
              control?.markAsDirty();
            });
            this.changeDetectorRef.detectChanges();
          }
          return of(null);
        })).subscribe();
  }
  resetPartnerForm(partnerCollaborators: PartnerUserCollaboratorDTO[]): void {
    this.partnerCollaborators.clear();
    partnerCollaborators.forEach(partnerCollaborator => this.addPartnerCollaborator(partnerCollaborator));
    if (this.partnerCollaborators.length === 0) {
      this.addPartnerCollaborator();
    }
  }

  get partnerCollaborators(): FormArray {
    return this.partnerForm.get('partnerCollaborators') as FormArray;
  }

  addPartnerCollaborator(partnerCollaborator?: PartnerUserCollaboratorDTO): void {
    this.partnerCollaborators.push(this.formBuilder.group({
      userEmail: [partnerCollaborator?.userEmail, [Validators.required, Validators.pattern(/(?!^\s+$)^.*$/m), Validators.maxLength(255)]],
      level: [partnerCollaborator?.level || this.PARTNER_LEVEL.VIEW, Validators.required]
    }));
  }

  getPartnerTranslationString(partner: ProjectPartnerSummaryDTO): string {
    return `${partner.sortNumber || ''} ${partner.abbreviation}`;
  }
}
