import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot} from '@angular/router';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {Log} from '@common/utils/log';
import {ProjectPaths} from '@project/common/project-util';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {combineLatest} from 'rxjs';
import {tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Injectable({providedIn: 'root'})
export class ProjectApplicationFormVisibilityService {

  constructor(private visibilityStatusService: FormVisibilityStatusService,
              private routingService: RoutingService,
              private projectStore: ProjectStore) {
    combineLatest([
      this.projectStore.project$,
      this.routingService.currentRoute
    ]).pipe(
      tap(([project, currentRoute]) => this.checkRoute(currentRoute.snapshot)),
      untilDestroyed(this)
      ).subscribe();
  }

  private checkRoute(childRoute: ActivatedRouteSnapshot): void {
    let allowed = true;

    if (childRoute.data.breadcrumb === 'project.breadcrumb.applicationForm.unit.costs') {
      allowed = (this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING) &&
        this.visibilityStatusService.shouldBeVisibleIfUnitCostsSelected()) || this.visibilityStatusService.shouldBeVisibleIfProjectDefinedUnitCostsAllowed();
    }

    if (childRoute.data.visibleOnly) {
      allowed = childRoute.data.visibleOnly.some((visible: string) => this.visibilityStatusService.isVisible(visible));
    }

    if (!allowed) {
      Log.info(`Current user role cannot access this route. It is not available in this version of the project. Route:`, this);
      this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, childRoute?.params?.projectId, 'applicationFormIdentification']);
    }
  }
}
