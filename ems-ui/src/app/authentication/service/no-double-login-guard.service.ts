import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable, of} from 'rxjs';
import {catchError, map, tap} from 'rxjs/operators';
import {AuthenticationService, OutputCurrentUser} from '@cat/api';

@Injectable()
export class NoDoubleLoginGuard implements CanActivate {

  constructor(private authenticationService: AuthenticationService,
              private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.authenticationService.getCurrentUser()
      .pipe(
        tap((cu: OutputCurrentUser) => {
          if (cu.id > 0)
            this.router.navigate(['app']);
        }),
        map(() => true),
        catchError(() => of(true)),
      );
  }

}
