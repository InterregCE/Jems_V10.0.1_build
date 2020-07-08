import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {PageOutputProject} from '@cat/api';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {PageEvent} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {Tables} from '../../../../common/utils/tables';

@Component({
  selector: 'app-project-application-list',
  templateUrl: 'project-application-list.component.html',
  styleUrls: ['project-application-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class ProjectApplicationListComponent {
  Tables = Tables;

  @Input()
  projectPage: PageOutputProject;
  @Output()
  newPage: EventEmitter<PageEvent> = new EventEmitter<PageEvent>();
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
        elementProperty: 'submissionDate',
        sortProperty: 'submissionDate'
      }
    ]
  });
}
