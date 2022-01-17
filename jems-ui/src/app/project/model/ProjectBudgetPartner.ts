import {ProjectPartnerSummaryDTO } from '@cat/api';

export interface ProjectBudgetPartner {
  id: number;
  abbreviation: string;
  role: ProjectPartnerSummaryDTO.RoleEnum;
  country: string;
  region: string;
  sortNumber: number;
  totalBudget: number;
}
