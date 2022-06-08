import {ProjectDetailDTO, ProjectStatusDTO} from '@cat/api';

export enum ProjectPaths {
  PROJECT_DETAIL_PATH = '/app/project/detail/',
}

export class ProjectUtil {

  static isDraft(statusOrProject: ProjectDetailDTO | ProjectStatusDTO): boolean {
    const status = this.getStatus(statusOrProject);
    return status === ProjectStatusDTO.StatusEnum.DRAFT;
  }

  static isStep1Draft(statusOrProject: ProjectDetailDTO | ProjectStatusDTO): boolean {
    const status = this.getStatus(statusOrProject);
    return status === ProjectStatusDTO.StatusEnum.STEP1DRAFT;
  }

  static isReturnedToApplicant(statusOrProject: ProjectDetailDTO | ProjectStatusDTO): boolean {
    const status = this.getStatus(statusOrProject);
    return status === ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT
      || status === ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANTFORCONDITIONS
      || status === ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTING
      || status === ProjectStatusDTO.StatusEnum.INMODIFICATION;
  }

  static isOpenForModifications(statusOrProject: ProjectDetailDTO | ProjectStatusDTO): boolean {
    return ProjectUtil.isDraft(statusOrProject) || ProjectUtil.isStep1Draft(statusOrProject) || ProjectUtil.isReturnedToApplicant(statusOrProject);
  }

  static isInModifiableStatusAfterApproved(statusOrProject: ProjectDetailDTO | ProjectStatusDTO): boolean {
    return this.getStatus(statusOrProject) === ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTING ||
      this.getStatus(statusOrProject) === ProjectStatusDTO.StatusEnum.INMODIFICATION;
  }

  static isInModifiableStatusBeforeApproved(statusOrProject: ProjectDetailDTO | ProjectStatusDTO): boolean {
    const status = this.getStatus(statusOrProject);
    return status === ProjectStatusDTO.StatusEnum.STEP1DRAFT
      || status === ProjectStatusDTO.StatusEnum.DRAFT
      || status === ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT
      || status === ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANTFORCONDITIONS;
  }

  static isInApprovedOrAnyStatusAfterApproved(statusOrProject: ProjectDetailDTO | ProjectStatusDTO): boolean {
    const status = this.getStatus(statusOrProject);
    return status === ProjectStatusDTO.StatusEnum.APPROVED
      || status === ProjectStatusDTO.StatusEnum.MODIFICATIONREJECTED
      || status === ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTING
      || status === ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTINGSUBMITTED
      || status === ProjectStatusDTO.StatusEnum.CONTRACTED
      || status === ProjectStatusDTO.StatusEnum.INMODIFICATION
      || status === ProjectStatusDTO.StatusEnum.MODIFICATIONSUBMITTED;
  }

  static isContractedOrAnyStatusAfterContracted(statusOrProject: ProjectDetailDTO | ProjectStatusDTO | ProjectStatusDTO.StatusEnum): boolean {
    const status = this.getStatus(statusOrProject);
    return status === ProjectStatusDTO.StatusEnum.CONTRACTED
      || status === ProjectStatusDTO.StatusEnum.INMODIFICATION
      || status === ProjectStatusDTO.StatusEnum.MODIFICATIONSUBMITTED
      || status === ProjectStatusDTO.StatusEnum.MODIFICATIONREJECTED;
  }

  private static getStatus(statusOrProject: ProjectDetailDTO | ProjectStatusDTO | ProjectStatusDTO.StatusEnum) {
    return ((statusOrProject as ProjectDetailDTO)?.projectStatus || statusOrProject)?.status || statusOrProject;
  }
}
