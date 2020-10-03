import {InputProjectRelevanceStrategy} from '@cat/api';

export interface ProjectRelevanceStrategy extends InputProjectRelevanceStrategy {
  id: number;
  projectStrategy: InputProjectRelevanceStrategy.StrategyEnum | string;
}
