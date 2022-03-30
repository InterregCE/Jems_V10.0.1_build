import {ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ProgrammePageSidenavService} from '../programme-page/services/programme-page-sidenav.service';
import {ProgrammeChecklistListPageStore} from './programme-checklist-list-page-store.service';
import {
  ProgrammeChecklistDetailPageStore
} from './programme-checklist-detail-page/programme-checklist-detail-page-store.service';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {ColumnWidth} from '@common/components/table/model/column-width';

@Component({
  selector: 'jems-programme-checklist-list-page',
  templateUrl: './programme-checklist-list-page.component.html',
  styleUrls: ['./programme-checklist-list-page.component.scss'],
  providers: [ProgrammeChecklistListPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeChecklistListPageComponent implements OnInit {
  DETAIL_PATH = ProgrammeChecklistDetailPageStore.CHECKLIST_DETAIL_PATH;

  checklists$ = this.pageStore.checklists$;
  tableConfiguration: TableConfiguration;

  @ViewChild('deleteCell', {static: true})
  deleteCell: TemplateRef<any>;

  constructor(private programmePageSidenavService: ProgrammePageSidenavService,
              private pageStore: ProgrammeChecklistListPageStore) { }

  ngOnInit(): void {
    this.tableConfiguration = new TableConfiguration({
      isTableClickable: true,
      sortable: false,
      routerLink: this.DETAIL_PATH,
      columns: [
        {
          displayedColumn: 'programme.checklists.type',
          elementTranslationKey: 'programme.checklists.type',
          elementProperty: 'type',
          columnWidth: ColumnWidth.DateColumn
        },
        {
          displayedColumn: 'programme.checklists.name',
          elementProperty: 'name',
          columnWidth: ColumnWidth.extraWideColumn
        },
        {
          displayedColumn: 'programme.checklists.modification.date',
          elementProperty: 'lastModificationDate',
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

  delete(id: number): void {
    this.pageStore.deleteChecklist(id).subscribe();
  }
}
