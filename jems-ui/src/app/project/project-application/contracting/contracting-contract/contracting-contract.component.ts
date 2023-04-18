import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {ActivatedRoute} from '@angular/router';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {ContractInfoUpdateDTO, ProjectContractInfoDTO, ProjectContractsService, UserRoleCreateDTO} from '@cat/api';
import {catchError, filter, map, switchMap, take, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Alert} from '@common/components/forms/alert';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {ContractingSection} from '@project/project-application/contracting/contracting-section';
import {ContractingSectionLockStore} from '@project/project-application/contracting/contracting-section-lock.store';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {APIError} from '@common/models/APIError';
import {TranslateService} from '@ngx-translate/core';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@UntilDestroy()
@Component({
  selector: 'jems-contracting-contract',
  templateUrl: './contracting-contract.component.html',
  styleUrls: ['./contracting-contract.component.scss'],
  providers: [FormService]
})
export class ContractingContractComponent implements OnInit {

  isLocked$: Observable<boolean>;
  lockedSubject$ = new BehaviorSubject<boolean>(false);

  Alert = Alert;

  error$ = new BehaviorSubject<APIError | null>(null);

  contractInfoForm: FormGroup = this.formBuilder.group({
      website: ['', Validators.maxLength(250)],
      partnershipAgreementDate: ['']
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
    private contractingSectionLockStore: ContractingSectionLockStore,
    private dialog: MatDialog,
    private translateService: TranslateService,
  ) {
    this.projectId = this.activatedRoute.snapshot.params.projectId;
    this.contractingSectionLockStore.lockedSections$.pipe(
      tap(lockedSections => this.lockedSubject$.next(lockedSections.includes(ContractingSection.ContractsAgreements.toString()))),
      untilDestroyed(this)
    ).subscribe();

    this.isLocked$ = this.lockedSubject$.asObservable();
    this.canEdit$ = this.canEdit();
    this.projectContractInfo$ = contractInfoService.getProjectContractInfo(this.projectId).pipe(
      tap(data => this.resetForm(data))
    );
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

    combineLatest([
      this.canEdit$,
      this.isLocked$
    ]).pipe(
      tap(([canEdit, isLocked]) => this.formService.setEditable(canEdit && !isLocked)),
      untilDestroyed(this)
    ).subscribe();
  }

  canEdit(): Observable<boolean> {
    return combineLatest([
      this.projectStore.userIsEditOrManageCollaborator$,
      this.permissionService.hasPermission(PermissionsEnum.ProjectContractsEdit),
    ]).pipe(
      map(([userIsProjectOwnerOrEditCollaborator, hasEditPermission]) =>
        userIsProjectOwnerOrEditCollaborator || hasEditPermission
      )
    );
  }

  lock(event: any) {
    const sectionNameKey = `project.application.contract.section.name.${ContractingSection.ContractsAgreements.toString()}`;
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
      switchMap(() => this.contractingSectionLockStore.lockSection(ContractingSection.ContractsAgreements)),
      tap(locked => this.lockedSubject$.next(true)),
      catchError((error) => this.showErrorMessage(error.error)),
      untilDestroyed(this)
    ).subscribe();

  }

  unlock(event: any) {
    const sectionNameKey = `project.application.contract.section.name.${ContractingSection.ContractsAgreements.toString()}`;
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
      switchMap(() => this.contractingSectionLockStore.unlockSection(ContractingSection.ContractsAgreements)),
      tap(locked => this.lockedSubject$.next(true)),
      catchError((error) => this.showErrorMessage(error.error)),
      untilDestroyed(this)
    ).subscribe();

  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      this.error$.next(null);
    }, 30000);
    return of(null);
  }
}
