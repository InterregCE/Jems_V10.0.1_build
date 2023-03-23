import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {PrivilegesPageStore} from '@project/project-application/privileges-page/privileges-page-store.service';
import {FormService} from '@common/components/section/form/form.service';
import {
  PartnerUserCollaboratorDTO,
  ProjectCallSettingsDTO,
  ProjectPartnerSummaryDTO,
  ProjectUserCollaboratorDTO
} from '@cat/api';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {FormArray, FormBuilder, Validators} from '@angular/forms';
import {TranslateService} from '@ngx-translate/core';
import {catchError, map, tap} from 'rxjs/operators';
import {APIError} from '@common/models/APIError';
import {combineLatest, Observable, of} from 'rxjs';
import {Alert} from '@common/components/forms/alert';
import {
  AFTER_APPROVED_STATUSES,
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {SecurityService} from '../../../../security/security.service';
import CallTypeEnum = ProjectCallSettingsDTO.CallTypeEnum;

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
  isAfterApproved$ = this.projectStore.projectStatus$.pipe(
    map(status => status.status),
    map(status => AFTER_APPROVED_STATUSES.includes(status))
  );
  isCallSpf$ = this.projectStore.projectCallType$.pipe(map((type) => type === CallTypeEnum.SPF));
  isProjectCollaboratorNonManage$: Observable<boolean>;

  partnerForm = this.formBuilder.group({
    partnerCollaborators: this.formBuilder.array([], [])
  });

  errorMessages = {
    atLeastOneManageUser: 'project.application.form.section.privileges.manage.user',
    uniqueEmails: 'project.application.form.section.privileges.unique.emails'
  };

  constructor(
    private pageStore: PrivilegesPageStore,
    public formService: FormService,
    private projectSidenavService: ProjectApplicationFormSidenavService,
    private formBuilder: FormBuilder,
    private translateService: TranslateService,
    private changeDetectorRef: ChangeDetectorRef,
    public projectStore: ProjectStore,
    public securityService: SecurityService
  ) {
    this.isProjectCollaboratorNonManage$ = this.isProjectCollaboratorNonManage();
  }

  ngOnInit(): void {
    this.formService.init(this.partnerForm, this.pageStore.projectCollaboratorsEditable$);
    this.resetPartnerForm(this.collaborators);
    this.formService.resetEditable();
  }

  isProjectCollaboratorNonManage(): Observable<boolean> {
    return combineLatest([
      this.pageStore.projectCollaborators$,
      this.securityService.currentUser
    ]).pipe(
        map(([projectCollaborators, currentUser]) =>
            projectCollaborators.find(collaborator => collaborator.userId === currentUser?.id
                && collaborator.level !== ProjectUserCollaboratorDTO.LevelEnum.MANAGE) != null
        )
    );
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
      level: [partnerCollaborator?.level || this.PARTNER_LEVEL.VIEW, Validators.required],
      gdpr: [partnerCollaborator?.gdpr]
    }));
  }

  getPartnerTranslationString(partner: ProjectPartnerSummaryDTO): string {
    return `${partner.sortNumber || ''} ${partner.abbreviation}`;
  }
}
