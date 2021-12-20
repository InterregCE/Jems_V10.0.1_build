import {ChangeDetectionStrategy, Component} from '@angular/core';
import { FormService } from '@common/components/section/form/form.service';
import {PrivilegesPageStore} from '@project/project-application/privileges-page/privileges-page-store.service';
import {combineLatest, Observable, of} from 'rxjs';
import {ProjectUserCollaboratorDTO} from '@cat/api';
import {ProjectApplicationFormSidenavService} from '../containers/project-application-form-page/services/project-application-form-sidenav.service';
import {catchError, map, tap} from 'rxjs/operators';
import {FormArray, FormBuilder, ValidatorFn, Validators} from '@angular/forms';
import {APIError} from '@common/models/APIError';
import {TranslateService} from '@ngx-translate/core';
import {Alert} from '@common/components/forms/alert';

const atLeastOneManageUser = (): ValidatorFn => (formArray: FormArray) => {
  return formArray.controls.find(user => user.get('level')?.value === ProjectUserCollaboratorDTO.LevelEnum.MANAGE)
    ? null : {atLeastOneManageUser: true};
};

const uniqueEmails = (): ValidatorFn => (formArray: FormArray) => {
  const emails = formArray.controls.map(user => user.get('userEmail')?.value);
  return emails.length === (new Set(emails)).size ? null : {uniqueEmails: true};
};

@Component({
  selector: 'app-privileges-page',
  templateUrl: './privileges-page.component.html',
  styleUrls: ['./privileges-page.component.scss'],
  providers: [PrivilegesPageStore, FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PrivilegesPageComponent {
  LEVEL = ProjectUserCollaboratorDTO.LevelEnum;
  Alert = Alert;

  data$: Observable<{
    projectTitle: string;
    projectCollaborators: ProjectUserCollaboratorDTO[];
  }>;

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
              private translateService: TranslateService) {
    this.data$ = combineLatest([this.pageStore.projectTitle$, this.pageStore.projectCollaborators$])
      .pipe(
        map(([projectTitle, projectCollaborators]) => ({
          projectTitle,
          projectCollaborators
        })),
        tap(data => this.resetForm(data.projectCollaborators)),
        tap(() => this.formService.resetEditable())
      );
    this.formService.init(this.form, this.pageStore.projectCollaboratorsEditable$);
  }

  saveCollaborators(): void {
    this.pageStore.saveProjectCollaborators(this.projectCollaborators.value)
      .pipe(
        tap(() => this.formService.setSuccess('project.application.form.section.privileges.saved')),
        catchError(error => this.formService.setError(error)),
        catchError(error => {
            const apiError = error.error as APIError;
            if (apiError?.formErrors) {
              Object.keys(apiError.formErrors).forEach(field => {
                const control = this.projectCollaborators.controls
                  .find(collaborator => collaborator.get('userEmail')?.value === field)?.get('userEmail');
                control?.setErrors({error: this.translateService.instant(apiError.formErrors[field].i18nKey)});
                control?.markAsDirty();
              });
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
      level: [projectCollaborator?.level || this.LEVEL.VIEW, Validators.required]
    }));
  }
}
