import {Injectable} from '@angular/core';
import {OutputCurrentUser} from '@cat/api';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {combineLatest, Observable} from 'rxjs';
import {filter, map, take, tap} from 'rxjs/operators';
import {SecurityService} from './security.service';
import {Log} from '../common/utils/log';
import {
  PartnerControlReportStore
} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';

@Injectable({providedIn: 'root'})
export class ControlReportGuard implements CanActivate {

  constructor(private router: Router,
              private securityService: SecurityService,
              private partnerControlReportStore: PartnerControlReportStore) {
  }

  private checkUser(user: OutputCurrentUser | null, childRoute: ActivatedRouteSnapshot): boolean {
    let allowed = true;
    combineLatest([
      this.partnerControlReportStore.controlReportEditable$,
      this.partnerControlReportStore.fullControlReportView$
    ])
      .pipe(
        take(1),
        map(([canEdit, canFullView]) => allowed = canEdit || canFullView),
        tap(() => {
          if (!allowed) {
            Log.info(`Current user role cannot access this route. Route:`, this, user?.role);
            this.router.navigate(['app']);
          }
        })
      )
      .subscribe();
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
