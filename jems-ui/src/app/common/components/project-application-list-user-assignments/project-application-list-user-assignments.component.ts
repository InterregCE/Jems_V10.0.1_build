import {ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {
  ProjectStatusDTO,
  ProjectUserDTO,
  ProjectUserService,
  UpdateProjectUserDTO,
  UserRoleCreateDTO,
  UserService,
  UserSummaryDTO,
} from '@cat/api';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {combineLatest, Observable} from 'rxjs';
import {catchError, map, take, tap} from 'rxjs/operators';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectApplicationListUserAssignmentsStore} from './project-application-list-user-assignments-store.service';
import {TranslateService} from '@ngx-translate/core';
import {Log} from '@common/utils/log';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@UntilDestroy()
@Component({
  selector: 'jems-project-application-list-user-assignments',
  templateUrl: './project-application-list-user-assignments.component.html',
  styleUrls: ['./project-application-list-user-assignments.component.scss'],
  providers: [FormService, ProjectApplicationListUserAssignmentsStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationListUserAssignmentsComponent implements OnInit {

  @ViewChild('callActionsCell', {static: true})
  actionsCell: TemplateRef<any>;
  @ViewChild('userAssignment', {static: true})
  userAssignment: TemplateRef<any>;

  tableConfiguration: TableConfiguration;
  separatorKeysCodes: number[] = [ENTER, COMMA];

  form = this.formBuilder.array([]);
  changes: UpdateProjectUserDTO[] = [];
  showSuccessAfterNextRefresh = false;

  data$: Observable<{
    rows: ProjectUserDTO[];
    totalElements: number;
  }>;

  filterData$: Observable<{
    calls: Map<number, string>;
    statuses: Map<ProjectStatusDTO.StatusEnum, string>;
    users: Map<number, string>;
  }>;

  filterForm = this.formBuilder.group({
    id: '',
    acronym: '',
    statuses: [[]],
    calls: [[]],
    users: [[]]
  });

  defaultUserPermissions: PermissionsEnum[] = [
    PermissionsEnum.ProjectRetrieve,
    PermissionsEnum.ProjectRetrieveEditUserAssignments,
  ];
  // those are creating MONITOR checkbox:
  availableUsersPermissions: PermissionsEnum[] = [
    PermissionsEnum.ProjectFormRetrieve,
    PermissionsEnum.ProjectFileApplicationRetrieve,
    PermissionsEnum.ProjectCheckApplicationForm,
    PermissionsEnum.ProjectAssessmentView,
    PermissionsEnum.ProjectStatusDecisionRevert,
    PermissionsEnum.ProjectStatusReturnToApplicant,
    PermissionsEnum.ProjectStartStepTwo,
    PermissionsEnum.ProjectFileAssessmentRetrieve,
  ];

  constructor(
    private formBuilder: FormBuilder,
    private projectUserService: ProjectUserService,
    public projectUserStore: ProjectApplicationListUserAssignmentsStore,
    private userService: UserService,
    public formService: FormService,
    private translateService: TranslateService
  ) {

    this.filterData$ = combineLatest([
      projectUserStore.publishedCalls$,
      this.userService.getMonitorUsers()
    ]).pipe(
      tap(data => Log.info('ProjectApplicationListUserAssignmentsComponent', data)),
      map( ([calls, monitorUsers]) =>({
        calls: new Map(calls.map(call => [call.id, call.name])),
        statuses: new Map(Object.values(ProjectStatusDTO.StatusEnum).map(status => [
          status, translateService.instant(`common.label.projectapplicationstatus.${status}`)
        ])),
        users: new Map(monitorUsers.map(monitorUser => [monitorUser.id, `${monitorUser.name} ${monitorUser.surname}`])),
      }))
    );

    this.filterForm.valueChanges.pipe(
      tap(filters => this.projectUserStore.filter$.next(filters)),
      untilDestroyed(this)
    ).subscribe();

    this.formService.init(this.form);
    this.data$ = combineLatest([
      this.projectUserStore.page$,
      this.userService.getMonitorUsers(),
      this.userService.getUsersWithProjectRetrievePermissions(),
    ]).pipe(
      map(([page, availableUsers, defaultUsers]) => {
        return {
          rows: page.content.map((project, index) => ({
            ...project,
            assignedUserIds: project.users.map(it => it.id),
            index,
            defaultUsers,
            availableUsers
          })),
          totalElements: page.totalElements
        };
      }),
      tap(data => this.resetForm(data.rows)),
      tap(() => {
        if (this.showSuccessAfterNextRefresh) {
          this.formService.setSuccess('project.user.assignment.message.successful');
        }
        this.showSuccessAfterNextRefresh = false;
      }),
    );
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
          columnWidth: ColumnWidth.ChipColumn,
          displayedColumn: 'project.table.column.name.acronym',
          elementProperty: 'acronym',
          sortProperty: 'acronym',
        },
        {
          displayedColumn: 'project.table.column.name.status',
          elementProperty: 'projectStatus',
          elementTranslationKey: 'common.label.projectapplicationstatus',
          sortProperty: 'currentStatus.status',
          columnWidth: ColumnWidth.SmallColumn,
        },
        {
          displayedColumn: 'project.table.column.name.related',
          elementProperty: 'relatedCall',
          sortProperty: 'callName',
          columnWidth: ColumnWidth.MediumColumn,
        },
        {
          displayedColumn: 'project.table.column.name.users',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.userAssignment
        },
        {
          columnWidth: ColumnWidth.IdColumn,
          displayedColumn: 'project.table.column.name.action',
          customCellTemplate: this.actionsCell
        }
      ]
    });

    this.form.valueChanges.pipe(
      map(() => this.form.value.filter((projectUser: UpdateProjectUserDTO) =>
        projectUser.userIdsToRemove.length || projectUser.userIdsToAdd.length)
      ),
      tap((changes: UpdateProjectUserDTO[]) => this.changes = changes),
      tap(changes => this.formService.setDirty(!!changes.length)),
      untilDestroyed(this),
    ).subscribe();
  }

  resetForm(projects: ProjectUserDTO[]): void {
    this.form.clear();
    projects.forEach(project => {
      this.form.push(this.formBuilder.group({
        projectId: this.formBuilder.control(project.id),
        userIds: this.formBuilder.array(project.users.map(it => it.id)),
        userIdsToAdd: this.formBuilder.array([]),
        userIdsToRemove: this.formBuilder.array([]),
      }));
    });
  }

  saveForm(): void {
    this.projectUserService.updateProjectUserAssignments(this.changes)
      .pipe(
        take(1),
        catchError(err => this.formService.setError(err)),
      ).subscribe(() => {
        this.showSuccessAfterNextRefresh = true;
        // refresh table
        this.projectUserStore.refresh();
    });
  }

  getUserByIdFromAvailable(userId: number, availableUsers: UserSummaryDTO[]): UserSummaryDTO | undefined {
    return availableUsers.find(x => x.id === userId);
  }

  projectAtIndex(projectIndex: number): FormGroup {
    return this.form.at(projectIndex) as FormGroup;
  }

  users(projectIndex: number): FormArray {
    return this.projectAtIndex(projectIndex).get('userIds') as FormArray;
  }

  clearUserProjectAssignments(projectIndex: number): void {
    this.userIds(projectIndex).forEach((element) => this.removeUser(projectIndex, element));
  }

  removeUser(projectIndex: number, userId: number): void {
    this.users(projectIndex).removeAt(this.userIds(projectIndex).indexOf(userId));
    const toAddIndex = (this.userIdsToAssign(projectIndex).value as number[]).indexOf(userId);
    if (toAddIndex !== -1) {
      this.userIdsToAssign(projectIndex).removeAt(toAddIndex);
    } else {
      this.userIdsToRemove(projectIndex).push(this.formBuilder.control(userId));
    }
  }

  addUser(projectIndex: number, user: UserSummaryDTO): void {
    this.users(projectIndex).push(this.formBuilder.control(user.id));
    const toRemoveIndex = (this.userIdsToRemove(projectIndex).value as number[]).indexOf(user.id);
    if (toRemoveIndex !== -1) {
      this.userIdsToRemove(projectIndex).removeAt(toRemoveIndex);
    } else {
      this.userIdsToAssign(projectIndex).push(this.formBuilder.control(user.id));
    }
  }

  getAvailableUsersWithoutSelected(projectIndex: number, availableUsers: UserSummaryDTO[]): UserSummaryDTO[] {
    const selectedUserIds = this.userIds(projectIndex);
    return availableUsers.filter(user => !selectedUserIds.includes(user.id));
  }

  private userIds(projectIndex: number): number[] {
    return this.form.at(projectIndex).get('userIds')?.value || [];
  }

  private userIdsToRemove(projectIndex: number): FormArray {
    return this.form.at(projectIndex).get('userIdsToRemove') as FormArray;
  }

  private userIdsToAssign(projectIndex: number): FormArray {
    return this.form.at(projectIndex).get('userIdsToAdd') as FormArray;
  }

  isThereAnyActiveFilter(): boolean {
    return this.filterForm.value.id?.length > 0 ||
      this.filterForm.value.acronym?.length > 0 ||
      this.filterForm.value.statuses?.length ||
      this.filterForm.value.calls?.length ||
      this.filterForm.value.users?.length;
  }
}
