import {Injectable} from '@angular/core';
import {OutputCurrentUser} from '@cat/api';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {filter, map} from 'rxjs/operators';
import {SecurityService} from './security.service';

@Injectable({providedIn: 'root'})
export class PermissionGuard implements CanActivate {

  constructor(private router: Router,
              private securityService: SecurityService) {
  }

  private checkUser(user: OutputCurrentUser | null, childRoute: ActivatedRouteSnapshot): boolean {
    let allowed = true;
    const permissionsOnly = childRoute?.data?.permissionsOnly;
    if (permissionsOnly) {
      allowed = permissionsOnly.some((only: string) => user?.role === only);
    }
    if (!allowed) {
      this.router.navigate(['app'])
    }
    return allowed;
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.securityService.currentUser
      .pipe(
        filter(user => !!user),
        map(user => this.checkUser(user, route))
      );
  }
}
