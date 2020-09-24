import {InputProjectRelevanceBenefit} from '@cat/api';

export interface ProjectRelevanceBenefit extends InputProjectRelevanceBenefit {
  id: number;
  targetGroup: InputProjectRelevanceBenefit.GroupEnum;
}
