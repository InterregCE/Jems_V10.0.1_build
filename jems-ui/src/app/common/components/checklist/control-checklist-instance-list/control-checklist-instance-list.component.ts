import {ChangeDetectionStrategy, Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {
  ChecklistInstanceDTO,
  ControllerInstitutionsApiService,
  IdNamePairDTO,
  ProgrammeChecklistDetailDTO,
} from '@cat/api';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {
  ControlChecklistInstanceListStore
} from '@common/components/checklist/control-checklist-instance-list/control-checklist-instance-list-store.service';
import {catchError, filter, finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '@common/utils/forms';
import {FormService} from '@common/components/section/form/form.service';
import {TableComponent} from '@common/components/table/table.component';
import {MatSort} from '@angular/material/sort';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';
import {ChecklistUtilsComponent} from '@common/components/checklist/checklist-utils/checklist-utils';
import {ChecklistSort} from '@common/components/checklist/checklist-instance-list/checklist-instance-list-custom-sort';
import {FormBuilder, Validators} from '@angular/forms';
import {ChecklistItem} from '@common/components/checklist/checklist-item';
import {AlertMessage} from '@common/components/file-list/file-list-table/alert-message';
import {Alert} from '@common/components/forms/alert';

@Component({
  selector: 'jems-control-checklist-instance-list',
  templateUrl: './control-checklist-instance-list.component.html',
  styleUrls: ['./control-checklist-instance-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ControlChecklistInstanceListStore, FormService]
})
export class ControlChecklistInstanceListComponent implements OnInit {
  Status = ChecklistInstanceDTO.StatusEnum;
  projectId: number;

  @Input()
  relatedType: ProgrammeChecklistDetailDTO.TypeEnum;
  @Input()
  relatedId: number;

  partnerId = Number(this.routingService.getParameter(this.activatedRoute, 'partnerId'));
  reportId = Number(this.routingService.getParameter(this.activatedRoute, 'reportId'));

  data$: Observable<{ reportId: number }>;

  descriptionForm = this.formBuilder.group({
    id: [null, Validators.required],
    editable: [false],
    description: ['', Validators.maxLength(150)],
  });

  private checklistInstances$: Observable<ChecklistInstanceDTO[]>;
  private checklistInstances: ChecklistInstanceDTO[];
  checklistInstancesSorted$: Observable<ChecklistInstanceDTO[]>;
  checklistTemplates$: Observable<IdNamePairDTO[]>;
  userCanEditControlChecklists$: Observable<boolean>;
  alerts$ = new BehaviorSubject<AlertMessage[]>([]);

  instancesTableConfiguration: TableConfiguration;
  selectedTemplate: IdNamePairDTO;
  checklistUtils: ChecklistUtilsComponent;

  @ViewChild('actionsCell', {static: true})
  actionsCell: TemplateRef<any>;

  @ViewChild('descriptionCell', {static: true})
  descriptionCell: TemplateRef<any>;

  @ViewChild('tableInstances') tableInstances: TableComponent;

  constructor(public pageStore: ControlChecklistInstanceListStore,
              private formService: FormService,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private dialog: MatDialog,
              private controllerInstitutionService: ControllerInstitutionsApiService,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore) {
    this.projectId = this.activatedRoute.snapshot.params.projectId;
    this.checklistUtils = new ChecklistUtilsComponent();
    this.userCanEditControlChecklists$ = this.userCanEditControlChecklists();
    this.data$ = combineLatest([
      this.partnerReportDetailPageStore.partnerReport$,
    ]).pipe(
      map(([report]) => ({reportId: report.reportNumber})),
    );
  }

  onInstancesSortChange(sort: Partial<MatSort>) {
    const order = sort.direction;
    this.pageStore.setInstancesSort({...sort, direction: order === 'desc' ? 'desc' : 'asc'});
  }

  private userCanEditControlChecklists(): Observable<boolean> {
    return this.institutionUserControlReportLevel()
      .pipe(
        map((level) => level === 'Edit')
      );
  }

  private institutionUserControlReportLevel(): Observable<string> {
    return this.controllerInstitutionService.getControllerUserAccessLevelForPartner(this.partnerId);
  }

  ngOnInit(): void {
    this.checklistInstances$ = this.pageStore.controlChecklistInstances(this.partnerId, this.reportId);
    this.checklistInstancesSorted$ = combineLatest([
      this.checklistInstances$,
      this.pageStore.getInstancesSort$,
    ]).pipe(
      map(([checklists, sort]) => [...checklists].sort(ChecklistSort.customSort(sort))),
      tap(data => this.checklistInstances = data)
    );
    this.checklistTemplates$ = this.pageStore.checklistTemplates(this.relatedType);
    this.instancesTableConfiguration = this.checklistUtils.initializeTableConfiguration(this.actionsCell, this.descriptionCell);
  }

  delete(reportId: number, checklist: ChecklistInstanceDTO): void {
    Forms.confirm(
      this.dialog, {
        title: checklist.name,
        message: {i18nKey: 'checklists.instance.delete.confirm', i18nArguments: {name: checklist.name}}
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        switchMap(() => this.pageStore.deleteChecklistInstance(this.partnerId, reportId, checklist.id)),
      ).subscribe();
  }

  createInstance(): void {
    this.pageStore.createInstance(this.partnerId, this.reportId, this.relatedId, this.selectedTemplate.id)
      .pipe(
        tap(instanceId => this.routingService.navigate(
            ['checklist', instanceId],
            {relativeTo: this.activatedRoute}
          )
        )
      ).subscribe();
  }

  isEditable(): boolean {
    return this.formService.isEditable();
  }

  editDescription(checklist: ChecklistItem) {
    this.descriptionForm.patchValue({
      id: checklist.id,
      editable: true,
      description: checklist.description,
    });
  }

  savingDescriptionId$ = new BehaviorSubject<number | null>(null);

  saveDescription() {
    this.savingDescriptionId$.next(this.descriptionForm.value.id);
    this.pageStore.updateInstanceDescription(this.descriptionForm.value.id, this.partnerId, this.reportId, this.descriptionForm.value.description)
      .pipe(
        take(1),
        tap(() => this.savingDescriptionId$.next(null)),
        tap(() => this.showAlert(ChecklistUtilsComponent.successAlert(
          'contracting.monitoring.checklists.description.change.message.success'
        ))),
        tap(() => this.updateChecklistInstancesAfterDescriptionSave(this.descriptionForm.value.id, this.descriptionForm.value.description)),
        catchError(error => {
          this.showAlert(ChecklistUtilsComponent.errorAlert(
            'contracting.monitoring.checklists.description.change.message.failure'
          ));
          throw error;
        }),
        finalize(() => this.savingDescriptionId$.next(null)),
        tap(() => this.descriptionForm.reset()),
      ).subscribe();

    this.descriptionForm.value.editable = false;
  }

  dismissAlert(id: string) {
    const alerts = this.alerts$.value.filter(that => that.id !== id);
    this.alerts$.next(alerts);
  }

  private showAlert(alert: AlertMessage) {
    this.alerts$.next([...this.alerts$.value, alert]);
    setTimeout(
      () => this.dismissAlert(alert.id),
      alert.type === Alert.SUCCESS ? 5000 : 30000);
  }


  private updateChecklistInstancesAfterDescriptionSave(checklistId: number, description: string | undefined) {
    const checklistInstancesIndex = this.checklistInstances.findIndex(c => c.id === checklistId);
    if (checklistInstancesIndex >= 0) {
      this.checklistInstances[checklistInstancesIndex].description = description ?? '';
      this.checklistInstances$ = of(this.checklistInstances);
    }
  }

  resetDescription() {
    this.descriptionForm.value.editable = false;
    this.descriptionForm.reset();
  }
}
