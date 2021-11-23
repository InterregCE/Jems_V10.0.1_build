import {AppControl} from '@common/components/section/form/app-control';

export class ProjectResultsPageConstants {

  public static RESULTS: AppControl = {
    name: 'results'
  };

  public static RESULT_INDICATOR: AppControl = {
    name: 'programmeResultIndicatorId'
  };

  public static RESULT_NUMBER: AppControl = {
    name: 'resultNumber',
  };

  public static TARGET_VALUE: AppControl = {
    name: 'targetValue',
  };

  public static BASELINE: AppControl = {
    name: 'baseline',
  };

  public static BASELINE_MAX_VALUE: AppControl = {
    name: 'baselineMaxValue',
  };

  public static PERIOD: AppControl = {
    name: 'periodNumber',
  };

  public static DESCRIPTION: AppControl = {
    name: 'description',
    maxLength: 1000,
  };
}
