import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {PageOutputProjectSimple} from '@cat/api';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {MatSort} from '@angular/material/sort';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {Permission} from '../../../../security/permissions/permission';

@Component({
  selector: 'app-project-application-list',
  templateUrl: 'project-application-list.component.html',
  styleUrls: ['project-application-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class ProjectApplicationListComponent {
  Permission = Permission

  @Input()
  projectPage: PageOutputProjectSimple;
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
        displayedColumn: 'First submission',
        columnType: ColumnType.Date,
        elementProperty: 'firstSubmissionDate',
        sortProperty: 'firstSubmission.updated'
      },
      {
        displayedColumn: 'Latest re-submission',
        columnType: ColumnType.Date,
        elementProperty: 'lastResubmissionDate',
        sortProperty: 'lastResubmission.updated'
      },
      {
        displayedColumn: 'Status',
        elementProperty: 'projectStatus.status',
        sortProperty: 'projectStatus.status'
      },
      {
        displayedColumn: 'Related Call',
        elementProperty: 'callName',
        sortProperty: 'callName'
      }
    ]
  });
}
