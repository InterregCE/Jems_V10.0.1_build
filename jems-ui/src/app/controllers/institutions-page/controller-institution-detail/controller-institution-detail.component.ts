import {AfterContentChecked, ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {
  ControllerInstitutionDTO,
  ControllerInstitutionsApiService,
  ControllerInstitutionUserDTO,
  NutsImportService,
  UserRoleCreateDTO,
  UserRoleDTO
} from '@cat/api';
import {InstitutionsPageStore} from '../institutions-page-store.service';
import {ControllersPageSidenavService} from '../../controllers-page-sidenav.service';
import {FormArray, FormBuilder, ValidatorFn, Validators} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {catchError, map, take, tap} from 'rxjs/operators';
import {ActivatedRoute, Router} from '@angular/router';
import {BaseComponent} from '@common/components/base-component';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Observable, Subject} from 'rxjs';
import {ControllerInstitutionDetailConstants} from './controller-institution-detail.constants';
import {PermissionService} from '../../../security/permissions/permission.service';
import Permissions = UserRoleDTO.PermissionsEnum;
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;


const uniqueEmails = (): ValidatorFn => (formArray: FormArray) => {
  const emails = formArray.controls.map(user => user.get('userEmail')?.value);
  return emails.length === (new Set(emails)).size ? null : {uniqueEmails: true};
};

@UntilDestroy()
@Component({
  selector: 'jems-controller-institutions-detail',
  templateUrl: './controller-institution-detail.component.html',
  styleUrls: ['./controller-institution-detail.component.scss'],
  providers: [InstitutionsPageStore, FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ControllerInstitutionDetailComponent extends BaseComponent implements OnInit, AfterContentChecked{
  ACCESS_LEVEL = ControllerInstitutionUserDTO.AccessLevelEnum;

  constants = ControllerInstitutionDetailConstants;
  data$: Observable<{
    controllerData: ControllerInstitutionDTO;
  }>;
  isDataLoaded = false;

  Permissions = Permissions;
  controllerForm = this.formBuilder.group({
    id: '',
    name: ['', [Validators.required, Validators.pattern(/(?!^\s+$)^.*$/m)]],
    description: ['', Validators.maxLength(2000)],
    institutionNuts: [],
    institutionUsers: this.formBuilder.array([], uniqueEmails()),
    createdAt: ''
  });
  isEdit: boolean;
  discardNutsChanges$ = new Subject<boolean>();
  institutionId: number = this.activatedRoute?.snapshot?.params.controllerInstitutionId;


  constructor(private controllersPageSidenav: ControllersPageSidenavService,
              private formBuilder: FormBuilder,
              private nutsService: NutsImportService,
              private controllerService: ControllerInstitutionsApiService,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              public formService: FormService,
              private controllerInstitutionStore: InstitutionsPageStore,
              private permissionService: PermissionService) {
    super();
  }

  ngOnInit() {
    this.data$ = this.controllerInstitutionStore.controllerInstitutionDetail$.pipe(
      map(data => ({controllerData: data})),
      tap(data => this.isEdit = !!data.controllerData.id),
      tap(data => this.resetForm(data.controllerData))
    );
  }

  ngAfterContentChecked(): void {
    this.disableNotEmptyUserEmailInput(this.controllerForm.get('institutionUsers') as FormArray);
  }

  resetForm(controllerData: ControllerInstitutionDTO) {
    this.controllerForm.patchValue(controllerData);
    this.discardNutsChanges$.next();

    this.institutionUsers.clear();
    controllerData.institutionUsers?.forEach((user) => this.addUser(user));

    this.formService.init(this.controllerForm);
    this.formService.setCreation(!this.isEdit);
    this.formService.resetEditable();
    this.hasEditPermission().pipe(
      tap(hasPermission => this.formService.setEditable(hasPermission)),
      untilDestroyed(this)
    ).subscribe();
  }

  saveForm() {
    if (this.isEdit) {
      this.controllerInstitutionStore.updateController(this.institutionId, this.controllerForm.getRawValue())
        .pipe(
          take(1),
          tap(() => this.formService.setDirty(false)),
          tap(data => this.resetForm(data)),
          tap(() => this.formService.setSuccess('controller.institution.update.success')),
          catchError(error => this.formService.setError(error)),
          untilDestroyed(this)
        ).subscribe();
    } else {
      this.controllerInstitutionStore.createController(this.controllerForm.value)
        .pipe(
          take(1),
          tap(() => this.formService.setSuccess('controller.institution.save.success')),
          tap(created => this.redirectToInstitutionDetail(created)),
          catchError(error => this.formService.setError(error)),
          untilDestroyed(this)
        ).subscribe();
    }
  }

  private redirectToInstitutionDetail(institution: any): void {
    this.router.navigate(
      ['..', institution.id],
      {relativeTo: this.activatedRoute}
    );
  }

  get institutionUsers(): FormArray {
    return this.controllerForm.get('institutionUsers') as FormArray;
  }

  get institutionNuts(): FormArray {
    return this.controllerForm.get('institutionNuts') as FormArray;
  }

  deleteUser(index: number) {
    this.institutionUsers.removeAt(index);
    this.formService.setDirty(true);
  }

  addUser(user?: ControllerInstitutionUserDTO) {
    const userEntry = this.formBuilder.group({
      userId: user?.userId,
      institutionId: user?.institutionId,
      userEmail: this.formBuilder.control(user?.userEmail, Validators.compose([Validators.required, Validators.email])),
      accessLevel:  this.formBuilder.control( user?.accessLevel || this.ACCESS_LEVEL.View),
    });
    this.institutionUsers.push(userEntry);
  }

  updateSelectedRegions(selected: any) {
    this.institutionNuts.setValue(selected);
    if (this.isDataLoaded) {
      this.formService.setDirty(true);
    } else {
      this.isDataLoaded = true;
    }
  }

  private hasEditPermission(): Observable<boolean> {
    return this.permissionService.hasPermission(PermissionsEnum.InstitutionsUpdate) ||
      this.permissionService.hasPermission(PermissionsEnum.InstitutionsUnlimited);
  }

  private disableNotEmptyUserEmailInput(institutionUsersFormGroup: FormArray) {
    institutionUsersFormGroup.controls.filter(control =>
      !!control.get('userEmail')?.value &&
      control.get('userEmail')?.status == 'VALID' &&
      control.get('userId')?.value != null)
      .forEach(control => control.get('userEmail')?.disable());
  }
}
