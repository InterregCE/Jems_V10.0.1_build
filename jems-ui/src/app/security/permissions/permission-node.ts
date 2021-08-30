import {UserRoleDTO} from '@cat/api';

export enum PermissionState {
  HIDDEN = 'HIDDEN',
  VIEW = 'VIEW',
  EDIT = 'EDIT'
}

export enum PermissionMode {
  HIDDEN_VIEW_EDIT = 'HIDDEN_VIEW_EDIT',
  HIDDEN_VIEW = 'HIDDEN_VIEW',
  VIEW_EDIT = 'VIEW_EDIT',
  TOGGLE_EDIT = 'TOGGLE_EDIT',
}

export class PermissionNode {
  name?: string;
  viewPermissions?: UserRoleDTO.PermissionsEnum[];
  editPermissions?: UserRoleDTO.PermissionsEnum[];
  mode?: PermissionMode = undefined;
  children?: PermissionNode[] = [];
  // this is just for demo purpose
  // TODO remove this when all permissions are used correctly and not just mocked
  // tslint:disable-next-line:no-inferrable-types
  disabled?: boolean = false;
  // state is only to be used when creating a fake view or if a permission will always have the same value (i.e is locked)
  state?: PermissionState;
  icon?: string;
  hideTooltip?: string;
  viewTooltip?: string;
  editTooltip?: string;
}
