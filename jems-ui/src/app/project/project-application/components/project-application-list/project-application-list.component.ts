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
  Permission = Permission;

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
        columnType: ColumnType.Date,
        elementProperty: 'firstSubmissionDate',
        sortProperty: 'firstSubmission.updated'
      },
      {
        displayedColumn: 'project.table.column.name.resubmission',
        columnType: ColumnType.Date,
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
}
