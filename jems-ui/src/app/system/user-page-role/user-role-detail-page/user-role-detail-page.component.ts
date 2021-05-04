import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {take} from 'rxjs/internal/operators';
import {UserRoleDTO} from '@cat/api';
import {Observable} from 'rxjs';
import {catchError, tap} from 'rxjs/operators';
import {SystemPageSidenavService} from '../../services/system-page-sidenav.service';
import {RoutingService} from '../../../common/services/routing.service';
import {UserRoleStore} from './user-role-store.service';
import {ActivatedRoute} from '@angular/router';
import {PermissionNode, PermissionState} from '../../../security/permissions/permission-node';
import {FormService} from '@common/components/section/form/form.service';
import {Permission} from '../../../security/permissions/permission';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'app-user-role-detail-page',
  templateUrl: './user-role-detail-page.component.html',
  styleUrls: ['./user-role-detail-page.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserRoleDetailPageComponent {

  PermissionState = PermissionState;
  roleId = this.activatedRoute?.snapshot?.params?.roleId;

  role$: Observable<UserRoleDTO>;

  userRoleForm = this.formBuilder.group({
    name: ['', [
      Validators.required,
      Validators.maxLength(50),
      Validators.minLength(1),
    ]],
    permissions: this.formBuilder.array([])
  });

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              private activatedRoute: ActivatedRoute,
              private router: RoutingService,
              private sidenavService: SystemPageSidenavService,
              private formService: FormService,
              public roleStore: UserRoleStore) {
    this.formService.init(this.userRoleForm);
    this.formService.setCreation(!this.roleId);
    this.role$ = this.roleStore.userRole$
      .pipe(
        tap(role => this.resetUserRole(role)),
      );
  }

  save(role: UserRoleDTO): void {
    const user: UserRoleDTO = {
      id: role.id,
      ...this.userRoleForm.value,
      permissions: this.getFormPermissions()
    };
    if (role?.id) {
      this.roleStore.saveUserRole(user)
        .pipe(
          take(1),
          tap(() => this.formService.setSuccess('User role saved successfully')),
          catchError(err => this.formService.setError(err))
        ).subscribe();
      return;
    }
    const redirectSuccessPayload = {
      state: {success: {i18nKey: 'userRole.detail.save.success'}}
    };
    this.roleStore.createUserRole(user)
      .pipe(
        take(1),
        tap(() => this.router.navigate(['/app/system/userRole/'], redirectSuccessPayload)),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  discard(role: UserRoleDTO): void {
    if (role.id) {
      this.resetUserRole(role);
    } else {
      this.router.navigate(['/app/system/userRole']);
    }
  }

  private extractFormPermissionSubGroup(perm: PermissionNode, currentRolePermissions: PermissionsEnum[]): FormGroup {
    if (!perm.children?.length) {
      return this.formBuilder.group({
        name: perm.name,
        isOneClickToggle: !perm.oneClickToggle?.length,
        state: this.getCurrentState(perm, currentRolePermissions)
      });
    } else {
      return this.formBuilder.group({
        name: perm.name,
        subtree: this.formBuilder.array(
          perm.children.map(child => this.extractFormPermissionSubGroup(child, currentRolePermissions))
        ),
      });
    }
  }

  resetUserRole(role: UserRoleDTO): void {
    this.name?.patchValue(role?.name);
    this.permissions.clear();
    Permission.DEFAULT_PERMISSIONS.forEach(perm =>
      this.permissions.push(this.extractFormPermissionSubGroup(perm, role.permissions))
    );
  }

  get name(): FormControl {
    return this.userRoleForm.get('name') as FormControl;
  }

  get permissions(): FormArray {
    return this.userRoleForm.get('permissions') as FormArray;
  }

  changeState(permission: AbstractControl, value: PermissionState): void {
    permission.get('state')?.setValue(value);
    this.formService.setDirty(true);
  }

  changeStateOfToggle(permission: AbstractControl): void {
    if (permission.get('state')?.value === PermissionState.EDIT) {
      permission.get('state')?.setValue(PermissionState.HIDDEN);
    } else {
      permission.get('state')?.setValue(PermissionState.EDIT);
    }
    this.formService.setDirty(true);
  }

  private getCurrentState(defaultPermission: PermissionNode, perms: PermissionsEnum[]): PermissionState {
    if (defaultPermission.editPermissions?.some(perm => perms?.includes(perm))) {
      return PermissionState.EDIT;
    }
    if (defaultPermission.viewPermissions?.some(perm => perms?.includes(perm))) {
      return PermissionState.VIEW;
    }
    if (defaultPermission.oneClickToggle?.some(perm => perms?.includes(perm))) {
      return PermissionState.EDIT;
    }
    return PermissionState.HIDDEN;
  }

  private getPermissionsForState(state: PermissionState, permissionNode: PermissionNode): PermissionsEnum[] {
    if (state === PermissionState.EDIT) {
      return permissionNode.editPermissions || permissionNode.oneClickToggle || [];
    }
    if (state === PermissionState.VIEW) {
      return permissionNode.viewPermissions || [];
    }
    return [];
  }

  private getFormPermissions(): PermissionsEnum[] {
    return Permission.DEFAULT_PERMISSIONS
      .flatMap((perm, i) => {
        const permissionGroup = this.permissions.at(i);

        if (perm.children?.length) {
          return perm.children.flatMap((childPerm, childIndex) => {
            const stateChild = (permissionGroup.get('subtree') as FormArray).at(childIndex)?.get('state')?.value;
            return this.getPermissionsForState(stateChild, childPerm);
          });
        }

        const state = permissionGroup.get('state')?.value;
        return this.getPermissionsForState(state, perm);
      });
  }
}
