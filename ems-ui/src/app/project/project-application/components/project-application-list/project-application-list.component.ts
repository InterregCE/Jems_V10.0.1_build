import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {PageOutputProject} from '@cat/api';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {MatSort} from '@angular/material/sort';
import {ColumnType} from '@common/components/table/model/column-type.enum';

@Component({
  selector: 'app-project-application-list',
  templateUrl: 'project-application-list.component.html',
  styleUrls: ['project-application-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class ProjectApplicationListComponent {
  @Input()
  projectPage: PageOutputProject;
  @Input()
  pageIndex: number;
  @Output()
  newPageSize: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newPageIndex: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newSort: EventEmitter<Partial<MatSort>> = new EventEmitter<Partial<MatSort>>();

  tableConfiguration: TableConfiguration = new TableConfiguration({
    routerLink: '/project/',
    isTableClickable: true,
    columns: [
      {
        displayedColumn: 'Id',
        elementProperty: 'id',
        sortProperty: 'id'
      },
      {
        displayedColumn: 'Acronym',
        elementProperty: 'acronym',
        sortProperty: 'acronym',
      },
      {
        displayedColumn: 'Submission Date',
        columnType: ColumnType.Date,
        elementProperty: 'submissionDate',
        sortProperty: 'submissionDate'
      },
      {
        displayedColumn: 'Status',
        elementProperty: 'projectStatus.status',
        sortProperty: 'projectStatus.status'
      }
    ]
  });
}
