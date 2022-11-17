import {ChangeDetectionStrategy, Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {
  ChecklistInstanceDTO, ControllerInstitutionsApiService,
  IdNamePairDTO,
  ProgrammeChecklistDetailDTO,
} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {ControlChecklistInstanceListStore} from '@common/components/checklist/control-checklist-instance-list/control-checklist-instance-list-store.service';
import {filter, map, switchMap, take, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '@common/utils/forms';
import {FormService} from '@common/components/section/form/form.service';
import {TableComponent} from '@common/components/table/table.component';
import {MatSort} from '@angular/material/sort';
import {ControlChecklistSort} from '@common/components/checklist/control-checklist-instance-list/control-checklist-instance-list-custom-sort';
import {
  PartnerReportDetailPageStore
} from '@project/project-application/report/partner-report-detail-page/partner-report-detail-page-store.service';

@Component({
  selector: 'jems-control-checklist-instance-list',
  templateUrl: './control-checklist-instance-list.component.html',
  styleUrls: ['./control-checklist-instance-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ControlChecklistInstanceListStore, FormService]
})
export class ControlChecklistInstanceListComponent implements OnInit {
  Status = ChecklistInstanceDTO.StatusEnum;

  @Input()
  relatedType: ProgrammeChecklistDetailDTO.TypeEnum;
  @Input()
  relatedId: number;

  partnerId = Number(this.routingService.getParameter(this.activatedRoute, 'partnerId'));
  reportId = Number(this.routingService.getParameter(this.activatedRoute, 'reportId'));

  data$: Observable<{ reportId: number }>;

  private checklistInstances$: Observable<ChecklistInstanceDTO[]>;
  checklistInstancesSorted$: Observable<ChecklistInstanceDTO[]>;
  checklistTemplates$: Observable<IdNamePairDTO[]>;
  userCanEditControlChecklists$: Observable<boolean>;

  instancesTableConfiguration: TableConfiguration;
  selectedTemplate: IdNamePairDTO;

  @ViewChild('visibleCell', {static: true})
  visibleCell: TemplateRef<any>;

  @ViewChild('deleteCell', {static: true})
  deleteCell: TemplateRef<any>;

  @ViewChild('tableInstances') tableInstances: TableComponent;

  constructor(public pageStore: ControlChecklistInstanceListStore,
              private formService: FormService,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute,
              private dialog: MatDialog,
              private controllerInstitutionService: ControllerInstitutionsApiService,
              private partnerReportDetailPageStore: PartnerReportDetailPageStore) {

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
      map(([checklists, sort]) => [...checklists].sort(ControlChecklistSort.customSort(sort))),
    );
    this.checklistTemplates$ = this.pageStore.checklistTemplates(this.relatedType);
    this.instancesTableConfiguration = this.initializeTableConfiguration();
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

  private initializeTableConfiguration(): TableConfiguration {
    return new TableConfiguration({
      isTableClickable: true,
      sortable: false,
      routerLink: 'checklist',
      columns: [
        {
          displayedColumn: 'common.id',
          elementProperty: 'id',
          columnWidth: ColumnWidth.IdColumn,
          sortProperty: 'id',
        },
        {
          displayedColumn: 'common.status',
          elementTranslationKey: 'checklists.instance.status',
          elementProperty: 'status',
          columnWidth: ColumnWidth.DateColumn,
          sortProperty: 'status',
        },
        {
          displayedColumn: 'common.name',
          elementProperty: 'name',
          columnWidth: ColumnWidth.extraWideColumn,
          sortProperty: 'name',
        },
        {
          displayedColumn: 'common.user',
          elementProperty: 'creatorEmail',
          columnWidth: ColumnWidth.WideColumn,
          sortProperty: 'creatorEmail',
        },
        {
          displayedColumn: 'checklists.instance.finished.date',
          elementProperty: 'finishedDate',
          columnType: ColumnType.DateOnlyColumn,
          columnWidth: ColumnWidth.DateColumn,
          sortProperty: 'finishedDate',
        },
        {
          displayedColumn: 'common.delete.entry',
          customCellTemplate: this.deleteCell,
          columnWidth: ColumnWidth.IdColumn,
          clickable: false
        }
      ]
    });
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
}
