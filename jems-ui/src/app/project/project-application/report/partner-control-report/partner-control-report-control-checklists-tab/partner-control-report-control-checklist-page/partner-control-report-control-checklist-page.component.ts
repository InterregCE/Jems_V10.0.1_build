import {ChangeDetectionStrategy, Component} from '@angular/core';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {
  PartnerControlReportControlChecklistPageStore
} from '@project/project-application/report/partner-control-report/partner-control-report-control-checklists-tab/partner-control-report-control-checklist-page/partner-control-report-control-checklist-page-store.service';
import {ChecklistComponentInstanceDTO, ChecklistInstanceDetailDTO} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {map, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {combineLatest, Observable} from 'rxjs';

@Component({
  selector: 'jems-partner-control-report-control-checklist-page',
  templateUrl: './partner-control-report-control-checklist-page.component.html',
  styleUrls: ['./partner-control-report-control-checklist-page.component.scss'],
  providers: [PartnerControlReportControlChecklistPageStore, FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PartnerControlReportControlChecklistPageComponent {
  Status = ChecklistInstanceDetailDTO.StatusEnum;

  data$: Observable<{
    checklist: ChecklistInstanceDetailDTO;
    editable: boolean;
  }>;

  confirmFinish = {
    title: 'checklists.instance.confirm.finish.title',
    message: 'checklists.instance.confirm.finish.message'
  };

  confirmUnfinish = {
    title: 'checklists.instance.confirm.unfinish.title',
    message: 'checklists.instance.confirm.unfinish.message'
  };

  constructor(private projectSidenavService: ProjectApplicationFormSidenavService,
              private pageStore: PartnerControlReportControlChecklistPageStore,
              private formService: FormService,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute) {
    this.data$ = combineLatest([
      this.pageStore.checklist$,
      this.pageStore.checklistEditable$,
    ]).pipe(
      map(([checklist, editable]) => ({checklist, editable})),
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

  saveDiscardMenuIsActive(): boolean {
    return this.formService.form.dirty;
  }
}
