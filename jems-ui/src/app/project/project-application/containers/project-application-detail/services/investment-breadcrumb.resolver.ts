import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable, of} from 'rxjs';
import {map, withLatestFrom} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {ProjectWorkPackageInvestmentDetailPageStore} from '@project/work-package/project-work-package-page/work-package-detail-page/project-work-package-investments-tab/project-work-package-investment-detail-page/project-work-package-Investment-detail-page-store.service';

@Injectable()
export class InvestmentBreadcrumbResolver implements Resolve<Observable<string>> {

  constructor(private investmentPageStore: ProjectWorkPackageInvestmentDetailPageStore, private translateService: TranslateService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Observable<string>> {
    return of(this.investmentPageStore.investment$.pipe(
      withLatestFrom(this.investmentPageStore.workPackageNumber$),
      map(([investment, workPackageNumber]) => `${this.translateService.instant('project.breadcrumb.workPackageInvestment.name')} ${workPackageNumber || ''}.${investment.investmentNumber || ''}`
      )));
  }
}

