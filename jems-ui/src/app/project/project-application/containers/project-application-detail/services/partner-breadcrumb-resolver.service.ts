import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable, of} from 'rxjs';
import {ProjectPartnerStore} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import {map} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';

@Injectable()
export class PartnerBreadcrumbResolver implements Resolve<Observable<string>> {

  constructor(private projectPartnerStore: ProjectPartnerStore, private translateService: TranslateService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Observable<string>> {
    return of(this.projectPartnerStore.partner$.pipe(map(
      partner => this.translateService.instant('common.label.project.partner.role.shortcut.' + partner.role, {partner: `${partner.sortNumber || ''} ${partner.abbreviation}`})
    )));
  }
}
