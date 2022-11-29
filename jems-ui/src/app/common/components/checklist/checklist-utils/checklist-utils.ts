import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {Component, TemplateRef} from '@angular/core';
import {MatSort} from '@angular/material/sort';
import {TableComponent} from '@common/components/table/table.component';
import {
    ChecklistInstanceListStore
} from '@common/components/checklist/checklist-instance-list/checklist-instance-list-store.service';
import {
    ContractingChecklistInstanceListStore
} from '@common/components/checklist/contracting-checklist-instance-list/contracting-checklist-instance-list-store.service';
import {AlertMessage} from '@common/components/file-list/file-list-table/alert-message';
import {v4 as uuid} from 'uuid';
import {Alert} from '@common/components/forms/alert';

@Component({template: ``})
export class ChecklistUtilsComponent {

  static successAlert(msg: string): AlertMessage {
    return {
      id: uuid(),
      type: Alert.SUCCESS,
      i18nMessage: msg,
      i18nArgs: {}
    };
  }

  static errorAlert(msg: string): AlertMessage {
    return {
      id: uuid(),
      type: Alert.ERROR,
      i18nMessage: msg,
      i18nArgs: {}
    };
  }

  onInstancesSortChange(sort: Partial<MatSort>,
                        tableSelected: TableComponent,
                        pageStore: ChecklistInstanceListStore | null,
                        alternatePageStore: ContractingChecklistInstanceListStore | null) {
      const field = sort.active || '';
      const order = sort.direction;
      const direction = order === 'desc' ? 'desc' : 'asc';

      if (tableSelected) {
          const oldField = tableSelected.matSort.active;
          const oldOrder = tableSelected.matSort.direction === 'desc' ? 'desc' : 'asc';

          if (field !== oldField || (field === oldField && order !== oldOrder)) {
              tableSelected.matSort.sort({id: field, start: 'asc', disableClear: true});
          }
      }

      if (alternatePageStore == null && pageStore != null) {
          pageStore.setInstancesSort({...sort, direction});
      } else if (alternatePageStore != null) {
          alternatePageStore.setInstancesSort({...sort, direction});
      }
  }

  initializeTableConfiguration(actions: TemplateRef<any>, description: TemplateRef<any>): TableConfiguration {
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
          columnWidth: ColumnWidth.WideColumn,
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
          displayedColumn: 'file.table.column.name.description',
          customCellTemplate: description,
          columnWidth: ColumnWidth.extraWideColumn,
          sortProperty: 'description'
        },
        {
          displayedColumn: 'file.table.column.name.action',
          customCellTemplate: actions,
          columnWidth: ColumnWidth.SmallColumn,
          clickable: false
        }
      ]
    });
  }
}
