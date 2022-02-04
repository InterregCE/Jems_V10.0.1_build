import {ProjectPartnerRoleEnum} from './ProjectPartnerRoleEnum';
import {ProjectCallSettingsDTO} from '@cat/api';
import CallTypeEnum = ProjectCallSettingsDTO.CallTypeEnum;

export class ProjectPartner {
  id: number;
  abbreviation: string;
  role: ProjectPartnerRoleEnum | null;
  sortNumber: number;
  country: string;

  constructor(id: number, abbreviation: string, role: ProjectPartnerRoleEnum | null, sortNumber: number, country: string) {
    this.id = id;
    this.abbreviation = abbreviation;
    this.role = role;
    this.sortNumber = sortNumber;
    this.country = country;
  }

  toPartnerNumberString(callType?: CallTypeEnum): string {
    if (callType === undefined || callType === CallTypeEnum.STANDARD) {
      return this.role === ProjectPartnerRoleEnum.LEAD_PARTNER ? 'LP1' : 'PP'.concat(this.sortNumber.toString());
    }
    return 'PP1 SPF';
  }
}
