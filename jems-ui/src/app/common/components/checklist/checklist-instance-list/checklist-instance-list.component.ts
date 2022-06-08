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
import {combineLatest, Observable} from 'rxjs';
import {
  ChecklistInstanceListStore
} from '@common/components/checklist/checklist-instance-list/checklist-instance-list-store.service';
import {filter, map, switchMap, take, tap} from 'rxjs/operators';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '@common/utils/forms';
import {FormService} from '@common/components/section/form/form.service';
import {FormArray, FormBuilder, FormControl} from '@angular/forms';
import {TableComponent} from '@common/components/table/table.component';
import {MatSort} from '@angular/material/sort';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {ChecklistSort} from '@common/components/checklist/checklist-instance-list/checklist-instance-list-custom-sort';

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

  private checklistInstances$: Observable<ChecklistInstanceDTO[]>;
  checklistInstancesSorted$: Observable<ChecklistInstanceDTO[]>;
  checklistTemplates$: Observable<IdNamePairDTO[]>;
  private selectedChecklists$: Observable<ChecklistInstanceSelectionDTO[]>;
  selectedChecklistsSorted$: Observable<ChecklistInstanceSelectionDTO[]>;

  instancesTableConfiguration: TableConfiguration;
  selectionTableConfiguration: TableConfiguration;
  selectedTemplate: IdNamePairDTO;

  @ViewChild('consolidateCell', {static: true})
  consolidateCell: TemplateRef<any>;

  @ViewChild('visibleCell', {static: true})
  visibleCell: TemplateRef<any>;

  @ViewChild('deleteCell', {static: true})
  deleteCell: TemplateRef<any>;

  @ViewChild('tableInstances') tableInstances: TableComponent;
  @ViewChild('tableSelected') tableSelected: TableComponent;

  constructor(public pageStore: ChecklistInstanceListStore,
              private formService: FormService,
              private formBuilder: FormBuilder,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute,
              private dialog: MatDialog) { }

  onInstancesSortChange(sort: Partial<MatSort>) {
    const field = sort.active || '';
    const order = sort.direction;

    if (this.tableSelected) {
      const oldField = this.tableSelected.matSort.active;
      const oldOrder = this.tableSelected.matSort.direction === 'desc' ? 'desc' : 'asc';

      if (field !== oldField || (field === oldField && order !== oldOrder)) {
        this.tableSelected.matSort.sort({id: field, start: 'asc', disableClear: true});
      }
    }

    this.pageStore.setInstancesSort({...sort, direction: order === 'desc' ? 'desc' : 'asc'});
  }

  onSelectedSortChange(sort: Partial<MatSort>) {
    const field = sort.active || '';
    const order = sort.direction;

    if (this.tableInstances) {
      const oldField = this.tableInstances.matSort.active;
      const oldOrder = this.tableInstances.matSort.direction === 'desc' ? 'desc' : 'asc';

      if (field !== oldField || (field === oldField && order !== oldOrder)) {
        this.tableInstances.matSort.sort({id: field, start: 'asc', disableClear: true});
      }
    }

    this.pageStore.setSelectedSort({...sort, direction: order === 'desc' ? 'desc' : 'asc'});
  }

  ngOnInit(): void {
    this.formService.init(this.form, this.pageStore.userCanChangeSelection$);
    this.checklistInstances$ = this.pageStore.checklistInstances(this.relatedType, this.relatedId);
    this.checklistInstancesSorted$ = combineLatest([
      this.checklistInstances$,
      this.pageStore.getInstancesSort$,
    ]).pipe(
      map(([checklists, sort]) => [...checklists].sort(ChecklistSort.customSort(sort))),
    );
    this.checklistTemplates$ = this.pageStore.checklistTemplates(this.relatedType);
    this.selectedChecklists$ = this.pageStore.selectedInstances(this.relatedType, this.relatedId)
      .pipe(
        tap(checklists => this.resetForm(checklists)),
      );
    this.selectedChecklistsSorted$ = combineLatest([
      this.selectedChecklists$,
      this.pageStore.getSelectedSort$,
    ]).pipe(
      map(([checklists, sort]) => [...checklists].sort(ChecklistSort.customSort(sort))),
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
          columnWidth: ColumnWidth.IdColumn,
          sortProperty: 'id',
        },
        {
          displayedColumn: 'checklists.instance.consolidated',
          customCellTemplate: this.consolidateCell,
          sortProperty: 'consolidated',
        },
        {
          displayedColumn: 'common.status',
          elementTranslationKey: 'checklists.instance.status',
          elementProperty: 'status',
          columnWidth: ColumnWidth.DateColumn,
          sortProperty: 'status',
        },
        {
          displayedColumn: 'common.type',
          elementTranslationKey: 'programme.checklists.type',
          elementProperty: 'type',
          columnWidth: ColumnWidth.DateColumn,
        },
        {
          displayedColumn: 'common.name',
          elementProperty: 'name',
          columnWidth: ColumnWidth.extraWideColumn,
          sortProperty: 'name',
        },
        ...!selection ? [{
          displayedColumn: 'checklists.instance.assessor',
          elementProperty: 'creatorEmail',
          columnWidth: ColumnWidth.WideColumn,
          sortProperty: 'creatorEmail',
        }] : [],
        {
          displayedColumn: 'checklists.instance.finished.date',
          elementProperty: 'finishedDate',
          columnType: ColumnType.DateOnlyColumn,
          columnWidth: ColumnWidth.DateColumn,
          sortProperty: 'finishedDate',
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

  save(original: ChecklistInstanceSelectionDTO[]): void {
    const allIds = original.map(ch => ch.id);
    const visibilities = allIds.reduce((resultObject: {[index: number]: any}, id) => {
      resultObject[id] = this.visibilities.value.includes(id);
      return resultObject;
    }, {});
    this.pageStore.setVisibilities(visibilities)
      .pipe(
        tap(() => this.formService.setSuccess('checklists.instances.list.saved.successfully'))
      ).subscribe();
  }

  resetForm(instances: ChecklistInstanceSelectionDTO[]): void {
    this.visibilities.clear();
    instances.filter(instance => instance.visible).forEach(instance => {
      this.visibilities.push(this.formBuilder.control(instance.id));
    });
    this.formService.resetEditable();
  }

  onVisibilityChange(change: MatCheckboxChange, id: number) {
    if (change.checked) {
      this.visibilities.push(this.formBuilder.control(id));
      this.formService.setDirty(true);
    } else {
      const index = this.visibilities.value.indexOf(id);
      if (index > -1) {
        this.visibilities.removeAt(index);
        this.formService.setDirty(true);
      }
    }
  }

  get visibilities(): FormArray {
    return this.form.get('visibilities') as FormArray;
  }

}
