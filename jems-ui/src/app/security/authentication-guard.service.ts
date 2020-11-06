import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable, of} from 'rxjs';
import {catchError, map, tap} from 'rxjs/operators';
import {AuthenticationService, OutputCurrentUser} from '@cat/api';

@Injectable({providedIn: 'root'})
export class AuthenticationGuard implements CanActivate {

  constructor(private authenticationService: AuthenticationService,
              private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.authGuard(state.url);
  }

  private authGuard(url: string): Observable<boolean> {
    return this.authenticationService.getCurrentUser().pipe(
      tap((cu: OutputCurrentUser) => {
        if (!cu || cu.id === -1) {
          throw {};
        }
      }),
      map(() => true),
      catchError(() => {
        this.unauthorized(url);
        return of(false);
      }),
    );
  }

  private unauthorized(url: string): void {
    this.router.navigate(['no-auth', 'login'], {queryParams: {ref: url}});
  }

}
