import {ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ProjectApplicationListUserAssignmentsStore} from '@common/components/project-application-list-user-assignments/project-application-list-user-assignments-store.service';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {OutputUser} from '@cat/api';

@Component({
  selector: 'app-project-application-list-user-assignments',
  templateUrl: './project-application-list-user-assignments.component.html',
  styleUrls: ['./project-application-list-user-assignments.component.scss'],
  providers: [ProjectApplicationListUserAssignmentsStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationListUserAssignmentsComponent implements OnInit {

  @ViewChild('userAssignment', {static: true})
  userAssignment: TemplateRef<any>;

  tableConfiguration: TableConfiguration;

  constructor(
    public listStore: ProjectApplicationListUserAssignmentsStore,
  ) {
  }

  ngOnInit(): void {
    this.tableConfiguration = new TableConfiguration({
      isTableClickable: false,
      columns: [
        {
          columnWidth: ColumnWidth.IdColumn,
          displayedColumn: 'project.table.column.name.id',
          elementProperty: 'customIdentifier',
          sortProperty: 'customIdentifier',
          tooltip: {
            tooltipContent: 'projectStatus',
            tooltipTranslationKey: 'common.label.projectapplicationstatus'
          }
        },
        {
          columnWidth: ColumnWidth.DateColumn,
          displayedColumn: 'project.table.column.name.acronym',
          elementProperty: 'acronym',
          sortProperty: 'acronym',
        },
        {
          columnWidth: ColumnWidth.DescriptionColumn,
          displayedColumn: 'project.table.column.name.users',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.userAssignment
        },
      ]
    });
  }

  getUserByIdFromAvailable(userId: number, availableUsers: OutputUser[]): OutputUser | undefined {
    return availableUsers.find(x => x.id === userId);
  }

}
