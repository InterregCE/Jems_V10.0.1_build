import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {SecurityService} from '../../security/security.service';
import {filter} from 'rxjs/internal/operators';
import {map, tap} from 'rxjs/operators';
import {Permission} from '../../security/permissions/permission';

@Injectable({providedIn: 'root'})
export class AuditPageGuard implements CanActivate {

    constructor(private router: Router,
                private securityService: SecurityService) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
        return this.securityService.currentUser
            .pipe(
                filter(user => !!user),
                tap(user => {
                    if (user?.role === Permission.APPLICANT_USER) {
                        this.router.navigate(['app', 'dashboard']);
                    }
                }),
                map(() => true)
            );
    }
}
