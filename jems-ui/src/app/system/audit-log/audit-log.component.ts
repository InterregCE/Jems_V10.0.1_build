import {ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {Observable} from 'rxjs';
import {map, startWith, tap} from 'rxjs/operators';
import {AuditSearchRequestDTO, AuditService} from '@cat/api';
import {FormArray, FormBuilder} from '@angular/forms';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {AuditLogStore} from './audit-log-store.service';
import {SystemPageSidenavService} from '../services/system-page-sidenav.service';
import {LocaleDatePipe} from '../../common/pipe/locale-date.pipe';

@UntilDestroy()
@Component({
  selector: 'app-audit-log',
  templateUrl: './audit-log.component.html',
  styleUrls: ['./audit-log.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [AuditLogStore]
})
export class AuditLogComponent implements OnInit {

  MAT_CHIP_USER_IDS_INDEX = 0;
  MAT_CHIP_USER_EMAILS_INDEX = 1;
  MAT_CHIP_ACTIONS_INDEX = 2;
  MAT_CHIP_PROJECT_IDS_INDEX = 3;
  MAT_CHIP_START_DATE_INDEX = 4;
  MAT_CHIP_END_DATE_INDEX = 5;

  @ViewChild('userIdCell', {static: true})
  userIdCell: TemplateRef<any>;

  @ViewChild('projectIdCell', {static: true})
  projectIdCell: TemplateRef<any>;

  @ViewChild('actionField', {static: true})
  actionField: TemplateRef<any>;

  auditTableConfiguration: TableConfiguration;
  filteredActions: Observable<string[]>;
  actions: string[] = Object.values(AuditSearchRequestDTO.ActionsEnum);

  referenceForm = this.formBuilder.group({
    userId: [],
    userEmail: [],
    actions: [],
    projectIds: [],
    timeFrom: [],
    timeTo: [],
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
    this.formBuilder.group({
      name: 'timeFrom',
      values: this.formBuilder.array([]),
      isInverted: false,
    }),
    this.formBuilder.group({
      name: 'timeTo',
      values: this.formBuilder.array([]),
      isInverted: false,
    }),
  ]);

  constructor(private auditService: AuditService,
              private formBuilder: FormBuilder,
              private systemPageSidenavService: SystemPageSidenavService,
              private localeDatePipe: LocaleDatePipe,
              public auditLogStore: AuditLogStore) {
  }

  ngOnInit(): void {

    this.auditTableConfiguration = new TableConfiguration({
      routerLink: '/app/audit/',
      isTableClickable: false,
      columns: [
        {
          displayedColumn: 'audit.table.date.and.time',
          elementProperty: 'timestamp',
          columnType: ColumnType.DateColumnWithSeconds,
          columnWidth: ColumnWidth.DateColumn,
          sortProperty: 'timestamp'
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
      map((filters: any[]) => ({
        userIds: filters[this.MAT_CHIP_USER_IDS_INDEX].values,
        userEmails: filters[this.MAT_CHIP_USER_EMAILS_INDEX].values,
        actions: filters[this.MAT_CHIP_ACTIONS_INDEX].values?.filter((action: any) => !!action),
        projectIds: filters[this.MAT_CHIP_PROJECT_IDS_INDEX].values,
        timeFrom: filters[this.MAT_CHIP_START_DATE_INDEX].values[0],
        timeTo: filters[this.MAT_CHIP_END_DATE_INDEX].values[0],
      } as AuditSearchRequestDTO)),
      tap(() => this.auditLogStore.auditPageIndex$.next(0)),
      tap((filters) => this.auditLogStore.auditPageFilter$.next(filters)),
      untilDestroyed(this)
    ).subscribe();

    this.filteredActions = this.referenceForm.controls.actions.valueChanges
      .pipe(
        startWith(''),
        map(value => this.filter(value, this.actions))
      );
  }


  addFilter(filterIndex: number, event: Event, isDate: boolean): void {
    if (!this.referenceForm.valid) {
      return;
    }
    if (filterIndex === this.MAT_CHIP_ACTIONS_INDEX) {
      const action = (event.target as any)?.value;
      if (!action || Object.values(AuditSearchRequestDTO.ActionsEnum).includes(action)) {
        this.addFilterToIndex(filterIndex, action);
        (event.target as any).value = '';
      }
      return;
    }
    if (isDate) {
      const value: Date = filterIndex === this.MAT_CHIP_START_DATE_INDEX ? this.referenceForm.controls.timeFrom.value.toDate() : this.referenceForm.controls.timeTo.value.toDate();
      this.addDateFilterToIndex(filterIndex, value);

      if (filterIndex === this.MAT_CHIP_START_DATE_INDEX) {
        this.referenceForm.controls.timeFrom.patchValue(null);
      }
      if (filterIndex === this.MAT_CHIP_END_DATE_INDEX) {
        this.referenceForm.controls.timeTo.patchValue(null);
      }

    } else {
      const value: number = (event.target as any)?.value;
      this.addFilterToIndex(filterIndex, value);
      (event.target as any).value = '';
    }
  }

  addFilterToIndex(filterIndex: number, value: number | string): void {
    if (this.getValuesForFilterOnIndex(filterIndex).value.indexOf(value) === -1) {
      this.getValuesForFilterOnIndex(filterIndex).push(this.formBuilder.control(value));
    }
  }

  addDateFilterToIndex(filterIndex: number, value: Date): void {
    this.getValuesForFilterOnIndex(filterIndex).clear();
    if (this.getValuesForFilterOnIndex(filterIndex).value.indexOf(value) === -1) {
      this.getValuesForFilterOnIndex(filterIndex).push(this.formBuilder.control(value));
    }
  }

  getValuesForFilterOnIndex(index: number): FormArray {
    return this.filtersForm.at(index).get('values') as FormArray;
  }

  private filter(value: string, actions: string[]): string[] {
    const filterValue = (value || '').toLowerCase();
    return actions
      .filter(action => action.toLowerCase().includes(filterValue));
  }

  formatDateValueInChip(filterItem: any): string {
    if (filterItem instanceof Date) {
      return this.localeDatePipe.transform(filterItem, 'L', 'LT');
    }
    return filterItem;
  }
}
