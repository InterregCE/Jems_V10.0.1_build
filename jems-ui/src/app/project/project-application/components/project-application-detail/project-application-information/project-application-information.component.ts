import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {OutputProject, OutputProjectStatus} from '@cat/api';
import moment from 'moment/moment';
import {Tables} from '../../../../../common/utils/tables';
import {CallStore} from '../../../../../call/services/call-store.service';

@Component({
  selector: 'app-project-application-information',
  templateUrl: './project-application-information.component.html',
  styleUrls: ['./project-application-information.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationInformationComponent {
  STATUS = OutputProjectStatus.StatusEnum;
  CALL_PATH = CallStore.CALL_DETAIL_PATH;

  @Input()
  project: OutputProject;

  getApplicantName(): string {
    const user = this?.project?.applicant;
    return user ? `${user.name} ${user.surname}` : '';
  }

  getSubmission(submission?: OutputProjectStatus): { [key: string]: any } {
    return {
      user: submission?.user?.email,
      date: this.formatDate(submission?.updated)
    };
  }

  getCodeAndTitle(code?: string, title?: string| null): string {
    return code && title ? `${code} - ${title}` : code || title || '';
  }

  getCallInfo(): { [key: string]: string | number } {
    const endDate = moment(this.project?.callSettings.endDate);
    const now = moment(new Date());
    const diff = moment.duration(endDate.diff(now));
    const daysLeft = Math.floor(diff.asDays());

    return {
      call: this.project?.callSettings.callName,
      date: endDate.format(Tables.DEFAULT_DATE_FORMAT),
      days: daysLeft > 0 ? daysLeft : 0,
      hours: diff.hours() > 0 ? diff.hours() : 0,
      minutes: diff.minutes() > 0 ? diff.minutes() : 0
    };
  }

  formatDate(date?: Date): string {
    return date ? moment(date).format(Tables.DEFAULT_DATE_FORMAT) : '';
  }
}
