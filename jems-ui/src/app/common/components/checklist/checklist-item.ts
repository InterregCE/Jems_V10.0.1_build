import {ProgrammeChecklistDTO} from '@cat/api';

export interface ChecklistItem {
  id: number;
  name: string;
  type: ProgrammeChecklistDTO.TypeEnum;
  creatorEmail: string;
  description: string;
}
