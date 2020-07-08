import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {PageOutputProject} from '@cat/api';
import {TableConfiguration} from '@common/components/table/model/table.configuration';

@Component({
  selector: 'app-project-application-list',
  templateUrl: 'project-application-list.component.html',
  styleUrls: ['project-application-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class ProjectApplicationListComponent {

  @Input()
  projectPage: PageOutputProject;

  tableConfiguration: TableConfiguration = new TableConfiguration({
    routerLink: '/project/',
    isTableClickable: true,
    columns: [
      {
        displayedColumn: 'Id',
        elementProperty: 'id',
      },
      {
        displayedColumn: 'Acronym',
        elementProperty: 'acronym',
      },
      {
        displayedColumn: 'Submission Date',
        elementProperty: 'submissionDate',
      }
    ]
  });
}
