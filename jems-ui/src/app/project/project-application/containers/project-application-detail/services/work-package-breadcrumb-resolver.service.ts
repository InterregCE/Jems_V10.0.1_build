import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable, of} from 'rxjs';
import {map} from 'rxjs/operators';
import {WorkPackagePageStore} from '@project/work-package/project-work-package-page/work-package-detail-page/work-package-page-store.service';
import {TranslateService} from '@ngx-translate/core';

@Injectable()
export class WorkPackageBreadcrumbResolver implements Resolve<Observable<number>> {

  constructor(private workPackagePageStore: WorkPackagePageStore, private translateService: TranslateService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Observable<number>> {
    return of(this.workPackagePageStore.workPackage$.pipe(map(
      workPackage => this.translateService.instant('common.label.workpackage.shortcut', {workpackage: `${workPackage.number}`})
    )));
  }
}
