import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectStatusDTO, UserRoleDTO} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {APIError} from '@common/models/APIError';
import {TranslateService} from '@ngx-translate/core';
import {catchError, finalize, map, tap} from 'rxjs/operators';
import * as moment from 'moment';
import {Alert} from '@common/components/forms/alert';
import {PreConditionCheckResult} from '@project/model/plugin/PreConditionCheckResult';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {Router} from '@angular/router';
import {CheckAndSubmitStore} from '@project/project-application/check-and-submit/check-and-submit-store.service';
import {ProjectUtil} from '@project/common/project-util';

@Component({
  selector: 'jems-check-and-submit',
  templateUrl: './check-and-submit.component.html',
  styleUrls: ['./check-and-submit.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CheckAndSubmitComponent {
  Alert = Alert;
  PermissionsEnum = UserRoleDTO.PermissionsEnum;
  STATUS = ProjectStatusDTO.StatusEnum;
  ProjectUtil = ProjectUtil;

  private preConditionCheckResult$ = new BehaviorSubject<PreConditionCheckResult | null>(null);

  data$: Observable<{
    currentVersionOfProjectStatus: ProjectStatusDTO.StatusEnum;
    currentVersionOfProjectTitle: string;
    projectId: number;
    projectCallEndDate: Date;
    projectCallEndDateStep1: Date;
    isCall2Step: boolean;
    isThisUserOwner: boolean;
    userIsProjectOwnerOrEditCollaborator: boolean;
    hasPreConditionCheckSucceed: boolean;
    preConditionCheckResults: PreConditionCheckResult | null;
  }>;

  // TODO: create a component
  error$ = new BehaviorSubject<APIError | null>(null);
  actionPending = false;
  preConditionCheckInProgress = false;

  constructor(public translate: TranslateService,
              private checkAndSubmitStore: CheckAndSubmitStore,
              private projectStore: ProjectStore,
              private router: Router
  ) {
    this.data$ = combineLatest([
      this.projectStore.currentVersionOfProject$,
      this.projectStore.currentVersionOfProjectTitle$,
      this.projectStore.userIsProjectOwner$,
      this.projectStore.userIsProjectOwnerOrEditCollaborator$,
      this.preConditionCheckResult$
    ]).pipe(
      map(([currentVersionOfProject, currentVersionOfProjectTitle, isThisUserOwner,userIsProjectOwnerOrEditCollaborator, preConditionCheckResults]) => ({
        currentVersionOfProjectTitle,
        currentVersionOfProjectStatus: currentVersionOfProject.projectStatus.status,
        projectId: currentVersionOfProject.id,
        projectCallEndDate: currentVersionOfProject.callSettings?.endDate,
        projectCallEndDateStep1: currentVersionOfProject.callSettings?.endDateStep1,
        isCall2Step: currentVersionOfProject.callSettings?.endDateStep1 !== null,
        isThisUserOwner,
        userIsProjectOwnerOrEditCollaborator,
        hasPreConditionCheckSucceed: preConditionCheckResults?.submissionAllowed || false,
        preConditionCheckResults,
      }))
    );
  }

  preConditionCheck(projectId: number): void {
    this.preConditionCheckInProgress = true;
    this.checkAndSubmitStore.preConditionCheck(projectId).pipe(
      tap(result => this.preConditionCheckResult$.next(result)),
      catchError((error) => this.showErrorMessage(error.error)),
      finalize(() => this.preConditionCheckInProgress = false)
    ).subscribe();
  }

  submitProject(projectId: number): void {
    this.actionPending = true;
    this.checkAndSubmitStore.submitApplication(projectId)
      .pipe(
        tap(() => this.redirectToProjectOverview(projectId)),
        catchError((error) => this.showErrorMessage(error.error)),
        finalize(() => this.actionPending = false)
      ).subscribe();
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      this.error$.next(null);
    },         4000);
    return of(null);
  }

  private redirectToProjectOverview(projectId: number): void {
    this.router.navigate([`/app/project/detail/${projectId}`]);
  }

  isCallEnded(projectCallEndDate: Date): boolean {
    const currentDate = moment(new Date());
    return !(currentDate.isBefore(projectCallEndDate));
  }

  isSubmitDisabled(projectCallEndDate: Date, hasPreConditionCheckSucceed: boolean, projectStatus: ProjectStatusDTO.StatusEnum): boolean {
    const currentDate = moment(new Date());
    return !(currentDate.isBefore(projectCallEndDate) && hasPreConditionCheckSucceed);
  }

  showCallEndedMessage(endCallStep1: Date, endCall: Date, callStatus: ProjectStatusDTO): boolean {
    const callClosedStep1 = this.isCallEnded(endCallStep1) && ProjectUtil.isStep1Draft(callStatus);
    const callClosed = this.isCallEnded(endCall) && ProjectUtil.isDraft(callStatus);

    return callClosedStep1 || callClosed;
  }

  showPreSubmissionCheckMessage(isCall2Step: boolean, checkSucceed: boolean, endCallStep1: Date, endCall: Date, callStatus: ProjectStatusDTO): boolean {
    const checkFailed = !checkSucceed;
    const submissionOpenStep1 = !this.isCallEnded(endCallStep1) && ProjectUtil.isStep1Draft(callStatus);
    const submissionOpen = !this.isCallEnded(endCall) && ProjectUtil.isDraft(callStatus);
    const projectCanBeSubmittedAfterCallEnded = ProjectUtil.isReturnedToApplicant(callStatus);

    if (isCall2Step){
      return checkFailed && (submissionOpenStep1 || submissionOpen || projectCanBeSubmittedAfterCallEnded);
    }
    return checkFailed && (submissionOpen || projectCanBeSubmittedAfterCallEnded);
  }
}
