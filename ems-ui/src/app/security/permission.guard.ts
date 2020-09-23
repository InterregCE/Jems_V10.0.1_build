import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {filter, map} from 'rxjs/operators';
import {SecurityService} from './security.service';

@Injectable({providedIn: 'root'})
export class PermissionGuard implements CanActivate {

  constructor(private router: Router,
              private securityService: SecurityService) {
  }

  // TODO also handle permissionsExcept from routeData
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.securityService.currentUser
      .pipe(
        filter(user => !!user),
        map(user => {
            let allowed = true;
            const permissionsOnly = route?.data?.permissionsOnly;
            if (permissionsOnly) {
              allowed = permissionsOnly.some((only: string) => user?.role === only);
            }
            if (!allowed) {
              this.router.navigate(['/'])
            }
            return allowed;
          }
        )
      );
  }

}
