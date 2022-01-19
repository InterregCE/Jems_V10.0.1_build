import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ProjectDetailDTO, ProjectStatusDTO, UserRoleCreateDTO} from '@cat/api';
import moment from 'moment/moment';
import {CallStore} from '../../../../../call/services/call-store.service';
import {LocaleDatePipe} from '@common/pipe/locale-date.pipe';
import {ProjectUtil} from '@project/common/project-util';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@Component({
  selector: 'jems-project-application-information',
  templateUrl: './project-application-information.component.html',
  styleUrls: ['./project-application-information.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationInformationComponent {
  CALL_PATH = CallStore.CALL_DETAIL_PATH;
  ProjectUtil = ProjectUtil;
  PermissionsEnum = PermissionsEnum;

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
    const isInStepOne = this.project?.callSettings.endDateStep1 && !this.project.step2Active;
    const endDate = isInStepOne ? this.project?.callSettings.endDateStep1 : this.project?.callSettings.endDate;
    const now = moment(new Date());
    const diff = moment.duration(moment(endDate).diff(now));
    const daysLeft = Math.floor(diff.asDays());

    return {
      call: this.project?.callSettings.callName,
      date: this.localeDatePipe.transform(endDate),
      days: daysLeft > 0 ? daysLeft : 0,
      hours: diff.hours() > 0 ? diff.hours() : 0,
      minutes: diff.minutes() > 0 ? diff.minutes() : 0
    };
  }

  hasDraftStatusColor(): boolean {
    return ProjectUtil.isDraft(this.project) || ProjectUtil.isReturnedToApplicant(this.project);
  }

  hasSubmittedStatusColor(): boolean {
    return this.projectStatus === ProjectStatusDTO.StatusEnum.SUBMITTED
      || this.projectStatus === ProjectStatusDTO.StatusEnum.ELIGIBLE
      || this.projectStatus === ProjectStatusDTO.StatusEnum.STEP1SUBMITTED
      || this.projectStatus === ProjectStatusDTO.StatusEnum.STEP1ELIGIBLE
      || this.projectStatus === ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTINGSUBMITTED
      || this.projectStatus === ProjectStatusDTO.StatusEnum.MODIFICATIONSUBMITTED;
  }

  hasApprovedStatusColor(): boolean {
    return this.projectStatus === ProjectStatusDTO.StatusEnum.APPROVED
      || this.projectStatus === ProjectStatusDTO.StatusEnum.STEP1APPROVED;
  }

  hasDeclinedStatusColor(): boolean {
    return this.projectStatus === ProjectStatusDTO.StatusEnum.INELIGIBLE
      || this.projectStatus === ProjectStatusDTO.StatusEnum.NOTAPPROVED
      || this.projectStatus === ProjectStatusDTO.StatusEnum.STEP1INELIGIBLE
      || this.projectStatus === ProjectStatusDTO.StatusEnum.STEP1NOTAPPROVED;
  }

  hasApprovedWithConditionsStatusColor(): boolean {
    return this.projectStatus === ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS
      || this.projectStatus === ProjectStatusDTO.StatusEnum.STEP1APPROVEDWITHCONDITIONS;
  }

  hasContractedStatusColor(): boolean {
    return this.projectStatus === ProjectStatusDTO.StatusEnum.CONTRACTED;
  }

  private get projectStatus(): ProjectStatusDTO.StatusEnum {
    return this.project?.projectStatus.status;
  }
}
