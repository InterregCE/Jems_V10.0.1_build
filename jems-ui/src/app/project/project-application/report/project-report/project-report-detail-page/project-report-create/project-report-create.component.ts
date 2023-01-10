import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';

@Component({
  selector: 'jems-project-report-create',
  templateUrl: './project-report-create.component.html',
  styleUrls: ['./project-report-create.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectReportCreateComponent {
  constructor(public pageStore: ProjectReportDetailPageStore) { }
}
