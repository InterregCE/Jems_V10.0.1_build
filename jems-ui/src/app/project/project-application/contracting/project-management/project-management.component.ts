import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ProjectContractingManagementDTO, ProjectContractingManagementService, UserRoleCreateDTO} from '@cat/api';
import {catchError, filter, switchMap, take, tap} from 'rxjs/operators';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {FormService} from '@common/components/section/form/form.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import { PermissionService } from 'src/app/security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {ContractingSectionLockStore} from '@project/project-application/contracting/contracting-section-lock.store';
import {ContractingSection} from '@project/project-application/contracting/contracting-section';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {APIError} from '@common/models/APIError';
import { Alert } from '@common/components/forms/alert';
import {TranslateService} from '@ngx-translate/core';

@UntilDestroy()
@Component({
  selector: 'jems-contract-management',
  templateUrl: './project-management.component.html',
  styleUrls: ['./project-management.component.scss'],
  providers: [FormService],
})
export class ProjectManagementComponent implements OnInit {

  isSectionLocked$: Observable<boolean>;
  lockedSubject$ = new BehaviorSubject<boolean>(true);
  projectManagers$: Observable<ProjectContractingManagementDTO[]>;
  projectManagersContacts: ProjectContractingManagementDTO[];
  projectId: number;

  Alert = Alert;
  error$ = new BehaviorSubject<APIError | null>(null);

  projectManagersForm: FormGroup = this.formBuilder.group({
    ProjectManager: this.formBuilder.group(this.getContactControlsConfig()),
    FinanceManager: this.formBuilder.group(this.getContactControlsConfig()),
    CommunicationManager: this.formBuilder.group(this.getContactControlsConfig())
  });

  managerContactEmailErrors = {
    email: 'project.contact.email.wrong.format'
  };
  managerContactTelephoneErrors = {
    pattern: 'project.contact.telephone.wrong.format'
  };

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private activatedRoute: ActivatedRoute,
              private managementService: ProjectContractingManagementService,
              private projectStore: ProjectStore,
              private permissionService: PermissionService,
              private contractingSectionLockStore: ContractingSectionLockStore,
              private dialog: MatDialog,
              private translateService: TranslateService) {
    this.projectId = this.activatedRoute.snapshot.params.projectId;
    this.projectManagers$ = managementService.getContractingManagement(this.projectId).pipe(
      tap(projectManagers => this.projectManagersContacts = projectManagers),
      tap(projectManagers => this.resetForm(projectManagers)),
      take(1)
    );

    this.contractingSectionLockStore.lockedSections$.pipe(
        tap(lockedSections => this.lockedSubject$.next(lockedSections.includes(ContractingSection.ProjectManagers.toString()))),
        untilDestroyed(this)
    ).subscribe();

    this.isSectionLocked$ = this.lockedSubject$.asObservable();
  }

  ngOnInit(): void {
    this.formService.init(this.projectManagersForm, of(true));
  }

  onSubmit(): void {
    const projectManager = this.getManagerContactFormValues(ProjectContractingManagementDTO.ManagementTypeEnum.ProjectManager);
    const financeManager = this.getManagerContactFormValues(ProjectContractingManagementDTO.ManagementTypeEnum.FinanceManager);
    const communicationManager = this.getManagerContactFormValues(ProjectContractingManagementDTO.ManagementTypeEnum.CommunicationManager);
    this.managementService.updateContractingManagement(this.projectId, [projectManager, financeManager, communicationManager]).pipe(
      take(1),
      tap(() => this.formService.setSuccess('project.application.contract.management.contacts.save.success')),
      tap(projectManagers => this.projectManagers$ = of(projectManagers)),
      catchError(error => this.formService.setError(error)),
      untilDestroyed(this)
    ).subscribe();
  }

  resetForm(projectManagers: ProjectContractingManagementDTO[]) {
    projectManagers.forEach(manager => {
      this.initManagerContactForm(this.projectManagersForm.get(manager.managementType) as FormGroup, manager);
    });

    combineLatest([
        this.projectStore.userIsEditOrManageCollaborator$,
        this.hasEditPermissionForProjectManagement(),
        this.isSectionLocked$
    ]).pipe(
      tap(([userIsProjectOwnerOrEditCollaborator, hasEditPermission, isSectionLocked]) =>
        this.formService.setEditable((userIsProjectOwnerOrEditCollaborator || hasEditPermission) && !isSectionLocked)),
      untilDestroyed(this)
    ).subscribe();
  }

  lock(event: any) {
    const sectionNameKey = `project.application.contract.section.name.${ContractingSection.ProjectManagers.toString()}`;
    Forms.confirm(
        this.dialog,
        {
          title: 'project.application.contract.section.lock.dialog.header',
          message: {
            i18nKey: 'project.application.contract.section.lock.dialog.message',
            i18nArguments: {name: this.translateService.instant(sectionNameKey)}
          }
        }).pipe(
        filter(confirm => confirm),
        switchMap(() => this.contractingSectionLockStore.lockSection(ContractingSection.ProjectManagers)),
        tap(locked => this.lockedSubject$.next(true)),
        catchError((error) => this.showErrorMessage(error.error)),
        untilDestroyed(this)
    ).subscribe();
  }

  unlock(event: any) {
    const sectionNameKey = `project.application.contract.section.name.${ContractingSection.ProjectManagers.toString()}`;
    Forms.confirm(
        this.dialog,
        {
          title: 'project.application.contract.section.unlock.dialog.header',
          message: {
            i18nKey: 'project.application.contract.section.unlock.dialog.message',
            i18nArguments: {name: this.translateService.instant(sectionNameKey)}
          }
        }).pipe(
        filter(confirm => confirm),
        switchMap(() => this.contractingSectionLockStore.unlockSection(ContractingSection.ProjectManagers)),
        tap(locked => this.lockedSubject$.next(true)),
        catchError((error) => this.showErrorMessage(error.error)),
        untilDestroyed(this)
    ).subscribe();
  }

  private getContactControlsConfig() {
    return {
      title: ['', Validators.maxLength(25)],
      firstName: ['', Validators.maxLength(50)],
      lastName: ['', Validators.maxLength(50)],
      email: ['', Validators.compose([
        Validators.email,
        Validators.maxLength(255)
      ])],
      telephone: ['', Validators.compose([
        Validators.pattern('^([\s]+[0-9+()/]+)|([0-9+()/]+)[ 0-9+()/-]*$'),
        Validators.maxLength(25)
      ])]
    };
  }

  private getManagerContactFormValues(managementType: ProjectContractingManagementDTO.ManagementTypeEnum): ProjectContractingManagementDTO {
    return {
      projectId: this.projectId,
      managementType,
      title: this.projectManagersForm.get(managementType)?.value.title,
      firstName: this.projectManagersForm.get(managementType)?.value.firstName,
      lastName: this.projectManagersForm.get(managementType)?.value.lastName,
      email: this.projectManagersForm.get(managementType)?.value.email,
      telephone: this.projectManagersForm.get(managementType)?.value.telephone,
    } as ProjectContractingManagementDTO;
  }

  private initManagerContactForm(managerFormGroup: FormGroup, manager: ProjectContractingManagementDTO) {
    managerFormGroup.controls.title.setValue(manager.title);
    managerFormGroup.controls.firstName.setValue(manager.firstName);
    managerFormGroup.controls.lastName.setValue(manager.lastName);
    managerFormGroup.controls.email.setValue(manager.email);
    managerFormGroup.controls.telephone.setValue(manager.telephone);
  }

  private hasEditPermissionForProjectManagement(): Observable<boolean> {
    return this.permissionService.hasPermission(PermissionsEnum.ProjectContractingManagementEdit);
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      this.error$.next(null);
    }, 30000);
    return of(null);
  }
}
