import {InputTranslation} from '@cat/api';

export interface IndicatorOverviewLine {
  outputIndicatorId: number;
  outputIndicatorName: InputTranslation[];
  outputIndicatorMeasurementUnit: InputTranslation[];
  outputIndicatorTargetValueSumUp: number;
  projectOutputNumber: string;
  projectOutputTitle: InputTranslation[];
  projectOutputTargetValue: number;
  resultIndicatorId: number;
  resultIndicatorName: InputTranslation[];
  resultIndicatorMeasurementUnit: InputTranslation[];
  resultIndicatorBaseline: number[];
  resultIndicatorTargetValueSumUp: number;
}
