import {PartnerUserCollaboratorDTO, ProjectPartnerSummaryDTO} from '@cat/api';

export interface PartnersCollaborationDataPerPartner {
  partner: ProjectPartnerSummaryDTO;
  partnerCollaborators: PartnerUserCollaboratorDTO[];
}
