import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {ChecklistComponentInstanceDTO, ChecklistInstanceDetailDTO} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {catchError, map, tap} from 'rxjs/operators';
import {
  ProjectReportProjectClosureChecklistPageStore
} from '@project/project-application/report/project-report/project-report-detail-page/project-report-project-closure-tab/project-report-project-closure-checklist-page/project-report-project-closure-checklist-page.store';

@Component({
  selector: 'jems-project-report-project-closure-checklist-page',
  templateUrl: './project-report-project-closure-checklist-page.component.html',
  styleUrls: ['./project-report-project-closure-checklist-page.component.scss'],
  providers: [ProjectReportProjectClosureChecklistPageStore, FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectReportProjectClosureChecklistPageComponent {
  Status = ChecklistInstanceDetailDTO.StatusEnum;

  data$: Observable<{
    checklist: ChecklistInstanceDetailDTO;
    editable: boolean;
    reportEditable: boolean;
  }>;

  confirmFinish = {
    title: 'checklists.instance.confirm.finish.title',
    message: 'checklists.instance.confirm.finish.message'
  };

  confirmReturnToInitiator = {
    title: 'checklists.instance.confirm.return.to.initiator.title',
    message: 'checklists.instance.confirm.return.to.initiator'
  };

  projectId = Number(this.routingService.getParameter(this.activatedRoute, 'projectId'));
  reportId = Number(this.routingService.getParameter(this.activatedRoute, 'reportId'));

  constructor(private projectSidenavService: ProjectApplicationFormSidenavService,
              private pageStore: ProjectReportProjectClosureChecklistPageStore,
              private formService: FormService,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute,
  ) {
    this.data$ = combineLatest([
      this.pageStore.checklist$,
      this.pageStore.checklistEditable$,
      this.pageStore.reportEditable$,
    ]).pipe(
      map(([checklist, editable, reportEditable]) => ({
        checklist,
        editable,
        reportEditable,
      })),
    );
  }

  save(checklist: ChecklistInstanceDetailDTO): void {
    checklist.components = this.getFormComponents();
    this.pageStore.updateChecklist(this.projectId, this.reportId, checklist)
      .pipe(
        tap(() => this.formService.setSuccess('checklists.instance.saved.successfully')),
        catchError(err => this.formService.setError(err)),
      ).subscribe();
  }

  updateStatus(checklistId: number, status: ChecklistInstanceDetailDTO.StatusEnum) {
    this.pageStore.changeStatus(this.projectId, this.reportId, checklistId, status)
      .pipe(
        tap(() => this.formService.setDirty(false)),
        tap(() => this.routingService.navigate(['../..'], {relativeTo: this.activatedRoute})),
        catchError(err => this.formService.setError(err)),
      ).subscribe();
  }

  saveDiscardMenuIsActive(): boolean {
    return this.formService.form.dirty;
  }

  private getFormComponents(): ChecklistComponentInstanceDTO[] {
    return this.formService.form.get('formComponents')?.value;
  }
}
