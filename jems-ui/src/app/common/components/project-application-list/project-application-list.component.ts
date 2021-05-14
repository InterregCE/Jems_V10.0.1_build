import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {ProjectApplicationListStore} from '@common/components/project-application-list/project-application-list-store.service';

@Component({
  selector: 'app-project-application-list',
  templateUrl: './project-application-list.component.html',
  styleUrls: ['./project-application-list.component.scss'],
  providers: [ProjectApplicationListStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationListComponent {

  @Input()
  filterByOwner: false;

  tableConfiguration: TableConfiguration = new TableConfiguration({
    routerLink: '/app/project/detail',
    isTableClickable: true,
    columns: [
      {
        displayedColumn: 'project.table.column.name.id',
        elementProperty: 'id',
        sortProperty: 'id'
      },
      {
        displayedColumn: 'project.table.column.name.acronym',
        elementProperty: 'acronym',
        sortProperty: 'acronym',
      },
      {
        displayedColumn: 'project.table.column.name.submission',
        columnType: ColumnType.DateColumn,
        elementProperty: 'firstSubmissionDate',
        sortProperty: 'firstSubmission.updated'
      },
      {
        displayedColumn: 'project.table.column.name.resubmission',
        columnType: ColumnType.DateColumn,
        elementProperty: 'lastResubmissionDate',
        sortProperty: 'lastResubmission.updated'
      },
      {
        displayedColumn: 'project.table.column.name.priority',
        elementProperty: 'programmePriorityCode',
        sortProperty: 'projectData.priorityPolicy.programmePriority.code',
        tooltip: {
          tooltipContent: 'programmePriority.title'
        }
      },
      {
        displayedColumn: 'project.table.column.name.objective',
        elementProperty: 'specificObjectiveCode',
        sortProperty: 'projectData.priorityPolicy.code',
        tooltip: {
          tooltipContent: 'specificObjective.programmeObjectivePolicy',
          tooltipTranslationKey: 'programme.policy'
        }
      },
      {
        displayedColumn: 'project.table.column.name.status',
        elementProperty: 'projectStatus',
        elementTranslationKey: 'common.label.projectapplicationstatus',
        sortProperty: 'projectStatus.status'
      },
      {
        displayedColumn: 'project.table.column.name.related',
        elementProperty: 'callName',
        sortProperty: 'callName'
      }
    ]
  });

  constructor(public listStore: ProjectApplicationListStore) {
  }

}
