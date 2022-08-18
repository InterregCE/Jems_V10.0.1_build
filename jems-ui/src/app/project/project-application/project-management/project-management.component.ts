import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ProjectContractingManagementDTO, ProjectContractingManagementService, UserRoleCreateDTO} from '@cat/api';
import {catchError, take, tap} from 'rxjs/operators';
import {combineLatest, Observable, of} from 'rxjs';
import {FormService} from '@common/components/section/form/form.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PermissionService} from '../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@UntilDestroy()
@Component({
  selector: 'jems-contract-management',
  templateUrl: './project-management.component.html',
  styleUrls: ['./project-management.component.scss'],
  providers: [FormService],
})
export class ProjectManagementComponent implements OnInit {

  projectManagers$: Observable<ProjectContractingManagementDTO[]>;
  projectManagersContacts: ProjectContractingManagementDTO[];
  projectId: number;

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
              private permissionService: PermissionService,) {
    this.projectId = this.activatedRoute.snapshot.params.projectId;
    this.projectManagers$ = managementService.getContractingManagement(this.projectId).pipe(
      tap(projectManagers => this.projectManagersContacts = projectManagers),
      tap(projectManagers => this.resetForm(projectManagers)),
      take(1)
    );
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
      catchError(error => this.formService.setError(error)),
      untilDestroyed(this)
    ).subscribe();
  }

  resetForm(projectManagers: ProjectContractingManagementDTO[]) {
    projectManagers.forEach(manager => {
      this.initManagerContactForm(this.projectManagersForm.get(manager.managementType) as FormGroup, manager);
    });

    combineLatest([this.projectStore.userIsProjectOwnerOrEditCollaborator$, this.hasEditPermissionForProjectManagement()]).pipe(
      tap(([userIsProjectOwnerOrEditCollaborator, hasEditPermission]) =>
        this.formService.setEditable(userIsProjectOwnerOrEditCollaborator || hasEditPermission)),
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
        Validators.pattern('^[0-9 +()/-]*$'),
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
}
