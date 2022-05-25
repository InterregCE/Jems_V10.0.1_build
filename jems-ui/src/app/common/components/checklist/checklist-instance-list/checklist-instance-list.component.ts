import {ChangeDetectionStrategy, Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {
  ChecklistInstanceDTO,
  ChecklistInstanceSelectionDTO,
  IdNamePairDTO,
  ProgrammeChecklistDetailDTO,
  UserRoleDTO
} from '@cat/api';
import {Observable} from 'rxjs';
import {
  ChecklistInstanceListStore
} from '@common/components/checklist/checklist-instance-list/checklist-instance-list-store.service';
import {filter, switchMap, take, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '@common/utils/forms';
import {FormService} from '@common/components/section/form/form.service';
import {FormArray, FormBuilder, FormControl} from '@angular/forms';

@Component({
  selector: 'jems-checklist-instance-list',
  templateUrl: './checklist-instance-list.component.html',
  styleUrls: ['./checklist-instance-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ChecklistInstanceListStore, FormService]
})
export class ChecklistInstanceListComponent implements OnInit {
  Status = ChecklistInstanceDTO.StatusEnum;
  PermissionEnum = UserRoleDTO.PermissionsEnum;

  @Input()
  relatedType: ProgrammeChecklistDetailDTO.TypeEnum;
  @Input()
  relatedId: number;

  form = this.formBuilder.group({
    visibilities: this.formBuilder.array([])
  });

  checklistInstances$: Observable<ChecklistInstanceDTO[]>;
  checklistTemplates$: Observable<IdNamePairDTO[]>;
  selectedChecklists$: Observable<ChecklistInstanceSelectionDTO[]>;

  instancesTableConfiguration: TableConfiguration;
  selectionTableConfiguration: TableConfiguration;
  selectedTemplate: IdNamePairDTO;

  @ViewChild('consolidateCell', {static: true})
  consolidateCell: TemplateRef<any>;

  @ViewChild('visibleCell', {static: true})
  visibleCell: TemplateRef<any>;

  @ViewChild('deleteCell', {static: true})
  deleteCell: TemplateRef<any>;

  constructor(public pageStore: ChecklistInstanceListStore,
              private formService: FormService,
              private formBuilder: FormBuilder,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute,
              private dialog: MatDialog) { }

  ngOnInit(): void {
    this.formService.init(this.form, this.pageStore.userCanChangeSelection$);
    this.checklistInstances$ = this.pageStore.checklistInstances(this.relatedType, this.relatedId);
    this.checklistTemplates$ = this.pageStore.checklistTemplates(this.relatedType);
    this.selectedChecklists$ = this.pageStore.selectedInstances(this.relatedType, this.relatedId)
      .pipe(
        tap(checklists => this.resetForm(checklists)),
      );

    this.instancesTableConfiguration = this.initializeTableConfiguration(false);
    this.selectionTableConfiguration = this.initializeTableConfiguration(true);
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

  private initializeTableConfiguration(selection: boolean): TableConfiguration {
    return new TableConfiguration({
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
          displayedColumn: 'checklists.instance.consolidated',
          customCellTemplate: this.consolidateCell,
          columnWidth: ColumnWidth.DateColumn
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
        ...!selection ? [{
          displayedColumn: 'checklists.instance.assessor',
          elementProperty: 'creatorEmail',
          columnWidth: ColumnWidth.DateColumn
        }] : [],
        {
          displayedColumn: 'checklists.instance.finished.date',
          elementProperty: 'finishedDate',
          columnType: ColumnType.DateOnlyColumn,
          columnWidth: ColumnWidth.DateColumn
        },
        ...selection ? [{
          displayedColumn: 'checklists.instance.visible',
          customCellTemplate: this.visibleCell,
          columnWidth: ColumnWidth.DateColumn,
          infoMessage:'checklists.instance.visible.tooltip',
          clickable: false
        }
        ] : [{
          displayedColumn: 'common.delete.entry',
          customCellTemplate: this.deleteCell,
          columnWidth: ColumnWidth.IdColumn,
          clickable: false
        }]
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

  save(): void {
    this.pageStore.setVisibilities(
      this.visibilities.controls.reduce(
        (map: {[index: number]: any} , obj) => {
          map[(obj.get('id')?.value)] = (obj.get('visible')?.value);
          return map;
        }, {}
      )
    ).pipe(
      tap(() => this.formService.setSuccess('checklists.instances.list.saved.successfully'))
    ).subscribe();
  }

  get visibilities(): FormArray {
    return this.form.get('visibilities') as FormArray;
  }

  getVisibleGroup(instance: ChecklistInstanceSelectionDTO): FormControl {
    return this.visibilities.controls
      .find(control => control.get('id')?.value === instance.id)
      ?.get('visible') as FormControl;
  }

  resetForm(instances: ChecklistInstanceSelectionDTO[]): void {
    this.visibilities.clear();
    instances.forEach(instance => this.visibilities.push(this.formBuilder.group({
      id: [instance.id],
      visible: [instance.visible],
    })));
    this.formService.resetEditable();
  }
}
