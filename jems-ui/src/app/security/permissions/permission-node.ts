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
  temporarilyDisabled?: boolean = false;
}
