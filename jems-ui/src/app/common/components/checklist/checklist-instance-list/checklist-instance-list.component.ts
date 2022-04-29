import {ChangeDetectionStrategy, Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {ChecklistInstanceDTO, IdNamePairDTO, ProgrammeChecklistDetailDTO} from '@cat/api';
import {Observable} from 'rxjs';
import {
  ChecklistInstanceListStore
} from '@common/components/checklist/checklist-instance-list/checklist-instance-list-store.service';
import {filter, switchMap, take, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '@common/utils/forms';

@Component({
  selector: 'jems-checklist-instance-list',
  templateUrl: './checklist-instance-list.component.html',
  styleUrls: ['./checklist-instance-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ChecklistInstanceListStore]
})
export class ChecklistInstanceListComponent implements OnInit {
  Status = ChecklistInstanceDTO.StatusEnum;

  @Input()
  relatedType: ProgrammeChecklistDetailDTO.TypeEnum;
  @Input()
  relatedId: number;

  checklistInstances$: Observable<ChecklistInstanceDTO[]>;
  checklistTemplates$: Observable<IdNamePairDTO[]>;

  tableConfiguration: TableConfiguration;
  selectedTemplate: IdNamePairDTO;

  @ViewChild('deleteCell', {static: true})
  deleteCell: TemplateRef<any>;

  constructor(public pageStore: ChecklistInstanceListStore,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute,
              private dialog: MatDialog) { }

  ngOnInit(): void {
    this.checklistInstances$ = this.pageStore.checklistInstances(this.relatedType, this.relatedId);
    this.checklistTemplates$ = this.pageStore.checklistTemplates(this.relatedType);
    this.initializeTableConfiguration();
  }

  delete(checklist: ChecklistInstanceDTO): void {
    Forms.confirm(
      this.dialog, {
        title: checklist.name,
        message: {i18nKey: 'checklists.instance.delete.confirm', i18nArguments: {name: checklist.name}}
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        switchMap(() => this.pageStore.deleteChecklistInstance(checklist.id)),
      ).subscribe();
  }

  private initializeTableConfiguration(): void {
    this.tableConfiguration = new TableConfiguration({
      isTableClickable: true,
      sortable: false,
      routerLink: 'checklist',
      columns: [
        {
          displayedColumn: 'common.id',
          elementProperty: 'id',
          columnWidth: ColumnWidth.IdColumn
        },
        {
          displayedColumn: 'common.status',
          elementTranslationKey: 'checklists.instance.status',
          elementProperty: 'status',
          columnWidth: ColumnWidth.DateColumn
        },
        {
          displayedColumn: 'common.type',
          elementTranslationKey: 'programme.checklists.type',
          elementProperty: 'type',
          columnWidth: ColumnWidth.DateColumn
        },
        {
          displayedColumn: 'common.name',
          elementProperty: 'name',
          columnWidth: ColumnWidth.extraWideColumn
        },
        {
          displayedColumn: 'checklists.instance.assessor',
          elementProperty: 'creatorEmail',
          columnWidth: ColumnWidth.DateColumn
        },
        {
          displayedColumn: 'checklists.instance.finished.date',
          elementProperty: 'finishedDate',
          columnType: ColumnType.DateColumn,
          columnWidth: ColumnWidth.DateColumn
        },
        {
          displayedColumn: 'common.delete.entry',
          customCellTemplate: this.deleteCell,
          columnWidth: ColumnWidth.IdColumn
        }
      ]
    });
  }

  createInstance(): void {
    this.pageStore.createInstance(this.relatedType, this.relatedId, this.selectedTemplate.id)
      .pipe(
        tap(instanceId => this.routingService.navigate(
          ['checklist', instanceId],
          {relativeTo: this.activatedRoute}
          )
        )
      ).subscribe();
  }
}
