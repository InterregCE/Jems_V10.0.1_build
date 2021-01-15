import {IndicatorOutputDto, InputTranslation} from '@cat/api';

export interface WorkPackageOutputDetails {
  outputNumber: number;
  programmeOutputIndicatorId: number;
  title: Array<InputTranslation>;
  targetValue: string;
  periodNumber: number;
  description: Array<InputTranslation>;
  indicator: IndicatorOutputDto | null;
  id: number;
}
