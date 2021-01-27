import {ProjectPartnerBudgetModel} from './ProjectPartnerBudgetModel';

export interface ProjectPartnerBudgetAndContribution {
    partnerSortNumber: number;
    partnerRole: string;
    partnerCountry: string;
    budgets: ProjectPartnerBudgetModel[];
    publicContribution: number;
    autoPublicContribution: number;
    privateContribution: number;
    totalContribution: number;
    totalEligibleBudget: number;
    percentOfTotalBudget: number;
}
