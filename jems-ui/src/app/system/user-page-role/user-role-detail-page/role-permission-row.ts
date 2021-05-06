import {PermissionMode} from '../../../security/permissions/permission-node';

export class RolePermissionRow {
  expandable: boolean;
  name: string;
  level: number;
  state: PermissionState;
  mode?: PermissionMode = undefined;
}
