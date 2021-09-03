import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormArray, FormBuilder} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectPartnerDetailPageStore} from '@project/partner/project-partner-detail-page/project-partner-detail-page.store';
import {ActivatedRoute} from '@angular/router';
import {catchError, map, switchMap, take, tap} from 'rxjs/operators';
import {
  OutputWorkPackageSimple,
  ProjectDetailDTO,
  ProjectPartnerStateAidDTO,
  WorkPackageActivitySummaryDTO
} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {WorkPackagePageStore} from '@project/work-package/project-work-package-page/work-package-detail-page/project-work-package-page-store.service';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectWorkPackagePageStore} from '@project/work-package/project-work-package-page/project-work-package-page-store.service';

interface ActivityIdentificationInformation {
  workpackageNumber: number,
  activities: WorkPackageActivitySummaryDTO[]
}

@Component({
  selector: 'app-project-partner-state-aid-tab',
  templateUrl: './project-partner-state-aid-tab.component.html',
  styleUrls: ['./project-partner-state-aid-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPartnerStateAidTabComponent {
  APPLICATION_FORM = APPLICATION_FORM;
  separatorKeysCodes: number[] = [ENTER, COMMA];

  data$: Observable<{
    stateAid: ProjectPartnerStateAidDTO,
    displayActivities: ActivityIdentificationInformation[],
    activities: WorkPackageActivitySummaryDTO[],
    project: ProjectDetailDTO
  }>;

  form = this.formBuilder.group({
    answer1: [],
    justification1: [],
    answer2: [],
    justification2: [],
    answer3: [],
    justification3: [],
    answer4: [],
    justification4: [],
    activities: this.formBuilder.array([]),
    stateAidScheme: [],
  });

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private activatedRoute: ActivatedRoute,
              private pageStore: ProjectPartnerDetailPageStore,
              private workPackageStore: WorkPackagePageStore,
              private workPackageProjectStore: ProjectWorkPackagePageStore,
              private projectStore: ProjectStore) {
    this.formService.init(this.form, this.pageStore.isProjectEditable$);
    this.data$ = combineLatest([
      this.pageStore.stateAid$,
      this.projectStore.activities$,
      this.workPackageProjectStore.workPackages$,
      this.projectStore.project$
    ]).pipe(
      map(([stateAid, activities, workpackages, project]) => ({
        stateAid,
        displayActivities: this.mapWorkpackagesAndActivities(activities, workpackages),
        activities,
        project
      })),
      tap(stateAid => this.resetForm(stateAid.stateAid, stateAid.activities))
    );
  }

  updateStateAid(): void {
    this.pageStore.partner$
      .pipe(
        take(1),
        switchMap(partner => this.pageStore.updateStateAid(partner.id, this.form.value)),
        tap(() => this.formService.setSuccess('project.partner.state.aid.saved')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  activities(): FormArray {
    return this.form.get('activities') as FormArray;
  }

  resetForm(stateAid: ProjectPartnerStateAidDTO, activities: WorkPackageActivitySummaryDTO[]): void {
    this.form.reset(stateAid);
    this.activities().clear();
    stateAid?.activities?.forEach((activity: WorkPackageActivitySummaryDTO) => {
      this.addActivity(activity, activities)
    });
    this.formService.setDirty(false);
  }

  addActivity(activity: WorkPackageActivitySummaryDTO, activitiesSorted: WorkPackageActivitySummaryDTO[]): void {
    const selectedActivities = this.getCurrentlySelectedActivities().concat(activity);

    this.activities().clear();
    selectedActivities.forEach(activity => this.activities().push(this.formBuilder.group(activity)));
    this.formService.setDirty(true);
  }

  getCurrentlySelectedActivities(): WorkPackageActivitySummaryDTO[] {
    return (this.activities().value as WorkPackageActivitySummaryDTO[]);
  }

  getActivitiesWithoutSelected(activities: WorkPackageActivitySummaryDTO[]): WorkPackageActivitySummaryDTO[] {
    return activities.filter(activity => (this.getCurrentlySelectedActivities().filter(selectedActivity => selectedActivity.activityId === activity.activityId).length) == 0);
  }

  removeActivity(index: number): void {
    this.activities().removeAt(index);
    this.formService.setDirty(true);
  }

  getDisplayValueForActivityNumber(activities: WorkPackageActivitySummaryDTO[], activity: WorkPackageActivitySummaryDTO): string {
    const a = activities.find(a => a.activityId === activity.activityId) as WorkPackageActivitySummaryDTO;
    return `${'ACTIVITY ' + (a?.workPackageNumber.toString() || '') + '.' + (a?.activityNumber.toString() || '')}`;
  }

  getDisplayValueForWorkPackageNumber(workpackageNumber: number): string {
    return `${'WORKPACKAGE ' + (workpackageNumber.toString() || '')}`;
  }

  getStateAidCheck(): string {
    // if all are answered yes, then there is a risk of state aid
    if (
      this.form.controls.answer1.value === true
      && this.form.controls.answer2.value === true
      && this.form.controls.answer3.value === true
      && this.form.controls.answer4.value === true
    )
    {
      return 'project.partner.state.aid.risk.of.state.aid';
    }

    // if one of the first three is no but last is yes, then there is risk of indirect aid
    if (
      (
        this.form.controls.answer1.value === false
        || this.form.controls.answer2.value === false
        || this.form.controls.answer3.value === false
      )
      && this.form.controls.answer4.value
    ) {
      return 'project.partner.state.aid.risk.of.indirect.aid';
    }

    // if one of them is missing values, return empty string and use the default from html
    if (
        this.form.controls.answer1.value === null
      || this.form.controls.answer1.value === undefined
      || this.form.controls.answer2.value === null
      || this.form.controls.answer2.value === undefined
      || this.form.controls.answer3.value === null
      || this.form.controls.answer3.value === undefined
      || this.form.controls.answer4.value === null
      || this.form.controls.answer4.value === undefined
    ) {
      return '';
    }

    // if all are answered and it's not one fo the first two cases, there is no risk of state aid
    return 'project.partner.state.aid.no.risk.of.state.aid';
  }

    updateSelectedScheme(value: any): void {
    this.form.controls.stateAidScheme.setValue(value);
  }

  private mapWorkpackagesAndActivities(activities: WorkPackageActivitySummaryDTO[], workpackages: OutputWorkPackageSimple[]): ActivityIdentificationInformation[]
  {
    const workpackagesAndActivities: ActivityIdentificationInformation[] = [];
    workpackages.forEach(workpackage => {
      workpackagesAndActivities.push({
        workpackageNumber: workpackage.number,
        activities: activities.filter(activity => activity.workPackageNumber === workpackage.number)
      })
    })
    return workpackagesAndActivities;
  }
}
