import {AppControl} from '@common/components/section/form/app-control';

export class ProjectWorkPackageOutputsTabConstants {

  public static OUTPUTS: AppControl = {
    name: 'outputs'
  };

  public static TITLE: AppControl = {
    name: 'title',
    maxLength: 200,
  };

  public static RESULT_INDICATOR: AppControl = {
    name: 'programmeOutputIndicatorId',
  };

  public static DESCRIPTION: AppControl = {
    name: 'description',
    maxLength: 500,
  };

  public static PERIOD: AppControl = {
    name: 'periodNumber',
  };

  public static OUTPUT_NUMBER: AppControl = {
    name: 'outputNumber',
  };

}
