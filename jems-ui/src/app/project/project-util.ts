import {ProjectDetailDTO, ProjectStatusDTO} from '@cat/api';

export class ProjectUtil {

  static isDraft(statusOrProject: ProjectDetailDTO | ProjectStatusDTO): boolean {
    const status = (statusOrProject as ProjectDetailDTO)?.projectStatus || statusOrProject;

    return status?.status === ProjectStatusDTO.StatusEnum.STEP1DRAFT
      || status?.status === ProjectStatusDTO.StatusEnum.DRAFT;
  }

  static isOpenForModifications(statusOrProject: ProjectDetailDTO | ProjectStatusDTO): boolean {
    const status = (statusOrProject as ProjectDetailDTO)?.projectStatus || statusOrProject;

    return status?.status === ProjectStatusDTO.StatusEnum.STEP1DRAFT
      || status?.status === ProjectStatusDTO.StatusEnum.DRAFT
      || status?.status === ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT;
  }
}
