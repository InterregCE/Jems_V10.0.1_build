import {ProjectPartnerBudgetModel} from './ProjectPartnerBudgetModel';

export interface ProjectPartnerBudgetAndContribution {
    partnerSortNumber: number;
    partnerAbbreviation: string;
    partnerRole: string;
    partnerCountry: string;
    isPartnerActive: boolean;
    costType: string;
    budgets: ProjectPartnerBudgetModel[];
    publicContribution: number;
    autoPublicContribution: number;
    privateContribution: number;
    totalContribution: number;
    totalEligibleBudget: number;
    percentOfTotalBudget: number;
}
