import {ProjectDetailDTO, ProjectStatusDTO} from '@cat/api';

export enum ProjectPaths {
  PROJECT_DETAIL_PATH = '/app/project/detail/',
}

export class ProjectUtil {

  static isDraft(statusOrProject: ProjectDetailDTO | ProjectStatusDTO): boolean {
    const status = this.getStatus(statusOrProject);
    return status === ProjectStatusDTO.StatusEnum.STEP1DRAFT || status === ProjectStatusDTO.StatusEnum.DRAFT;
  }

  static isReturnedToApplicant(statusOrProject: ProjectDetailDTO | ProjectStatusDTO): boolean {
    const status = this.getStatus(statusOrProject);
    return status === ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT
      || status === ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANTFORCONDITIONS
      || status === ProjectStatusDTO.StatusEnum.MODIFICATIONPRECONTRACTING;
  }

  static isOpenForModifications(statusOrProject: ProjectDetailDTO | ProjectStatusDTO): boolean {
    return ProjectUtil.isDraft(statusOrProject) || ProjectUtil.isReturnedToApplicant(statusOrProject);
  }

  private static getStatus(statusOrProject: ProjectDetailDTO | ProjectStatusDTO | ProjectStatusDTO.StatusEnum) {
    return ((statusOrProject as ProjectDetailDTO)?.projectStatus || statusOrProject)?.status || statusOrProject;
  }
}
