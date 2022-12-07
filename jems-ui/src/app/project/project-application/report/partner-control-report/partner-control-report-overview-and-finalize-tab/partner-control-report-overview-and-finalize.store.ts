import {Injectable} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {
    PartnerControlReportStore
} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';
import {ControlWorkOverviewDTO, ProjectPartnerReportControlOverviewService} from '@cat/api';
import {switchMap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';

@Injectable({ providedIn: 'root' })
export class PartnerControlReportOverviewAndFinalizeStore {

    controlWorkOverview$: Observable<ControlWorkOverviewDTO>;

    constructor(
        public routingService: RoutingService,
        public controlReportFileStore: PartnerControlReportStore,
        private service: ProjectPartnerReportControlOverviewService
    ) {
        this.controlWorkOverview$ = this.getControlWorkOverview();
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
}
