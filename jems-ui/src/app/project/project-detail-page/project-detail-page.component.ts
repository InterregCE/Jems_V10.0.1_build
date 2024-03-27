import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectDetailDTO, ProjectPeriodDTO} from '@cat/api';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {
  ContractReportingStore
} from '@project/project-application/contracting/contract-reporting/contract-reporting.store';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';

@Component({
  selector: 'jems-project-detail-page',
  templateUrl: './project-detail-page.component.html',
  styleUrls: ['./project-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectDetailPageComponent {

  projectEndDate: Date | null;

  data$: Observable<{
    currentVersionOfProject: ProjectDetailDTO;
    currentVersionOfProjectTitle: string;
    projectStartDate: string;
    projectEndDateAsString: string;
    projectDurationAsString: string;
    periods: ProjectPeriodDTO[];
  }>;

  constructor(public projectStore: ProjectStore,
              public projectVersionStore: ProjectVersionStore,
              public contractReportingStore: ContractReportingStore) {
    this.data$ = combineLatest([
      this.projectStore.currentVersionOfProject$,
      this.projectStore.currentVersionOfProjectTitle$,
      this.contractReportingStore.availablePeriods$,
      this.contractReportingStore.contractingMonitoringStartDate$,
    ]).pipe(
      map(([currentVersionOfProject, currentVersionOfProjectTitle, availablePeriods, contractingMonitoringStartDate]) => ({
        currentVersionOfProject,
        currentVersionOfProjectTitle,
        projectStartDate: contractingMonitoringStartDate,
        projectEndDateAsString: this.projectEndDateString(availablePeriods),
        projectDurationAsString: this.projectDurationString(availablePeriods),
        periods: availablePeriods,
      }))
    );
  }

  projectEndDateString(periods: ProjectPeriodDTO[]): string {
    const period = periods.find(p => p.number === (periods.length - 1));
    this.projectEndDate = period ? new Date(period.endDate) : null;
    return period ? period.endDate : '';
  }

  projectDurationString(periods: ProjectPeriodDTO[]): string {
    const period = periods.find(p => p.number === (periods.length - 1));
    return period ? period.end.toString() : '';
  }

}
