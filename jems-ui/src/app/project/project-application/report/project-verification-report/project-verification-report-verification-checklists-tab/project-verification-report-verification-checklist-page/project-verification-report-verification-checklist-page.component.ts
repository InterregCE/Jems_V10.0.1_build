import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ChecklistComponentInstanceDTO, ChecklistInstanceDetailDTO, ProjectReportDTO} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {FormService} from '@common/components/section/form/form.service';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {PermissionService} from '../../../../../../security/permissions/permission.service';
import {catchError, map, tap} from 'rxjs/operators';
import {ReportUtil} from '@project/common/report-util';
import {
  ProjectReportDetailPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-detail-page-store.service';
import {
  ProjectVerificationReportVerificationChecklistPageStore
} from '@project/project-application/report/project-verification-report/project-verification-report-verification-checklists-tab/project-verification-report-verification-checklist-page/project-verification-report-verification-checklist-page-store.service';
import {
  ProjectReportPageStore
} from '@project/project-application/report/project-report/project-report-page-store.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'jems-project-verification-report-verification-checklist-page',
  templateUrl: './project-verification-report-verification-checklist-page.component.html',
  styleUrls: ['./project-verification-report-verification-checklist-page.component.scss'],
  providers: [ProjectVerificationReportVerificationChecklistPageStore, FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectVerificationReportVerificationChecklistPageComponent {
  Status = ChecklistInstanceDetailDTO.StatusEnum;

  data$: Observable<{
    checklist: ChecklistInstanceDetailDTO;
    editable: boolean;
    reportEditable: boolean;
    isAfterVerificationChecklist: boolean;
    isChecklistCreatedDuringLastReopening: boolean;
  }>;

  confirmFinish = {
    title: 'checklists.instance.confirm.finish.title',
    message: 'checklists.instance.confirm.finish.message'
  };

  confirmReturnToInitiator = {
    title: 'checklists.instance.confirm.return.to.initiator.title',
    message: 'checklists.instance.confirm.return.to.initiator'
  };

  userCanEditControlChecklists$: Observable<boolean>;

  projectId = Number(this.routingService.getParameter(this.activatedRoute, 'projectId'));
  reportId = Number(this.routingService.getParameter(this.activatedRoute, 'reportId'));

  constructor(private projectSidenavService: ProjectApplicationFormSidenavService,
              private pageStore: ProjectVerificationReportVerificationChecklistPageStore,
              private formService: FormService,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute,
              private projectReportDetailPageStore: ProjectReportDetailPageStore,
              private projectReportPageStore: ProjectReportPageStore,
              private permissionService: PermissionService) {
    this.data$ = combineLatest([
      this.pageStore.checklist$,
      this.pageStore.checklistEditable$,
      this.pageStore.reportEditable$,
      this.projectReportDetailPageStore.projectReport$
    ]).pipe(
      map(([checklist, editable, reportEditable, report]) => ({
        checklist,
        editable,
        reportEditable,
        isAfterVerificationChecklist: this.isAfterVerificationChecklist(checklist.createdAt, report.verificationEndDate),
        isChecklistCreatedDuringLastReopening: this.isChecklistCreatedDuringLastReopening(checklist.createdAt, report)
      })),
    );
    this.userCanEditControlChecklists$ = this.userCanEditVerificationChecklists();
  }

  save(checklist: ChecklistInstanceDetailDTO): void {
    checklist.components = this.getFormComponents();
    this.pageStore.updateChecklist(this.projectId, this.reportId, checklist)
      .pipe(
        tap(() => this.formService.setSuccess('checklists.instance.saved.successfully')),
        catchError(err => this.formService.setError(err)),
        untilDestroyed(this)
      ).subscribe();
  }

  updateStatus(checklistId: number, status: ChecklistInstanceDetailDTO.StatusEnum) {
    this.pageStore.changeStatus(this.projectId, this.reportId, checklistId, status)
      .pipe(
        tap(() => this.formService.setDirty(false)),
        tap(() => this.routingService.navigate(['../..'], {relativeTo: this.activatedRoute})),
        catchError(err => this.formService.setError(err)),
        untilDestroyed(this)
      ).subscribe();
  }

  saveDiscardMenuIsActive(): boolean {
    return this.formService.form.dirty;
  }

  private userCanEditVerificationChecklists(): Observable<boolean> {
    return combineLatest([
      this.projectReportDetailPageStore.reportStatus$,
      this.projectReportPageStore.userCanEditVerification$,
    ])
      .pipe(
        map(([reportStatus, canEditVerification]) =>
          (canEditVerification && ReportUtil.isVerificationReportOpen(reportStatus))
          ||
          (canEditVerification && reportStatus === ProjectReportDTO.StatusEnum.Finalized)
        )
      );
  }

  private getFormComponents(): ChecklistComponentInstanceDTO[] {
    return this.formService.form.get('formComponents')?.value;
  }

  private isAfterVerificationChecklist(createdAt: Date, verificationReportVerificationFinalizedDate: Date): boolean {
    if (verificationReportVerificationFinalizedDate === null) {
      return true;
    }
    return createdAt > verificationReportVerificationFinalizedDate;
  }

  private isChecklistCreatedDuringLastReopening(createdAt: Date, report: ProjectReportDTO) {
    if (report.status !== ProjectReportDTO.StatusEnum.ReOpenFinalized) {
      return false;
    }
    return createdAt > report.verificationLastReOpenDate;
  }
}
