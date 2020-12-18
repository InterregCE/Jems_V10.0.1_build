import {FormGroup, ValidatorFn, Validators} from '@angular/forms';
import {AppControl} from '@common/components/section/form/app-control';

export class ProjectWorkPackageActivitiesTabConstants {

  public static ACTIVITIES: AppControl = {
    name: 'activities'
  };

  public static TITLE: AppControl = {
    name: 'title',
    validators: [Validators.maxLength(200)]
  };

  public static START_PERIOD: AppControl = {
    name: 'startPeriod',
  };

  public static END_PERIOD: AppControl = {
    name: 'endPeriod',
  };

  public static PERIODS: AppControl = {
    name: 'periods',
    errorMessages: {
      startAfterEnd: 'project.work.package.periods.start.after.end'
    },
    validators: [ProjectWorkPackageActivitiesTabConstants.startBeforeEndPeriod]
  };

  public static DESCRIPTION: AppControl = {
    name: 'description',
    validators: [Validators.maxLength(500)]
  };

  public static DELIVERABLES: AppControl = {
    name: 'deliverables'
  };

  public static DELIVERABLE: AppControl = {
    name: 'description',
    validators: [Validators.maxLength(200)]
  };

  public static PERIOD: AppControl = {
    name: 'period',
  };

  public static startBeforeEndPeriod(activity: FormGroup): ValidatorFn | null {
    const startPeriod = activity.get(ProjectWorkPackageActivitiesTabConstants.START_PERIOD.name)?.value;
    const endPeriod = activity.get(ProjectWorkPackageActivitiesTabConstants.END_PERIOD.name)?.value;
    if (!startPeriod || !endPeriod) {
      return null;
    }
    if (startPeriod > endPeriod) {
      return {startAfterEnd: true} as any;
    }
    return null;
  }
}
