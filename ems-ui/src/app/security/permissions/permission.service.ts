import {Injectable} from '@angular/core';
import {NgxPermissionsService} from 'ngx-permissions';
import {from, Observable, ReplaySubject} from 'rxjs';
import {SecurityService} from '../security.service';
import {OutputCurrentUser} from '@cat/api';
import {filter} from 'rxjs/operators';

@Injectable({providedIn: 'root'})
export class PermissionService {

  private permissionsChanged$: ReplaySubject<void> = new ReplaySubject();

  constructor(private ngxPermissionsService: NgxPermissionsService,
              private securityService: SecurityService) {
    this.securityService.currentUser
      .pipe(
        filter((user: OutputCurrentUser) => !!user)
      )
      .subscribe((user: OutputCurrentUser) => this.setPermissions([user.role]))
  }

  setPermissions(permissions: string[]): void {
    this.ngxPermissionsService.flushPermissions();
    this.ngxPermissionsService.loadPermissions(permissions)
    this.permissionsChanged$.next();
  }

  hasPermission(permission: string): Observable<boolean> {
    return from(this.ngxPermissionsService.hasPermission(permission));
  }

  permissionsChanged(): Observable<void> {
    return this.permissionsChanged$.asObservable();
  }
}
