import {IndicatorOutputDto} from '@cat/api';

export interface WorkPackageOutputDetails {
  outputNumber: number;
  programmeOutputIndicatorId: number;
  title: string;
  targetValue: string;
  periodNumber: number;
  description: string;
  indicator: IndicatorOutputDto | null;
  id: number;
}
