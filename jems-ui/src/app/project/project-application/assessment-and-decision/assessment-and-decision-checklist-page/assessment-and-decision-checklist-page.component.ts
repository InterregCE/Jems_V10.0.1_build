import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {
  AssessmentAndDecisionChecklistPageStore
} from '@project/project-application/assessment-and-decision/assessment-and-decision-checklist-page/assessment-and-decision-checklist-page-store.service';
import {ChecklistComponentInstanceDTO, ChecklistInstanceDetailDTO} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {map, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {combineLatest, Observable} from 'rxjs';
import {Alert} from '@common/components/forms/alert';

@Component({
  selector: 'jems-assessment-and-decision-checklist-page',
  templateUrl: './assessment-and-decision-checklist-page.component.html',
  styleUrls: ['./assessment-and-decision-checklist-page.component.scss'],
  providers: [AssessmentAndDecisionChecklistPageStore, FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AssessmentAndDecisionChecklistPageComponent {
  Alert = Alert;

  Status = ChecklistInstanceDetailDTO.StatusEnum;
  isConsolidated: boolean;

  data$: Observable<{
    checklist: ChecklistInstanceDetailDTO;
    editable: boolean;
    userCanConsolidate: boolean;
  }>;

  confirmFinish = {
    title: 'checklists.instance.confirm.finish.title',
    message: 'checklists.instance.confirm.finish.message'
  };

  confirmDraft = {
    title: 'checklists.instance.confirm.return.to.user.title',
    message: 'checklists.instance.confirm.return.to.user'
  };

  constructor(private projectSidenavService: ProjectApplicationFormSidenavService,
              private pageStore: AssessmentAndDecisionChecklistPageStore,
              private formService: FormService,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute) {
    this.data$ = combineLatest([
      this.pageStore.checklist$,
      this.pageStore.checklistEditable$,
      this.pageStore.userCanConsolidate$
    ]).pipe(
      map(([checklist, editable, userCanConsolidate]) => ({checklist, editable, userCanConsolidate})),
      tap(data => this.isConsolidated = data.checklist.consolidated)
    );
  }

  save(checklist: ChecklistInstanceDetailDTO): void {
    checklist.components = this.getFormComponents();
    this.pageStore.updateChecklist(checklist)
      .pipe(
        tap(() => this.formService.setSuccess('checklists.instance.saved.successfully'))
      ).subscribe();
  }


  updateStatus(checklistId: number, status: ChecklistInstanceDetailDTO.StatusEnum) {
    this.pageStore.changeStatus(checklistId, status)
      .pipe(
        tap(() => this.formService.setDirty(false)),
        tap(() => this.routingService.navigate(['../..'], {relativeTo: this.activatedRoute}))
      ).subscribe();
  }

  private getFormComponents(): ChecklistComponentInstanceDTO[] {
    return this.formService.form.get('formComponents')?.value;
  }

  updateConsolidatedFlag(consolidated: boolean) {
    this.isConsolidated = consolidated;
  }
}
