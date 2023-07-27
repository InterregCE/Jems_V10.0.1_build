import {Injectable} from '@angular/core';
import {RoutingService} from '@common/services/routing.service';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {
  ChecklistInstanceDetailDTO,
  ChecklistInstanceDTO,
  VerificationChecklistInstanceService
} from '@cat/api';
import {map, shareReplay, switchMap, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {SecurityService} from '../../../../../../security/security.service';
import {ActivatedRoute} from '@angular/router';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {
  ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';

@Injectable()
export class ProjectVerificationReportVerificationChecklistPageStore {
  static CHECKLIST_DETAIL_PATH = `verificationReport/verificationChecklistsTab/checklist/`;

  checklist$: Observable<ChecklistInstanceDetailDTO>;
  checklistEditable$: Observable<boolean>;
  reportEditable$: Observable<boolean>;

  projectId = Number(this.routingService.getParameter(this.activatedRoute, 'projectId'));
  reportId = Number(this.routingService.getParameter(this.activatedRoute, 'reportId'));

  private updatedChecklist$ = new Subject<ChecklistInstanceDetailDTO>();

  constructor(private routingService: RoutingService,
              private verificationChecklistInstanceService: VerificationChecklistInstanceService,
              private securityService: SecurityService,
              private activatedRoute: ActivatedRoute,
              private reportStore: ProjectReportDetailPageStore,
              private projectReportPageStore: ProjectReportPageStore,) {
    this.checklist$ = this.checklist();
    this.checklistEditable$ = this.checklistEditable();
    this.reportEditable$ = this.reportStore.reportEditable$;
  }

  updateChecklist(projectId: number, reportId: number, checklist: ChecklistInstanceDetailDTO): Observable<ChecklistInstanceDetailDTO> {
    return this.verificationChecklistInstanceService.updateVerificationChecklistInstance(projectId, reportId, checklist)
      .pipe(
        take(1),
        tap(() => this.updatedChecklist$.next(checklist)),
        tap(updated => Log.info('Updated verification checklist instance', this, updated))
      );
  }

  changeStatus(projectId: number, reportId: number, checklistId: number, status: ChecklistInstanceDTO.StatusEnum): Observable<ChecklistInstanceDTO> {
    return this.verificationChecklistInstanceService.changeVerificationChecklistStatus(checklistId, projectId, reportId,  status)
      .pipe(
        take(1),
        tap(updated => Log.info('Changed verification checklist status', this, updated))
      );
  }

  private checklist(): Observable<ChecklistInstanceDetailDTO> {
    const initialChecklist$ = combineLatest([
      this.routingService.routeParameterChanges(ProjectVerificationReportVerificationChecklistPageStore.CHECKLIST_DETAIL_PATH, 'checklistId')
      ]
    ).pipe(
        switchMap(([ checklistId]) => {
          return this.verificationChecklistInstanceService.getVerificationChecklistInstanceDetail(checklistId as number, this.projectId, this.reportId);
        }),
        tap(checklist => Log.info('Fetched the verification checklist instance', this, checklist))
      );

    return merge(initialChecklist$, this.updatedChecklist$)
      .pipe(
        tap(checklist => checklist.components.sort((a, b) => a.position - b.position)),
        shareReplay()
      );
  }

  private checklistEditable(): Observable<boolean> {
    return combineLatest([
        this.checklist$,
        this.securityService.currentUserDetails,
        this.projectReportPageStore.userCanEditVerification$,
        this.reportStore.projectReport$,
    ])
      .pipe(
        map(([
            checklist,
             user,
             reportEditable,
             report,
         ]) =>
             checklist.status === ChecklistInstanceDetailDTO.StatusEnum.DRAFT
             && user?.email === checklist.creatorEmail
             && (reportEditable || (checklist.createdAt > report.verificationEndDate))
        )
      );
  }
}
