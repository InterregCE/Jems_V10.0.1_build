import {UserRoleDTO} from '@cat/api';

export enum PermissionState {
  HIDDEN = 'HIDDEN',
  VIEW = 'VIEW',
  EDIT = 'EDIT'
}

export class PermissionNode {
  name?: string;
  state?: PermissionState = PermissionState.HIDDEN;
  viewPermissions?: UserRoleDTO.PermissionsEnum[];
  editPermissions?: UserRoleDTO.PermissionsEnum[];
}
