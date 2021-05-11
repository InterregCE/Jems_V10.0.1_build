import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {take} from 'rxjs/internal/operators';
import {UserRoleDTO} from '@cat/api';
import {combineLatest, Observable, of} from 'rxjs';
import {catchError, map, tap} from 'rxjs/operators';
import {SystemPageSidenavService} from '../../services/system-page-sidenav.service';
import {RoutingService} from '../../../common/services/routing.service';
import {UserRoleStore} from './user-role-store.service';
import {ActivatedRoute} from '@angular/router';
import {PermissionMode, PermissionNode, PermissionState} from '../../../security/permissions/permission-node';
import {FormService} from '@common/components/section/form/form.service';
import {Permission} from '../../../security/permissions/permission';
import {FlatTreeControl} from '@angular/cdk/tree';
import {MatTreeFlatDataSource, MatTreeFlattener} from '@angular/material/tree';
import {RolePermissionRow} from './role-permission-row';
import {PermissionService} from '../../../security/permissions/permission.service';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'app-user-role-detail-page',
  templateUrl: './user-role-detail-page.component.html',
  styleUrls: ['./user-role-detail-page.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserRoleDetailPageComponent {

  treeControl = new FlatTreeControl<RolePermissionRow>(
    node => node.level, node => node.expandable);

  private treeFlattener = new MatTreeFlattener<AbstractControl, RolePermissionRow>(
    (form: AbstractControl, level: number) => ({
      expandable: !!this.subtree(form),
      name: form.get('name')?.value,
      // TODO remove 'disabled' when all permissions are used correctly and not just mocked
      disabled: !!form.get('disabled')?.value,
      form,
      level,
      parentIndex: form.get('parentIndex')?.value,
      state: !!this.subtree(form) ? null : this.state(form)?.value,
      mode: form.get('mode')?.value || null,
    }),
    node => node.level,
    node => node.expandable,
    form => this.subtree(form).controls || []
  );

  dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);

  PermissionState = PermissionState;
  PermissionMode = PermissionMode;
  roleId = this.activatedRoute?.snapshot?.params?.roleId;

  data$: Observable<{
    role: UserRoleDTO,
    isUpdateAllowed: boolean,
  }>;

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
              private permissionService: PermissionService,
              public roleStore: UserRoleStore) {
    this.formService.init(this.userRoleForm);

    this.data$ = combineLatest([
      this.roleStore.userRole$,
      of(!this.roleId),
      this.permissionService.hasPermission(PermissionsEnum.RoleUpdate),
    ]).pipe(
      map(([role, isCreation, canUserUpdate]) => ({
        role,
        isUpdateAllowed: canUserUpdate || isCreation,
      })),
      tap(data => this.resetUserRole(data.role, data.isUpdateAllowed)),
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

  discard(role: UserRoleDTO, shouldUpdateBePossible: boolean): void {
    if (role.id) {
      this.resetUserRole(role, shouldUpdateBePossible);
    } else {
      this.router.navigate(['/app/system/userRole']);
    }
  }

  private extractFormPermissionSubGroup(perm: PermissionNode,
                                        currentRolePermissions: PermissionsEnum[],
                                        parentIndex: number): FormGroup {
    if (!perm.children?.length) {
      return this.formBuilder.group({
        name: perm.name,
        parentIndex,
        mode: perm.mode,
        // TODO remove 'disabled' when all permissions are used correctly and not just mocked
        disabled: perm.temporarilyDisabled,
        state: this.getCurrentState(perm, currentRolePermissions)
      });
    } else {
      return this.formBuilder.group({
        name: perm.name,
        parentIndex,
        subtree: this.formBuilder.array(
          perm.children.map(child => this.extractFormPermissionSubGroup(child, currentRolePermissions, parentIndex))
        ),
      });
    }
  }

  resetUserRole(role: UserRoleDTO, isUpdateAllowed: boolean): void {
    this.name?.patchValue(role?.name);
    this.permissions.clear();
    const groups = Permission.DEFAULT_PERMISSIONS.map((perm, index) =>
      this.extractFormPermissionSubGroup(perm, role.permissions, index)
    );
    groups.forEach(group => this.permissions.push(group));

    this.dataSource.data = groups;
    this.treeControl.expandAll();
    if (this.roleId) {
      this.formService.resetEditable();
    } else {
      this.formService.setCreation(true);
    }

    if (!isUpdateAllowed) {
      this.userRoleForm.disable();
    }
  }


  changeState(permission: AbstractControl, value: PermissionState): void {
    this.state(permission)?.setValue(value);
    this.formService.setDirty(true);
  }

  changeStateOfToggle(permission: AbstractControl): void {
    if (this.state(permission)?.value === PermissionState.EDIT) {
      this.state(permission)?.setValue(PermissionState.HIDDEN);
    } else {
      this.state(permission)?.setValue(PermissionState.EDIT);
    }
    this.formService.setDirty(true);
  }

  private getCurrentState(defaultPermission: PermissionNode, perms: PermissionsEnum[]): PermissionState {
    if (defaultPermission.editPermissions?.every(perm => perms?.includes(perm))) {
      return PermissionState.EDIT;
    }
    if (defaultPermission.viewPermissions?.every(perm => perms?.includes(perm))) {
      return PermissionState.VIEW;
    }
    return PermissionState.HIDDEN;
  }

  private getPermissionsForState(state: PermissionState, permissionNode: PermissionNode): PermissionsEnum[] {
    if (state === PermissionState.EDIT) {
      return (permissionNode.editPermissions || []).concat(permissionNode.viewPermissions || []);
    }
    if (state === PermissionState.VIEW) {
      return permissionNode.viewPermissions || [];
    }
    return [];
  }

  private getFormPermissions(): PermissionsEnum[] {
    return Permission.DEFAULT_PERMISSIONS.flatMap((perm: PermissionNode, index: number) =>
      this.extractChildrenPermissions(this.permissions.at(index), perm));
  }

  private extractChildrenPermissions(nodeForm: AbstractControl, permissionNode: PermissionNode): PermissionsEnum[] {
    if (!permissionNode.children?.length) {
      const state = this.state(nodeForm)?.value;
      return this.getPermissionsForState(state, permissionNode);
    }

    return permissionNode.children.flatMap((node: PermissionNode, index: number) =>
      this.extractChildrenPermissions(this.subtree(nodeForm).at(index), node));
  }

  hasChild = (_: number, node: RolePermissionRow) => node.expandable;

  get name(): FormControl {
    return this.userRoleForm.get('name') as FormControl;
  }

  get permissions(): FormArray {
    return this.userRoleForm.get('permissions') as FormArray;
  }

  subtree(control: AbstractControl): FormArray {
    return control.get('subtree') as FormArray;
  }

  state(control: AbstractControl): AbstractControl {
    return control.get('state') as AbstractControl;
  }
}
