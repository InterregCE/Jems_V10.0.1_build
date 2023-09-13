import {ProjectDetailDTO, ProjectPeriodDTO, ProjectStatusDTO} from '@cat/api';
import * as moment from 'moment/moment';

export enum ProjectPaths {
  PROJECT_DETAIL_PATH = '/app/project/detail/',
}

export class ProjectUtil {

  static isDraft(statusOrProject: ProjectDetailDTO | ProjectStatusDTO | string): boolean {
    const status = this.getStatus(statusOrProject);
    return status === ProjectStatusDTO.StatusEnum.DRAFT;
  }

  static isStep1Draft(statusOrProject: ProjectDetailDTO | ProjectStatusDTO | string): boolean {
    const status = this.getStatus(statusOrProject);
    return status === ProjectStatusDTO.StatusEnum.STEP1DRAFT;
  }

  static isReturnedToApplicant(statusOrProject: ProjectDetailDTO | ProjectStatusDTO | string): boolean {
    const status = this.getStatus(statusOrProject);
    return status === ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT
      || status === ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANTFORCONDITIONS
      || status === ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTING
      || status === ProjectStatusDTO.StatusEnum.INMODIFICATION;
  }

  static isOpenForModifications(statusOrProject: ProjectDetailDTO | ProjectStatusDTO | string): boolean {
    return ProjectUtil.isDraft(statusOrProject) || ProjectUtil.isStep1Draft(statusOrProject) || ProjectUtil.isReturnedToApplicant(statusOrProject);
  }

  static isInModifiableStatusAfterApproved(statusOrProject: ProjectDetailDTO | ProjectStatusDTO | string): boolean {
    return this.getStatus(statusOrProject) === ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTING ||
      this.getStatus(statusOrProject) === ProjectStatusDTO.StatusEnum.INMODIFICATION;
  }

  static isInModifiableStatusBeforeApproved(statusOrProject: ProjectDetailDTO | ProjectStatusDTO | string): boolean {
    const status = this.getStatus(statusOrProject);
    return status === ProjectStatusDTO.StatusEnum.STEP1DRAFT
      || status === ProjectStatusDTO.StatusEnum.DRAFT
      || status === ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT
      || status === ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANTFORCONDITIONS;
  }

  static isInSubmittedOrAnyStatusAfterSubmitted(statusOrProject: ProjectDetailDTO | ProjectStatusDTO): boolean {
    const status = this.getStatus(statusOrProject);
    return status == ProjectStatusDTO.StatusEnum.SUBMITTED
      || status == ProjectStatusDTO.StatusEnum.CONDITIONSSUBMITTED
      || status == ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT
      || status == ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANTFORCONDITIONS
      || status == ProjectStatusDTO.StatusEnum.ELIGIBLE
      || status == ProjectStatusDTO.StatusEnum.INELIGIBLE
      || status == ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS
      || status == ProjectStatusDTO.StatusEnum.NOTAPPROVED
      || this.isInApprovedOrAnyStatusAfterApproved(statusOrProject);
  }

  static isInApprovedOrAnyStatusAfterApproved(statusOrProject: ProjectDetailDTO | ProjectStatusDTO): boolean {
    const status = this.getStatus(statusOrProject);
    return status === ProjectStatusDTO.StatusEnum.APPROVED
      || status === ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTING
      || status === ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTINGSUBMITTED
      || this.isContractedOrAnyStatusAfterContracted(statusOrProject);
  }

  static isContractedOrAnyStatusAfterContracted(statusOrProject: ProjectDetailDTO | ProjectStatusDTO | ProjectStatusDTO.StatusEnum): boolean {
    const status = this.getStatus(statusOrProject);
    return status === ProjectStatusDTO.StatusEnum.CONTRACTED
      || status === ProjectStatusDTO.StatusEnum.INMODIFICATION
      || status === ProjectStatusDTO.StatusEnum.MODIFICATIONSUBMITTED
      || status === ProjectStatusDTO.StatusEnum.MODIFICATIONREJECTED;
  }

  private static getStatus(statusOrProject: ProjectDetailDTO | ProjectStatusDTO | ProjectStatusDTO.StatusEnum | string) {
    return ((statusOrProject as ProjectDetailDTO)?.projectStatus || statusOrProject)?.status || statusOrProject;
  }

  static getPeriodArguments(period: ProjectPeriodDTO): { [key: string]: number | string } {
    return {
      periodNumber: period?.number,
      start: period?.start,
      end: period?.end,
      startDate: moment(period?.startDate).format('MM/DD/YYYY'),
      endDate: moment(period?.endDate).format('MM/DD/YYYY')
    };
  }

  static getPeriodKey(hasStartDate: any): string {
    if(hasStartDate) {
      return 'project.application.form.work.package.output.delivery.period.entry.contracted';
    } else {
      return 'project.application.form.work.package.output.delivery.period.entry';
    }
  }
}
