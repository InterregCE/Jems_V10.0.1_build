import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {map, tap} from 'rxjs/operators';
import {Permission} from './permissions/permission';
import {SecurityService} from './security.service';

@Injectable({providedIn: 'root'})
export class NavigationGuard implements CanActivate {

  constructor(private router: Router,
              private securityService: SecurityService) {
  }

  canActivate(): Observable<boolean> {
    return this.securityService.currentUser
      .pipe(
        tap(currentUser =>
          // redirect to home page depending on role type
          currentUser?.role === Permission.APPLICANT_USER
            ? this.router.navigate(['/calls'])
            : this.router.navigate(['/project'])
        ),
        map(() => true)
      );
  }

}
