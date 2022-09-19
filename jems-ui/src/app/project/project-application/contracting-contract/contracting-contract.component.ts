import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {ActivatedRoute} from '@angular/router';
import {combineLatest, Observable, of} from 'rxjs';
import {ContractInfoUpdateDTO, ProjectContractInfoDTO, ProjectContractsService, UserRoleCreateDTO} from '@cat/api';
import {catchError, map, take, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Alert} from '@common/components/forms/alert';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PermissionService} from '../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@UntilDestroy()
@Component({
  selector: 'jems-contracting-contract',
  templateUrl: './contracting-contract.component.html',
  styleUrls: ['./contracting-contract.component.scss'],
  providers: [FormService]
})
export class ContractingContractComponent implements OnInit {

  Alert = Alert;

  contractInfoForm: FormGroup = this.formBuilder.group({
      website: ['', Validators.maxLength(250)],
      partnershipAgreementDate:['']
    }
  );

  canEdit$: Observable<boolean>;
  projectContractInfo$: Observable<ProjectContractInfoDTO>;
  projectId: number;

  constructor(
    private contractInfoService: ProjectContractsService,
    private formBuilder: FormBuilder,
    private formService: FormService,
    private activatedRoute: ActivatedRoute,
    private projectStore: ProjectStore,
    private permissionService: PermissionService,
  ) {
    this.projectId = this.activatedRoute.snapshot.params.projectId;
    this.projectContractInfo$ = contractInfoService.getProjectContractInfo(this.projectId).pipe(
      tap(data => this.resetForm(data))
    );
    this.canEdit$ = this.canEdit();
  }

  ngOnInit(): void {
    this.formService.init(this.contractInfoForm, of(true));
  }

  onSubmit(): void {
    const contractInfoUpdateDTO: ContractInfoUpdateDTO = {
      website: this.contractInfoForm.get('website')?.value,
      partnershipAgreementDate: this.contractInfoForm.get('partnershipAgreementDate')?.value
    };

    this.contractInfoService.updateProjectContractInfo(this.projectId, contractInfoUpdateDTO).pipe(
      take(1),
      tap(() => this.formService.setSuccess('project.application.contract.contracts.save.success')),
      catchError(error => this.formService.setError(error)),
      untilDestroyed(this)
    ).subscribe();
  }

  resetForm(projectManagers: ProjectContractInfoDTO) {
    this.contractInfoForm.controls.website.setValue(projectManagers.website);
    this.contractInfoForm.controls.partnershipAgreementDate.setValue(projectManagers.partnershipAgreementDate);

    this.canEdit$.pipe(
      tap(canEdit => this.formService.setEditable(canEdit)),
      untilDestroyed(this)
    ).subscribe();
  }

  canEdit(): Observable<boolean>{
    return combineLatest([
      this.projectStore.userIsProjectOwnerOrEditCollaborator$,
      this.permissionService.hasPermission(PermissionsEnum.ProjectContractsEdit)]).pipe(
      map(([userIsProjectOwnerOrEditCollaborator, hasEditPermission]) =>
          userIsProjectOwnerOrEditCollaborator || hasEditPermission,
        untilDestroyed(this)
      ));
  }


}
