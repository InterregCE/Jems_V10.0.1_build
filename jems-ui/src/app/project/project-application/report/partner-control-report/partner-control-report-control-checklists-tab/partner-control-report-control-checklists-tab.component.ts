import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProgrammeChecklistDetailDTO, ProjectPartnerControlReportDTO} from '@cat/api';
import {BehaviorSubject, Observable} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {map} from 'rxjs/operators';
import {FormService} from '@common/components/section/form/form.service';
import {
  PartnerControlReportStore
} from '@project/project-application/report/partner-control-report/partner-control-report-store.service';

@Component({
    selector: 'jems-partner-control-report-control-checklists-tab-component',
    templateUrl: './partner-control-report-control-checklists-tab.component.html',
    styleUrls: ['./partner-control-report-control-checklists-tab.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [FormService]
})
export class PartnerControlReportControlChecklistsTabComponent {
    ChecklistType = ProgrammeChecklistDetailDTO.TypeEnum;

    data$: Observable<{
        partnerControlReport: ProjectPartnerControlReportDTO;
    }>;

    // TODO: create a component
    error$ = new BehaviorSubject<APIError | null>(null);
    actionPending = false;

    constructor(public store: PartnerControlReportStore) {
        this.data$ = store.partnerControlReport$.pipe(
            map((partnerControlReport) => ({partnerControlReport}))
        );
    }
}
