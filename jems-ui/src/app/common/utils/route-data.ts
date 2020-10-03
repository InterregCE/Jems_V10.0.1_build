import {Permission} from '../../security/permissions/permission';
import {ReplaySubject} from 'rxjs';

/**
 * Defines route details:
 * - breadcrumb: i18nKey or identifier used in BreadcrumbComponent as label
 * - breadcrumb: a subject that can be used by custom breadcrumb providers to adapt the label
 * - permissionsOnly: defines which user permissions are allowed to access the page - handled in PermissionGuard
 * - permissionsExcept: defines which user permissions are not allowed to access the page - to be handled in PermissionGuard
 */
export class RouteData {
  breadcrumb?: string;
  breadcrumb$?: ReplaySubject<string>;
  permissionsOnly?: Permission[];
  permissionsExcept?: Permission[];

  constructor(data: Partial<RouteData>) {
    this.breadcrumb = data.breadcrumb;
    this.breadcrumb$ = data.breadcrumb$;
    this.permissionsOnly = data.permissionsOnly;
    this.permissionsExcept = data.permissionsExcept;
  }
}
