import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {combineLatest, Observable, Subject} from 'rxjs';
import {debounceTime, distinctUntilChanged, map, mergeMap, startWith, tap} from 'rxjs/operators';
import {Tables} from '../../common/utils/tables';
import {Log} from '../../common/utils/log';
import {AuditSearchRequestDTO, AuditService} from '@cat/api';
import {FormArray, FormBuilder} from '@angular/forms';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {UntilDestroy, untilDestroyed} from "@ngneat/until-destroy";
import {ColumnWidth} from "@common/components/table/model/column-width";
import {AuditLogConstants} from "./audit/log/constants/audit.log.constants";

@UntilDestroy()
@Component({
  selector: 'app-audit-log',
  templateUrl: './audit-log.component.html',
  styleUrls: ['./audit-log.component.scss']
})
export class AuditLogComponent implements OnInit {
  auditLogConstants = AuditLogConstants;

  MAT_CHIP_USER_IDS_INDEX = 0;
  MAT_CHIP_USER_EMAILS_INDEX = 1;
  MAT_CHIP_ACTIONS_INDEX = 2;
  MAT_CHIP_PROJECT_IDS_INDEX = 3;

  @ViewChild('userIdCell', {static: true})
  userIdCell: TemplateRef<any>;

  @ViewChild('projectIdCell', {static: true})
  projectIdCell: TemplateRef<any>;

  @ViewChild('actionField', {static: true})
  actionField: TemplateRef<any>;

  auditTableConfiguration: TableConfiguration;

  auditPageSize$ = new Subject<number>();
  auditPageIndex$ = new Subject<number>();
  auditPageFilter$ = new Subject<AuditSearchRequestDTO>();
  filteredActions: Observable<string[]>;

  actions = this.auditLogConstants.actionList;

  auditPage$ =
      combineLatest([
        this.auditPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
        this.auditPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
        this.auditPageFilter$.pipe(startWith({} as AuditSearchRequestDTO)),
      ])
          .pipe(
              mergeMap(([pageIndex, pageSize, pageFilter]) =>
                  this.auditService.getAudits(pageFilter,  pageIndex, pageSize)),
              tap(page => Log.info('Fetched the Audits:', this, page.content)),
          );

  referenceForm = this.formBuilder.group({
    userId: [],
    userEmail: [],
    actions: [],
    description: [],
  });

  filtersForm = this.formBuilder.array([
    this.formBuilder.group({
      name: 'userId',
      values: this.formBuilder.array([]),
      isInverted: false,
    }),
    this.formBuilder.group({
      name: 'userEmail',
      values: this.formBuilder.array([]),
      isInverted: false,
    }),
    this.formBuilder.group({
      name: 'action',
      values: this.formBuilder.array([]),
      isInverted: false,
    }),
    this.formBuilder.group({
      name: 'projectId',
      values: this.formBuilder.array([]),
      isInverted: false,
    }),
  ])

  constructor(private auditService: AuditService,
              private formBuilder: FormBuilder) {
  }

  ngOnInit(): void {

    this.auditTableConfiguration = new TableConfiguration({
      routerLink: '/app/audit/',
      isTableClickable: false,
      columns: [
        {
          displayedColumn: 'audit.table.date.and.time',
          elementProperty: 'timestamp',
          columnType: ColumnType.DateColumn,
          columnWidth: ColumnWidth.DateColumn
        },
        {
          displayedColumn: 'audit.table.user.id',
          columnType: ColumnType.Decimal,
          customCellTemplate: this.userIdCell,
          columnWidth: ColumnWidth.IdColumn
        },
        {
          displayedColumn: 'audit.table.user.email',
          elementProperty: 'user.email'
        },
        {
          displayedColumn: 'audit.table.action',
          elementProperty: 'action'
        },
        {
          displayedColumn: 'audit.table.project.id',
          columnType: ColumnType.Decimal,
          customCellTemplate: this.projectIdCell,
          columnWidth: ColumnWidth.IdColumn
        },
        {
          displayedColumn: 'audit.table.description',
          elementProperty: 'description'
        }
      ]
    });

    this.filtersForm.valueChanges.pipe(
        debounceTime(500),
        distinctUntilChanged(),
        map((filters: any[]) => ({
          userIds: filters[this.MAT_CHIP_USER_IDS_INDEX].values,
          userEmails: filters[this.MAT_CHIP_USER_EMAILS_INDEX].values,
          actions: filters[this.MAT_CHIP_ACTIONS_INDEX].values,
          projectIds: filters[this.MAT_CHIP_PROJECT_IDS_INDEX].values,
          timeFrom: null as any,
          timeTo: null as any,
        } as AuditSearchRequestDTO)),
        tap((filters) => this.auditPageFilter$.next(filters)),
        untilDestroyed(this)
    ).subscribe();

    this.filteredActions = this.referenceForm.controls.actions.valueChanges
        .pipe(
            startWith(''),
            map(value => this.filter(value, this.actions))
        );
  }

  addFilter(filterIndex: number, event: Event): void {
    const value: number = (event.target as any)?.value;
    this.addFilterToIndex(filterIndex, value);
    (event.target as any).value = '';
  }

  addFilterToIndex(filterIndex: number, value: number|string): void {
    if (this.getValuesForFilterOnIndex(filterIndex).value.indexOf(value) === -1) {
      this.getValuesForFilterOnIndex(filterIndex).push(this.formBuilder.control(value));
    }
  }

  getValuesForFilterOnIndex(index: number): FormArray {
    return this.filtersForm.at(index).get('values') as FormArray
  }

  private filter(value: string, actions: string[]): string[] {
    const filterValue = (value || '').toLowerCase();
    return actions
        .filter(action => action.toLowerCase().includes(filterValue))
        .map(action => action);
  }
}
