import {ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ProgrammePageSidenavService} from '../../programme-page/services/programme-page-sidenav.service';
import {ProgrammePrioritiesPageStore} from './programme-priorities-page-store.service';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnType} from '@common/components/table/model/column-type.enum';

@Component({
  selector: 'app-programme-priority-list-page',
  templateUrl: './programme-priority-list-page.component.html',
  styleUrls: ['./programme-priority-list-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [ProgrammePrioritiesPageStore]
})
export class ProgrammePriorityListPageComponent implements OnInit {

  @ViewChild('specificObjective', {static: true})
  specificObjective: TemplateRef<any>;

  tableConfiguration: TableConfiguration;

  constructor(private programmePageSidenavService: ProgrammePageSidenavService,
              public prioritiesPageStore: ProgrammePrioritiesPageStore) {
  }

  ngOnInit(): void {
    this.tableConfiguration = new TableConfiguration({
      routerLink: '/app/programme/priorities',
      isTableClickable: true,
      sortable: false,
      columns: [
        {
          displayedColumn: 'programme.priority.list.column.code',
          elementProperty: 'code'
        },
        {
          displayedColumn: 'programme.priority.list.column.title',
          elementProperty: 'title',
          columnType: ColumnType.InputTranslation
        },
        {
          displayedColumn: 'programme.priority.list.column.objective',
          customCellTemplate: this.specificObjective
        }
      ]
    });
  }

}
