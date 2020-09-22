import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {map, take} from 'rxjs/operators';
import {SecurityService} from '../../security/security.service';

@Injectable()
export class CurrentUserResolver implements Resolve<string | number | undefined> {

  constructor(private securityService: SecurityService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<string | number | undefined> {
    if (!this.securityService.hasBeenInitialized) {
      return this.securityService.reloadCurrentUser()
        .pipe(
          map(currentUser => currentUser?.id),
          take(1)
          );
    } else {
      return this.securityService
        .currentUser
        .pipe(
          map(currentUser => currentUser?.id),
          take(1),
        )
    }
  }
}
