import {Injectable} from '@angular/core';
import {NgxPermissionsService} from 'ngx-permissions';
import {from, Observable, ReplaySubject} from 'rxjs';
import {SecurityService} from '../security.service';
import {OutputCurrentUser} from '@cat/api';
import {filter} from 'rxjs/operators';
import {Log} from '../../common/utils/log';

@Injectable({providedIn: 'root'})
export class PermissionService {

  private permissionsChanged$: ReplaySubject<string[]> = new ReplaySubject();

  constructor(private ngxPermissionsService: NgxPermissionsService,
              private securityService: SecurityService) {
    this.securityService.currentUser
      .pipe(
        filter((user: OutputCurrentUser) => !!user)
      )
      .subscribe((user: OutputCurrentUser) => this.setPermissions([user.role]))
  }

  setPermissions(permissions: string[]): void {
    Log.info('Setting new user permissions', this, permissions);
    this.ngxPermissionsService.flushPermissions();
    this.ngxPermissionsService.loadPermissions(permissions);
    this.permissionsChanged$.next(permissions);
  }

  hasPermission(permission: string): Observable<boolean> {
    return from(this.ngxPermissionsService.hasPermission(permission));
  }

  permissionsChanged(): Observable<string[]> {
    return this.permissionsChanged$.asObservable();
  }
}
