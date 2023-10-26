import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
  AuditControlCorrectionDetailPageStore
} from '@project/project-application/report/report-corrections-overview/report-corrections-audit-control-detail-page/audit-control-correction-overview/audit-control-correction-detail/audit-control-correction-detail-page.store';
import {combineLatest, Observable} from 'rxjs';
import {ProjectAuditControlCorrectionExtendedDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';

@Component({
  selector: 'jems-audit-control-correction-detail',
  templateUrl: './audit-control-correction-detail.component.html',
  styleUrls: ['./audit-control-correction-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AuditControlCorrectionDetailComponent {

  data$: Observable<{
    projectId: number;
    auditControlId: number;
    correction: ProjectAuditControlCorrectionExtendedDTO;
  }>;

  constructor(
    public router: RoutingService,
    private auditControlCorrectionDetailPageStore: AuditControlCorrectionDetailPageStore
  ) {
    this.data$ = combineLatest([
      this.auditControlCorrectionDetailPageStore.projectId$,
      this.auditControlCorrectionDetailPageStore.auditControlId$,
      this.auditControlCorrectionDetailPageStore.correction$
    ])
      .pipe(map(([projectId, auditControlId, correction]) => (
        {
          projectId,
          auditControlId: Number(auditControlId),
          correction
        })
      ));
  }

  redirectToCorrectionsOverview(projectId: number, auditControlId: number) {
    this.router.navigate([`/app/project/detail/${projectId}/corrections/auditControl/${auditControlId}`]);
  }
}
