import {Injectable} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {
    PartnerControlReportStore
} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';
import {
  ControlDeductionOverviewDTO,
  ControlWorkOverviewDTO,
  ProjectPartnerReportControlOverviewService
} from '@cat/api';
import {switchMap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';

@Injectable({ providedIn: 'root' })
export class PartnerControlReportOverviewAndFinalizeStore {

    controlWorkOverview$: Observable<ControlWorkOverviewDTO>;
    controlDeductionOverview$: Observable<ControlDeductionOverviewDTO>;

    constructor(
        public routingService: RoutingService,
        public controlReportFileStore: PartnerControlReportStore,
        private service: ProjectPartnerReportControlOverviewService
    ) {
        this.controlWorkOverview$ = this.getControlWorkOverview();
        this.controlDeductionOverview$ = this.getControlDeductionOverview();
    }

    private getControlWorkOverview(): Observable<ControlWorkOverviewDTO> {
        return combineLatest([
            this.controlReportFileStore.partnerId$,
            this.controlReportFileStore.reportId$,
        ]).pipe(
            switchMap(([partnerId, reportId]) =>
                this.service.getControlWorkOverview(partnerId, reportId)
            )
        );
    }

  private getControlDeductionOverview(): Observable<ControlDeductionOverviewDTO> {
    return combineLatest([
      this.controlReportFileStore.partnerId$,
      this.controlReportFileStore.partnerControlReport$
    ]).pipe(
      switchMap(([partnerId,report]) => {
          return this.service.getControlDeductionByTypologyOfErrorsOverview(partnerId, report?.id, report?.linkedFormVersion);
        }
      )
    );
  }
}
