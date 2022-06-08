import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {ProjectApplicationListStore} from '@common/components/project-application-list/project-application-list-store.service';
import {FormBuilder} from '@angular/forms';
import {map, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {combineLatest, Observable} from 'rxjs';
import {ProgrammeSpecificObjectiveDTO, ProjectStatusDTO} from '@cat/api';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'jems-project-application-list',
  templateUrl: './project-application-list.component.html',
  styleUrls: ['./project-application-list.component.scss'],
  providers: [ProjectApplicationListStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
@UntilDestroy()
export class ProjectApplicationListComponent {

  @Input()
  filterByOwner: false;

  tableConfiguration: TableConfiguration = new TableConfiguration({
    routerLink: '/app/project/detail',
    isTableClickable: true,
    columns: [
      {
        displayedColumn: 'project.table.column.name.id',
        elementProperty: 'customIdentifier',
        sortProperty: 'customIdentifier'
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
        sortProperty: 'priorityPolicy.programmePriority.code',
        tooltip: {
          tooltipContent: 'programmePriority.title'
        }
      },
      {
        displayedColumn: 'project.table.column.name.objective',
        elementProperty: 'specificObjectiveCode',
        sortProperty: 'priorityPolicy.code',
        tooltip: {
          tooltipContent: 'specificObjective.programmeObjectivePolicy',
          tooltipTranslationKey: 'programme.policy'
        }
      },
      {
        displayedColumn: 'project.table.column.name.status',
        elementProperty: 'projectStatus',
        elementTranslationKey: 'common.label.projectapplicationstatus',
        sortProperty: 'currentStatus.status'
      },
      {
        displayedColumn: 'project.table.column.name.related',
        elementProperty: 'callName',
        sortProperty: 'callName'
      }
    ]
  });

  filterForm = this.formBuilder.group({
    id: '',
    acronym: '',
    firstSubmissionFrom: '',
    firstSubmissionTo: '',
    lastSubmissionFrom: '',
    lastSubmissionTo: '',
    objectives: [[]],
    statuses: [[]],
    calls: [[]]
  });

  filterData$: Observable<{
    objectives: Map<ProgrammeSpecificObjectiveDTO.ProgrammeObjectivePolicyEnum, string>;
    calls: Map<number, string>;
    statuses: Map<ProjectStatusDTO.StatusEnum, string>;
  }>;

  constructor(public listStore: ProjectApplicationListStore,
              private formBuilder: FormBuilder,
              private translateService: TranslateService) {
    this.filterData$ = combineLatest([listStore.policyObjectives$, listStore.publishedCalls$])
      .pipe(
        map(([objectives, calls]) =>({
          objectives: new Map(objectives.map(objective => [objective.programmeObjectivePolicy, objective.code])),
          calls: new Map(calls.map(call => [call.id, call.name])),
          statuses: new Map(Object.values(ProjectStatusDTO.StatusEnum).map(status => [
            status, translateService.instant(`common.label.projectapplicationstatus.${status}`)
          ]))
        }))
      );

    this.filterForm.valueChanges.pipe(
      tap(filters => this.listStore.filter$.next(filters)),
      untilDestroyed(this)
    ).subscribe();
  }

  isThereAnyActiveFilter(): boolean {
    return this.filterForm.value.id?.length > 0 ||
      this.filterForm.value.acronym?.length > 0 ||
      this.filterForm.value.firstSubmissionFrom ||
      this.filterForm.value.firstSubmissionTo ||
      this.filterForm.value.lastSubmissionFrom ||
      this.filterForm.value.lastSubmissionTo ||
      this.filterForm.value.objectives?.length > 0 ||
      this.filterForm.value.statuses?.length > 0 ||
      this.filterForm.value.calls?.length;
  }

}
