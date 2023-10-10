import {Component} from '@angular/core';
import {RoutingService} from '@common/services/routing.service';
import {
  ReportCorrectionsAuditControlDetailPageStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/report-corrections-audit-control-detail-page.store';
import {map, take, tap} from 'rxjs/operators';
import {ProjectPaths} from '@project/common/project-util';
import {combineLatest} from 'rxjs';

@Component({
  selector: 'jems-report-corrections-audit-control-create-page',
  templateUrl: './report-corrections-audit-control-create-page.component.html',
  styleUrls: ['./report-corrections-audit-control-create-page.component.scss']
})
export class ReportCorrectionsAuditControlCreatePageComponent {

  constructor(
    private routingService: RoutingService,
    private detailPageStore: ReportCorrectionsAuditControlDetailPageStore,
  ) {
    combineLatest([
      this.detailPageStore.projectId$,
      this.detailPageStore.canEdit$
    ]).pipe(
      take(1),
      map(([projectId, canEdit]) => !canEdit && this.routingService.navigate([ProjectPaths.PROJECT_DETAIL_PATH, projectId])),
    ).subscribe();
  }
}
