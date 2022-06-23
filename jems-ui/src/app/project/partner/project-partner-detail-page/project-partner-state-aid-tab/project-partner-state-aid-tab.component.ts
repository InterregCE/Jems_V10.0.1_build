import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormArray, FormBuilder} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectPartnerDetailPageStore} from '@project/partner/project-partner-detail-page/project-partner-detail-page.store';
import {ActivatedRoute} from '@angular/router';
import {catchError, filter, map, switchMap, take, tap} from 'rxjs/operators';
import {
  OutputWorkPackageSimple,
  ProgrammeStateAidDTO,
  ProjectDetailDTO,
  ProjectPartnerStateAidDTO,
  WorkPackageActivitySummaryDTO
} from '@cat/api';
import {combineLatest, Observable, of} from 'rxjs';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {WorkPackagePageStore} from '@project/work-package/project-work-package-page/work-package-detail-page/work-package-page-store.service';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ProjectWorkPackagePageStore} from '@project/work-package/project-work-package-page/project-work-package-page-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {RoutingService} from '@common/services/routing.service';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import {Alert} from '@common/components/forms/alert';

interface ActivityIdentificationInformation {
  workpackage: OutputWorkPackageSimple;
  activities: WorkPackageActivitySummaryDTO[];
}

@UntilDestroy()
@Component({
  selector: 'jems-project-partner-state-aid-tab',
  templateUrl: './project-partner-state-aid-tab.component.html',
  styleUrls: ['./project-partner-state-aid-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPartnerStateAidTabComponent {
  APPLICATION_FORM = APPLICATION_FORM;
  separatorKeysCodes: number[] = [ENTER, COMMA];
  Alert = Alert;

  data$: Observable<{
    stateAid: ProjectPartnerStateAidDTO;
    displayActivities: ActivityIdentificationInformation[];
    activities: WorkPackageActivitySummaryDTO[];
    project: ProjectDetailDTO;
    isEditable: boolean;
    stateAidsForDropdown: ProgrammeStateAidDTO[];
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
    stateAidScheme: [0],
  });

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private activatedRoute: ActivatedRoute,
              private pageStore: ProjectPartnerDetailPageStore,
              private workPackageStore: WorkPackagePageStore,
              private workPackageProjectStore: ProjectWorkPackagePageStore,
              private projectStore: ProjectStore,
              private router: RoutingService,
              private visibilityStatusService: FormVisibilityStatusService
              ) {
    visibilityStatusService.isVisible$((APPLICATION_FORM.SECTION_B.STATE_AID)).pipe(
      untilDestroyed(this),
      filter(isVisible => !isVisible),
      tap(() => this.router.navigate(['../identity'], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'})),
    ).subscribe();

    this.formService.init(this.form, this.pageStore.isProjectEditable$);
    this.data$ = combineLatest([
      this.pageStore.stateAid$,
      this.projectStore.projectEditable$.pipe(
        switchMap(isEditable => isEditable ? this.projectStore.activities$ : of([]))
      ),
      this.workPackageProjectStore.workPackages$,
      this.projectStore.project$,
      this.projectStore.projectEditable$
    ]).pipe(
      map(([stateAid, activities, workpackages, project, isEditable]) => ({
        stateAid,
        displayActivities: this.mapWorkpackagesAndActivities(isEditable ? activities : stateAid.activities, workpackages),
        activities: isEditable ? activities : stateAid.activities,
        project,
        isEditable,
        stateAidsForDropdown: project.callSettings.stateAids,
      })),
      tap(stateAid => this.resetForm(stateAid.stateAid)),
    );
  }

  updateStateAid(stateAids: ProgrammeStateAidDTO[]): void {
    const stateAidToSave = {
      ...this.form.value,
      stateAidScheme: stateAids.find(stateAid => this.form.controls?.stateAidScheme.value === stateAid.id),
    };
    this.pageStore.partner$
      .pipe(
        take(1),
        switchMap(partner => this.pageStore.updateStateAid(partner.id, stateAidToSave)),
        tap(() => this.formService.setSuccess('project.partner.state.aid.saved')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  activities(): FormArray {
    return this.form.get('activities') as FormArray;
  }

  resetForm(stateAid: ProjectPartnerStateAidDTO): void {
    this.form.reset(stateAid);
    this.activities().clear();
    stateAid?.activities?.forEach((activity: WorkPackageActivitySummaryDTO) => {
      this.addActivity(activity);
    });
    this.form.controls.stateAidScheme.setValue(stateAid.stateAidScheme?.id || 0);
    this.formService.setDirty(false);
  }

  addActivity(activityToAdd: WorkPackageActivitySummaryDTO): void {
    const selectedActivities = this.getCurrentlySelectedActivities().concat(activityToAdd);
    selectedActivities.sort((firstActivity, secondActivity) =>
      firstActivity.workPackageNumber === secondActivity.workPackageNumber ? firstActivity.activityNumber - secondActivity.activityNumber : firstActivity.workPackageNumber - secondActivity.workPackageNumber
    );
    this.activities().clear();
    selectedActivities.forEach(activity => this.activities().push(this.formBuilder.group(activity)));
    this.formService.setDirty(true);
  }

  getCurrentlySelectedActivities(): WorkPackageActivitySummaryDTO[] {
    return (this.activities().value as WorkPackageActivitySummaryDTO[]);
  }

  getWorkpackagesWithActivities(workpackages: ActivityIdentificationInformation[]): ActivityIdentificationInformation[] {
    return workpackages.filter(workpackage => (this.getActivitiesWithoutSelected(workpackage.activities).length) !== 0);
  }

  getActivitiesWithoutSelected(activities: WorkPackageActivitySummaryDTO[]): WorkPackageActivitySummaryDTO[] {
    return activities.filter(activity => (this.getCurrentlySelectedActivities().filter(selectedActivity => selectedActivity.activityId === activity.activityId).length) === 0);
  }

  removeActivity(index: number): void {
    this.activities().removeAt(index);
    this.formService.setDirty(true);
  }

  getDisplayValueForActivityNumber(activities: WorkPackageActivitySummaryDTO[], activity: WorkPackageActivitySummaryDTO): string {
    const foundActivity = activities.find(a => a.activityId === activity.activityId) as WorkPackageActivitySummaryDTO;
    return `ACTIVITY ${foundActivity?.workPackageNumber.toString() || ''}.${foundActivity?.activityNumber.toString() || ''}`;
  }

  getDisplayValueForWorkPackageNumber(workpackage: OutputWorkPackageSimple): string {
    return `WORKPACKAGE ${(workpackage.number.toString() || '')} `;
  }

  getStateAidCheck(): string {
    // if all are answered yes, then there is a risk of state aid
    if (
      this.form.controls.answer1.value === true
      && this.form.controls.answer2.value === true
      && this.form.controls.answer3.value === true
      && this.form.controls.answer4.value === true
    ) {
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
    if (this.isUnset(this.form.controls.answer1.value)
      || this.isUnset(this.form.controls.answer2.value)
      || this.isUnset(this.form.controls.answer3.value)
      || this.isUnset(this.form.controls.answer4.value)) {
      return '';
    }

    // if all are answered and it's not one fo the first two cases, there is no risk of state aid
    return 'project.partner.state.aid.no.risk.of.state.aid';
  }

  updateSelectedScheme(value: number): void {
    this.form.controls.stateAidScheme.setValue(value);
  }

  private mapWorkpackagesAndActivities(activities: WorkPackageActivitySummaryDTO[], workpackages: OutputWorkPackageSimple[]): ActivityIdentificationInformation[] {
    const workpackagesAndActivities: ActivityIdentificationInformation[] = [];
    workpackages.forEach(workpackage => {
      workpackagesAndActivities.push({
        workpackage,
        activities: activities.filter(activity => activity.workPackageNumber === workpackage.number)
      });
    });
    return workpackagesAndActivities;
  }

  private isUnset(value: any): boolean {
    return value === null || value === undefined;
  }
}
