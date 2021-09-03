import {Pipe, PipeTransform} from '@angular/core';
import {WorkPackageActivitySummaryDTO} from '@cat/api';

@Pipe({name: 'filterActivities', pure: true})
export class ProjectWorkPackageActivitiesFilterPipe implements PipeTransform {

  transform(activities: WorkPackageActivitySummaryDTO[], filterText: string): WorkPackageActivitySummaryDTO[] {
    if (!filterText) {
      return activities;
    }

    return activities.filter(activity =>
      ProjectWorkPackageActivitiesFilterPipe.getSearchValueForActivity(activity).toUpperCase().includes(filterText.toUpperCase())
    );
  }

  private static getSearchValueForActivity(activity: WorkPackageActivitySummaryDTO): string {
    return `${'ACTIVITY'} ${activity?.workPackageNumber || ''}${'.'}${activity?.activityNumber || ''}`;
  }

}
