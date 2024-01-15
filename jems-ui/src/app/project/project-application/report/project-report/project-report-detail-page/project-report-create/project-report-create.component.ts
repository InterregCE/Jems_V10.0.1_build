import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {ProjectReportPageStore} from '@project/project-application/report/project-report/project-report-page-store.service';
import {RoutingService} from '@common/services/routing.service';
import {combineLatest} from 'rxjs';
import {map, take, tap} from 'rxjs/operators';
import {ProjectPaths} from '@project/common/project-util';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';

@Component({
  selector: 'jems-project-report-create',
  templateUrl: './project-report-create.component.html',
  styleUrls: ['./project-report-create.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectReportCreateComponent {
  constructor(public pageStore: ProjectReportDetailPageStore,
              private projectReportPageStore: ProjectReportPageStore,
              private projectStore: ProjectStore,
              private routingService: RoutingService) {
    combineLatest([
      this.projectStore.projectId$,
      this.projectReportPageStore.userCanEditReport$,
    ]).pipe(
      take(1),
      map(([projectId, canEdit]) => !canEdit && this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, projectId])),
    ).subscribe();
  }
}
