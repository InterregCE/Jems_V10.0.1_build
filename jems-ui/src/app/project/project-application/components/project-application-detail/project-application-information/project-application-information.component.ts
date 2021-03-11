import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {InputTranslation, OutputProject, OutputProjectStatus} from '@cat/api';
import {MultiLanguageInputService} from '../../../../../common/services/multi-language-input.service';
import moment from 'moment/moment';
import {Tables} from '../../../../../common/utils/tables';

@Component({
  selector: 'app-project-application-information',
  templateUrl: './project-application-information.component.html',
  styleUrls: ['./project-application-information.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationInformationComponent {
  STATUS = OutputProjectStatus.StatusEnum;

  @Input()
  project: OutputProject;

  getApplicantName(): string {
    const user = this?.project?.firstSubmission?.user || this?.project?.lastResubmission?.user;
    return user ? user.name + ' ' + user.surname : '';
  }

  getSubmission(submission?: OutputProjectStatus): { [key: string]: any } {
    return {
      user: submission?.user?.email,
      date: this.formatDate(submission?.updated)
    };
  }

  getCodeAndTitle(code?: string, titleTranslations?: InputTranslation[]): string {
    const title = MultiLanguageInputService.getFirstTranslation(titleTranslations || []);
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
