import {UserRoleDTO} from '@cat/api';

export enum PermissionState {
  HIDDEN = 'HIDDEN',
  VIEW = 'VIEW',
  EDIT = 'EDIT'
}

export enum PermissionMode {
  HIDDEN_VIEW_EDIT = 'HIDDEN_VIEW_EDIT',
  HIDDEN_VIEW = 'HIDDEN_VIEW',
  TOGGLE_EDIT = 'TOGGLE_EDIT',
}

export class PermissionNode {
  name?: string;
  state?: PermissionState = PermissionState.HIDDEN;
  viewPermissions?: UserRoleDTO.PermissionsEnum[];
  editPermissions?: UserRoleDTO.PermissionsEnum[];
  mode?: PermissionMode = undefined;
  children?: PermissionNode[] = [];
}
