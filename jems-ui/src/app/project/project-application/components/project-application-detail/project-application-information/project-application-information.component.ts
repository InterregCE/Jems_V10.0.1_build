import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ProjectDetailDTO, ProjectStatusDTO} from '@cat/api';
import moment from 'moment/moment';
import {CallStore} from '../../../../../call/services/call-store.service';
import {LocaleDatePipe} from '../../../../../common/pipe/locale-date.pipe';

@Component({
  selector: 'app-project-application-information',
  templateUrl: './project-application-information.component.html',
  styleUrls: ['./project-application-information.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationInformationComponent {
  STATUS = ProjectStatusDTO.StatusEnum;
  CALL_PATH = CallStore.CALL_DETAIL_PATH;

  @Input()
  project: ProjectDetailDTO;

  constructor(private localeDatePipe: LocaleDatePipe) {
  }

  getApplicantName(): string {
    const user = this?.project?.applicant;
    return user ? `${user.name} ${user.surname}` : '';
  }

  getSubmission(submission?: ProjectStatusDTO): { [key: string]: any } {
    return {
      user: submission?.user?.email,
      date: this.localeDatePipe.transform(submission?.updated)
    };
  }

  getCodeAndTitle(code?: string, title?: string | null): string {
    return code && title ? `${code} - ${title}` : code || title || '';
  }

  getCallInfo(): { [key: string]: string | number } {
    const endDate = moment(this.project?.callSettings.endDate);
    const now = moment(new Date());
    const diff = moment.duration(endDate.diff(now));
    const daysLeft = Math.floor(diff.asDays());

    return {
      call: this.project?.callSettings.callName,
      date: this.localeDatePipe.transform(this.project?.callSettings.endDate),
      days: daysLeft > 0 ? daysLeft : 0,
      hours: diff.hours() > 0 ? diff.hours() : 0,
      minutes: diff.minutes() > 0 ? diff.minutes() : 0
    };
  }
}
