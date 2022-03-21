import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {FormArray, FormBuilder, ValidatorFn, Validators} from '@angular/forms';
import {ProjectUserCollaboratorDTO} from '@cat/api';
import {of} from 'rxjs';
import {PrivilegesPageStore} from '@project/project-application/privileges-page/privileges-page-store.service';
import {FormService} from '@common/components/section/form/form.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {TranslateService} from '@ngx-translate/core';
import {catchError, tap} from 'rxjs/operators';
import {APIError} from '@common/models/APIError';
import { Alert } from '@common/components/forms/alert';

const atLeastOneManageUser = (): ValidatorFn => (formArray: FormArray) => {
  return formArray.controls.find(user => user.get('level')?.value === ProjectUserCollaboratorDTO.LevelEnum.MANAGE)
    ? null : {atLeastOneManageUser: true};
};

const uniqueEmails = (): ValidatorFn => (formArray: FormArray) => {
  const emails = formArray.controls.map(user => user.get('userEmail')?.value);
  return emails.length === (new Set(emails)).size ? null : {uniqueEmails: true};
};

@Component({
  selector: 'jems-application-form-privileges-expansion-panel',
  templateUrl: './application-form-privileges-expansion-panel.component.html',
  styleUrls: ['./application-form-privileges-expansion-panel.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ApplicationFormPrivilegesExpansionPanelComponent implements OnInit {
  @Input()
  projectCollaboratorsData: ProjectUserCollaboratorDTO[];

  @Input()
  projectTitle: string;

  PROJECT_LEVEL = ProjectUserCollaboratorDTO.LevelEnum;
  Alert = Alert;

  form = this.formBuilder.group({
    projectCollaborators: this.formBuilder.array([], [atLeastOneManageUser(), uniqueEmails()])
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
      if (this.projectCollaboratorsData) {
        this.formService.init(this.form, this.pageStore.projectCollaboratorsEditable$);
        this.resetForm(this.projectCollaboratorsData);
        this.formService.resetEditable();
      }
    }

  saveCollaborators(): void {
    this.pageStore.saveProjectCollaborators(this.projectCollaborators.value)
      .pipe(
        tap(() => this.formService.setSuccess('project.application.form.section.privileges.saved')),
        catchError(error => {
          const apiError = error.error as APIError;
          if (apiError?.formErrors) {
            Object.keys(apiError.formErrors).forEach(field => {
              const control = this.projectCollaborators.controls
                .find(collaborator => collaborator.get('userEmail')?.value === field)?.get('userEmail');
              control?.setErrors({error: this.translateService.instant(apiError.formErrors[field].i18nKey)});
              control?.markAsDirty();
            });
            this.changeDetectorRef.detectChanges();
          }
          return of(null);
        })

      ).subscribe();
  }

  resetForm(projectCollaborators: ProjectUserCollaboratorDTO[]): void {
    this.projectCollaborators.clear();
    projectCollaborators.forEach(projectCollaborator => this.addCollaborator(projectCollaborator));
  }

  get projectCollaborators(): FormArray {
    return this.form.get('projectCollaborators') as FormArray;
  }

  addCollaborator(projectCollaborator?: ProjectUserCollaboratorDTO): void {
    this.projectCollaborators.push(this.formBuilder.group({
      userEmail: [projectCollaborator?.userEmail, [Validators.required, Validators.maxLength(255)]],
      level: [projectCollaborator?.level || this.PROJECT_LEVEL.VIEW, Validators.required]
    }));
  }
}
