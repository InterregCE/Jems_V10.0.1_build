import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {
  AssessmentAndDecisionChecklistPageStore
} from '@project/project-application/assessment-and-decision/assessment-and-decision-checklist-page/assessment-and-decision-checklist-page-store.service';
import {ChecklistComponentInstanceDTO, ChecklistInstanceDetailDTO} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'jems-assessment-and-decision-checklist-page',
  templateUrl: './assessment-and-decision-checklist-page.component.html',
  styleUrls: ['./assessment-and-decision-checklist-page.component.scss'],
  providers: [AssessmentAndDecisionChecklistPageStore, FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AssessmentAndDecisionChecklistPageComponent {

  checklist$ = this.pageStore.checklist$;

  constructor(private projectSidenavService: ProjectApplicationFormSidenavService,
              private pageStore: AssessmentAndDecisionChecklistPageStore,
              private formService: FormService,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute) { }

  save(components: ChecklistComponentInstanceDTO[], checklist: ChecklistInstanceDetailDTO): void {
    checklist.components = components;
    this.pageStore.updateChecklist(checklist)
      .pipe(
        tap(() => this.formService.setSuccess('checklists.instance.saved.successfully'))
      ).subscribe();
  }

  finish(components: ChecklistComponentInstanceDTO[], checklist: ChecklistInstanceDetailDTO) {
    checklist.components = components;
    checklist.status = ChecklistInstanceDetailDTO.StatusEnum.FINISHED;
    this.pageStore.updateChecklist(checklist)
      .pipe(
        tap(() => this.formService.setDirty(false)),
        tap(() => this.routingService.navigate(['../..'], {relativeTo: this.activatedRoute}))
      ).subscribe();
  }
}
