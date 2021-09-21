import {InputTranslation} from '@cat/api';

export interface CallStateAidDTO {
  id: number;
  abbreviatedName: Array<InputTranslation>;
  selected: boolean;
}
