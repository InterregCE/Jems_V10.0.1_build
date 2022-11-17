import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable, of} from 'rxjs';
import {TranslateService} from '@ngx-translate/core';
import {map} from 'rxjs/operators';
import {ProjectUnitCostsStore} from '@project/unit-costs/project-unit-costs-page/project-unit-costs-store.service';

@Injectable()
export class ProjectProposedUnitCostBreadcrumbResolver implements Resolve<Observable<number>> {

  constructor(private projectUnitCostsStore: ProjectUnitCostsStore, private translateService: TranslateService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Observable<number>> {
    return of(this.projectUnitCostsStore.unitCost$.pipe(map(
      unitCost => unitCost.id)
    ));
  }
}
