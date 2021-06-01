import {ProjectStatusDTO} from '@cat/api';

export class ProjectStepStatus {
  step: number | undefined;

  constructor(step: number | undefined) {
    this.step = Number(step);
  }

  get draft(): ProjectStatusDTO.StatusEnum {
    return this.step === 1 ? ProjectStatusDTO.StatusEnum.STEP1DRAFT : ProjectStatusDTO.StatusEnum.DRAFT;
  }

  get returnedToApplicant(): ProjectStatusDTO.StatusEnum {
    return ProjectStatusDTO.StatusEnum.RETURNEDTOAPPLICANT;
  }

  get approved(): ProjectStatusDTO.StatusEnum {
    return this.step === 1 ? ProjectStatusDTO.StatusEnum.STEP1APPROVED : ProjectStatusDTO.StatusEnum.APPROVED;
  }

  get notApproved(): ProjectStatusDTO.StatusEnum {
    return this.step === 1 ? ProjectStatusDTO.StatusEnum.STEP1NOTAPPROVED : ProjectStatusDTO.StatusEnum.NOTAPPROVED;
  }

  get approvedWithConditions(): ProjectStatusDTO.StatusEnum {
    return this.step === 1 ? ProjectStatusDTO.StatusEnum.STEP1APPROVEDWITHCONDITIONS : ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS;
  }

  get eligible(): ProjectStatusDTO.StatusEnum {
    return this.step === 1 ? ProjectStatusDTO.StatusEnum.STEP1ELIGIBLE : ProjectStatusDTO.StatusEnum.ELIGIBLE;
  }

  get ineligible(): ProjectStatusDTO.StatusEnum {
    return this.step === 1 ? ProjectStatusDTO.StatusEnum.STEP1INELIGIBLE : ProjectStatusDTO.StatusEnum.INELIGIBLE;
  }
}
