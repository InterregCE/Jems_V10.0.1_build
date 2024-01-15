import {Injectable} from '@angular/core';
import {UserDTO, UserRoleCreateDTO} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {SecurityService} from '../../security/security.service';
import {map} from 'rxjs/operators';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;
import {PermissionService} from '../../security/permissions/permission.service';
import {Permission} from '../../security/permissions/permission';

@Injectable()
export class DashboardPageStore {

  currentUser$: Observable<UserDTO | null>;
  canViewPartnerReports$: Observable<boolean>;
  canViewProjectReports$: Observable<boolean>;

  constructor(private securityService: SecurityService,
              private permissionService: PermissionService) {
    this.currentUser$ = this.securityService.currentUserDetails;
    this.canViewPartnerReports$ = this.canViewPartnerReports();
    this.canViewProjectReports$ = this.canViewProjectReports();
  }

  canViewPartnerReports(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.PartnerReportsRetrieve),
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportingView),
      this.securityService.currentUser
    ])
      .pipe(
        map(([canRetrievePartnerReports, canViewReportingForMonitoring, currentUser]) => {
          if (canRetrievePartnerReports) {
            const isMonitoringUser = this.getArraysIntersection(currentUser?.role.permissions, Permission.MONITORING_PERMISSIONS).length > 0;
            return isMonitoringUser ? canViewReportingForMonitoring : true;
          }
          return false;
        })
      );
  }

  canViewProjectReports(): Observable<boolean> {
    return combineLatest([
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportsRetrieve),
      this.permissionService.hasPermission(PermissionsEnum.ProjectReportingView),
      this.securityService.currentUser
    ])
      .pipe(
        map(([canRetrievePartnerReports, canViewReportingForMonitoring, currentUser]) => {
          if (canRetrievePartnerReports) {
            const isMonitoringUser = this.getArraysIntersection(currentUser?.role.permissions, Permission.MONITORING_PERMISSIONS).length > 0;
            return isMonitoringUser ? canViewReportingForMonitoring : true;
          }
          return false;
        })
      );
  }

  private getArraysIntersection(a1: PermissionsEnum[] | undefined, a2: PermissionsEnum[]): PermissionsEnum[] {
    return a1 ? a1.filter(n => a2.indexOf(n) !== -1) : [];
  }
}
