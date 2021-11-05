import {InputTranslation} from '@cat/api';

export interface CallStateAidDTO {
  id: number;
  abbreviatedName: InputTranslation[];
  selected: boolean;
}
