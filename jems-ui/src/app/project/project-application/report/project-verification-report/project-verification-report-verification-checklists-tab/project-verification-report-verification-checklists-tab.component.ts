import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {
  ProgrammeChecklistDetailDTO,
  ProjectReportDTO,
} from '@cat/api';
import {BehaviorSubject, Observable} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {map} from 'rxjs/operators';
import {
  ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';

@Component({
  selector: 'jems-project-verification-report-verification-checklists-tab',
  templateUrl: './project-verification-report-verification-checklists-tab.component.html',
  styleUrls: ['./project-verification-report-verification-checklists-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService]
})
export class ProjectVerificationReportVerificationChecklistsTabComponent {

  ChecklistType = ProgrammeChecklistDetailDTO.TypeEnum;

  data$: Observable<{
    projectVerificationReport: ProjectReportDTO;
  }>;

  error$ = new BehaviorSubject<APIError | null>(null);
  actionPending = false;
  verificationReportEditable$: Observable<boolean>;
  verificationReport$: Observable<ProjectReportDTO>;

  constructor(
    public store: ProjectReportDetailPageStore,
    private reportPageStore: ProjectReportPageStore,
  ) {
    this.verificationReportEditable$ = this.reportPageStore.userCanEditVerification$;
    this.verificationReport$ = this.store.projectReport$;
    this.data$ = store.projectReport$.pipe(
      map((projectVerificationReport) => ({projectVerificationReport}))
    );
  }
}
