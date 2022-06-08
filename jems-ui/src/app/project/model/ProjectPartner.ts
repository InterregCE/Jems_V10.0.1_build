import {ProjectPartnerRoleEnum} from './ProjectPartnerRoleEnum';

export class ProjectPartner {
  id: number;
  active: boolean;
  abbreviation: string;
  role: ProjectPartnerRoleEnum | null;
  sortNumber: number;
  country: string;
  partnerNumber: string;

  constructor(
    id: number,
    active: boolean,
    abbreviation: string,
    role: ProjectPartnerRoleEnum | null,
    sortNumber: number,
    country: string,
    partnerNumber: string
  ) {
    this.id = id;
    this.active = active;
    this.abbreviation = abbreviation;
    this.role = role;
    this.sortNumber = sortNumber;
    this.country = country;
    this.partnerNumber = partnerNumber;
  }
}
