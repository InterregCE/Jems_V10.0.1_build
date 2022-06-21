import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {UserRoleDTO} from '@cat/api';
import {combineLatest, Observable, of} from 'rxjs';
import {catchError, map, take, tap} from 'rxjs/operators';
import {SystemPageSidenavService} from '../../services/system-page-sidenav.service';
import {RoutingService} from '@common/services/routing.service';
import {UserRoleDetailPageStore} from './user-role-detail-page-store.service';
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
  selector: 'jems-user-role-detail-page',
  templateUrl: './user-role-detail-page.component.html',
  styleUrls: ['./user-role-detail-page.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserRoleDetailPageComponent {

  treeControlCreateAndCollaborate = new FlatTreeControl<RolePermissionRow>(
    node => node.level, node => node.expandable);

  treeControlInspect = new FlatTreeControl<RolePermissionRow>(
    node => node.level, node => node.expandable);

  treeControlTopNavigation = new FlatTreeControl<RolePermissionRow>(
    node => node.level, node => node.expandable);

  PermissionState = PermissionState;
  PermissionMode = PermissionMode;
  roleId = this.activatedRoute?.snapshot?.params?.roleId;
  data$: Observable<{
    role: UserRoleDTO;
    isUpdateAllowed: boolean;
  }>;
  userRoleForm = this.formBuilder.group({
    name: ['', [
      Validators.required,
      Validators.maxLength(50),
      Validators.minLength(1),
    ]],
    defaultForRegisteredUser: [false, []],
    permissionsInspect: this.formBuilder.array([]),
    permissionsTopBar: this.formBuilder.array([]),
    permissionsCreateAndCollaborate: this.formBuilder.array([])
  });

  roleHasProjectCreate = false;
  roleHasProjectMonitor = false;

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
  dataSourceCreateProjects = new MatTreeFlatDataSource(this.treeControlCreateAndCollaborate, this.treeFlattener);
  dataSourceInspectProjects = new MatTreeFlatDataSource(this.treeControlInspect, this.treeFlattener);
  dataSourceTopNavigation = new MatTreeFlatDataSource(this.treeControlTopNavigation, this.treeFlattener);

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              private activatedRoute: ActivatedRoute,
              private router: RoutingService,
              private sidenavService: SystemPageSidenavService,
              private formService: FormService,
              private permissionService: PermissionService,
              public pageStore: UserRoleDetailPageStore) {
    this.formService.init(this.userRoleForm);

    this.data$ = combineLatest([
      this.pageStore.userRole$,
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

  get name(): FormControl {
    return this.userRoleForm.get('name') as FormControl;
  }

  get defaultForRegisteredUser(): FormControl {
    return this.userRoleForm.get('defaultForRegisteredUser') as FormControl;
  }

  get permissionsInspect(): FormArray {
    return this.userRoleForm.get('permissionsInspect') as FormArray;
  }

  get permissionsTopBar(): FormArray {
    return this.userRoleForm.get('permissionsTopBar') as FormArray;
  }

  get permissionsCreateAndCollaborate(): FormArray {
    return this.userRoleForm.get('permissionsCreateAndCollaborate') as FormArray;
  }

  save(role: UserRoleDTO): void {
    const user: UserRoleDTO = {
      id: role.id,
      name: this.userRoleForm.value.name,
      defaultForRegisteredUser: this.userRoleForm.value.defaultForRegisteredUser,
      permissions: this.getFormPermissions()
    };
    if (role?.id) {
      this.pageStore.saveUserRole(user)
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
    this.pageStore.createUserRole(user)
      .pipe(
        take(1),
        tap(() => this.router.navigate(['/app/system/role/'], redirectSuccessPayload)),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  discard(role: UserRoleDTO, shouldUpdateBePossible: boolean): void {
    if (role.id) {
      this.resetUserRole(role, shouldUpdateBePossible);
    } else {
      this.router.navigate(['/app/system/role']);
    }
  }

  resetUserRole(role: UserRoleDTO, isUpdateAllowed: boolean): void {
    this.name?.patchValue(role?.name);
    this.defaultForRegisteredUser?.patchValue(role?.defaultForRegisteredUser);
    this.roleHasProjectCreate = role.permissions ? role.permissions.filter((permission: any) => permission === PermissionsEnum.ProjectCreate).length > 0 : false;
    this.permissionsInspect.clear();
    this.permissionsTopBar.clear();
    this.permissionsCreateAndCollaborate.clear();

    const createAndCollaborateGroups = Permission.DEFAULT_USER_CREATE_AND_COLLABORATE_PERMISSIONS.map((perm, index) =>
      this.extractFormPermissionSubGroup(perm, role.permissions, index)
    );
    createAndCollaborateGroups.forEach((group: FormGroup) => this.permissionsCreateAndCollaborate.push(group));

    const inspectGroups = Permission.DEFAULT_USER_INSPECT_PERMISSIONS.map((perm, index) =>
      this.extractFormPermissionSubGroup(perm, role.permissions, index)
    );
    inspectGroups.forEach(group => this.permissionsInspect.push(group));
    this.roleHasProjectMonitor = this.hasMonitoringPrivileges();

    const groups = Permission.TOP_NAVIGATION_PERMISSIONS.map((perm, index) =>
      this.extractFormPermissionSubGroup(perm, role.permissions, index)
    );
    groups.forEach(group => this.permissionsTopBar.push(group));

    this.dataSourceCreateProjects.data = createAndCollaborateGroups;
    this.dataSourceInspectProjects.data = inspectGroups;
    this.dataSourceTopNavigation.data = groups;
    this.treeControlCreateAndCollaborate.expandAll();
    this.treeControlInspect.expandAll();
    this.treeControlTopNavigation.expandAll();
    if (this.roleId) {
      this.formService.resetEditable();
    } else {
      this.formService.setCreation(true);
    }

    this.adaptDependentPermissions();

    if (!isUpdateAllowed) {
      this.userRoleForm.disable();
    }
  }

  changeState(permission: AbstractControl, value: PermissionState): void {
    if (this.state(permission).value !== value) {
      this.state(permission)?.setValue(value);
      this.formChanged();
    }
  }

  changeStateOfToggle(permission: AbstractControl): void {
    if (this.state(permission)?.value === PermissionState.EDIT) {
      this.state(permission)?.setValue(PermissionState.HIDDEN);
    } else {
      this.state(permission)?.setValue(PermissionState.EDIT);
    }
    this.adaptDependentPermissions();
    this.formChanged();
  }

  hasChild = (_: number, node: RolePermissionRow) => node.expandable;

  subtree(control: AbstractControl): FormArray {
    return control.get('subtree') as FormArray;
  }

  state(control: AbstractControl): AbstractControl {
    return control.get('state') as AbstractControl;
  }

  hideTooltip(control: AbstractControl): AbstractControl {
    return control.get('hideTooltip') as AbstractControl;
  }

  viewTooltip(control: AbstractControl): AbstractControl {
    return control.get('viewTooltip') as AbstractControl;
  }

  icon(control: AbstractControl): AbstractControl {
    return control.get('icon') as AbstractControl;
  }

  editTooltip(control: AbstractControl): AbstractControl {
    return control.get('editTooltip') as AbstractControl;
  }

  infoMessage(control: AbstractControl): AbstractControl {
    return control.get('infoMessage') as AbstractControl;
  }

  formChanged(): void {
    this.formService.setDirty(true);
  }

  grantProjectCreate(): void {
    this.roleHasProjectCreate = !this.roleHasProjectCreate;

    if (this.roleHasProjectCreate) {
      this.permissionsTopBar.controls
        .filter(node => node.value.name === 'topbar.main.dashboard')
        .forEach(node => this.subtree(node).controls.forEach((child: AbstractControl) => this.changeState(child, PermissionState.VIEW)));
    }
    this.formChanged();
  }

  grantProjectMonitor(): void {
    this.roleHasProjectMonitor = !this.roleHasProjectMonitor;
    this.formChanged();
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
        disabled: perm.disabled,
        state: perm.state ? perm.state : this.getCurrentState(perm, currentRolePermissions),
        hideTooltip: perm.hideTooltip,
        viewTooltip: perm.viewTooltip,
        editTooltip: perm.editTooltip,
        infoMessage: perm.infoMessage,
        icon: perm.icon
      });
    } else {
      return this.formBuilder.group({
        name: perm.name,
        parentIndex,
        icon: perm.icon,
        subtree: this.formBuilder.array(
          perm.children.map(child => this.extractFormPermissionSubGroup(child, currentRolePermissions, parentIndex))
        ),
      });
    }
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

  private static getPermissionsForState(state: PermissionState, permissionNode: PermissionNode): PermissionsEnum[] {
    if (state === PermissionState.EDIT) {
      return (permissionNode.editPermissions || []).concat(permissionNode.viewPermissions || []);
    }
    if (state === PermissionState.VIEW) {
      return permissionNode.viewPermissions || [];
    }
    return [];
  }

  private getFormPermissions(): PermissionsEnum[] {
    const permissions: PermissionsEnum[] = [];

    if (!this.roleHasProjectMonitor) {
      this.permissionsInspect.clear();
    } else {
      Permission.DEFAULT_USER_INSPECT_PERMISSIONS.flatMap((perm: PermissionNode, index: number) =>
        this.extractChildrenPermissions(this.permissionsInspect.at(index), perm)).forEach(permission => permissions.push(permission));
    }

    if (this.roleHasProjectCreate) {
      permissions.push(PermissionsEnum.ProjectCreate);
      Permission.DEFAULT_USER_CREATE_AND_COLLABORATE_PERMISSIONS
        .flatMap((perm: PermissionNode, index: number) =>
          this.extractChildrenPermissions(this.permissionsCreateAndCollaborate.at(index), perm))
        .forEach(permission => permissions.push(permission));
    } else {
      this.permissionsCreateAndCollaborate.clear();
    }

    Permission.TOP_NAVIGATION_PERMISSIONS.flatMap((perm: PermissionNode, index: number) =>
      this.extractChildrenPermissions(this.permissionsTopBar.at(index), perm)).forEach(permission => permissions.push(permission));


    return permissions;
  }

  private extractChildrenPermissions(nodeForm: AbstractControl, permissionNode: PermissionNode): PermissionsEnum[] {
    if (!permissionNode.children?.length) {
      const state = this.state(nodeForm)?.value;
      return UserRoleDetailPageComponent.getPermissionsForState(state, permissionNode);
    }

    return permissionNode.children.flatMap((node: PermissionNode, index: number) =>
      this.extractChildrenPermissions(this.subtree(nodeForm).at(index), node));
  }

  private hasMonitoringPrivileges(): boolean {
    return Permission.DEFAULT_USER_INSPECT_PERMISSIONS.flatMap((perm: PermissionNode, index: number) =>
      this.hasAnyStateNotHidden(this.permissionsInspect.at(index), perm)).filter(isNotHidden => isNotHidden).length > 0;
  }

  private hasAnyStateNotHidden(nodeForm: AbstractControl, permissionNode: PermissionNode): boolean[] {
    if (!permissionNode.children?.length) {
      const state = this.state(nodeForm)?.value;
      return [state !== PermissionState.HIDDEN];
    }

    return permissionNode.children.flatMap((node: PermissionNode, index: number) =>
      this.hasAnyStateNotHidden(this.subtree(nodeForm).at(index), node));
  }

  private adaptDependentPermissions(): void {
    const instantiateGroup = this.treeControlInspect?.dataNodes
      ?.find(node => node.name === 'permission.assessment.instantiate') as any;
    const consolidateGroup = this.treeControlInspect?.dataNodes
      ?.find(node => node.name === 'permission.assessment.consolidate') as any;
    if (!consolidateGroup || !instantiateGroup) {
      return;
    }
    if (this.state(instantiateGroup.form)?.value === PermissionState.HIDDEN) {
      this.state(consolidateGroup.form)?.setValue(PermissionState.HIDDEN);
      consolidateGroup.disabled = true;
    } else {
      consolidateGroup.disabled = false;
    }
  }
}
