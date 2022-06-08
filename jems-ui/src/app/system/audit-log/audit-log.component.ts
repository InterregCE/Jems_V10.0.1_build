import {ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {tap} from 'rxjs/operators';
import {AuditSearchRequestDTO, AuditService} from '@cat/api';
import {FormBuilder} from '@angular/forms';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {AuditLogStore} from './audit-log-store.service';
import {SystemPageSidenavService} from '../services/system-page-sidenav.service';
import {FilterListInputComponent} from '@common/components/filter/filter-list-input/filter-list-input.component';

@UntilDestroy()
@Component({
  selector: 'jems-audit-log',
  templateUrl: './audit-log.component.html',
  styleUrls: ['./audit-log.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [AuditLogStore]
})
export class AuditLogComponent implements OnInit {

  @ViewChild('userIdCell', {static: true})
  userIdCell: TemplateRef<any>;

  @ViewChild('projectIdCell', {static: true})
  projectIdCell: TemplateRef<any>;

  @ViewChild('actionField', {static: true})
  actionField: TemplateRef<any>;

  @ViewChild('descriptionCell', {static: true})
  descriptionCell: TemplateRef<any>;

  @ViewChild('userIdFilter')
  userIdFilter!: FilterListInputComponent;

  @ViewChild('projectIdFilter')
  projectIdFilter!: FilterListInputComponent;

  auditTableConfiguration: TableConfiguration;

  filterForm = this.formBuilder.group({
    userIds: [[]],
    userEmails: [[]],
    actions: [[]],
    projectIds: [[]],
    timeFrom: [],
    timeTo: []
  });

  actions = new Map<AuditSearchRequestDTO.ActionsEnum, AuditSearchRequestDTO.ActionsEnum>(
    Object.values(AuditSearchRequestDTO.ActionsEnum).map(action => [action, action])
  );

  constructor(private auditService: AuditService,
              private formBuilder: FormBuilder,
              private systemPageSidenavService: SystemPageSidenavService,
              public auditLogStore: AuditLogStore) {
    this.filterForm.valueChanges.pipe(
      tap(() => this.auditLogStore.auditPageIndex$.next(0)),
      tap((filters) => this.auditLogStore.auditPageFilter$.next(filters)),
      untilDestroyed(this)
    ).subscribe();
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
          customCellTemplate: this.descriptionCell,
        }
      ]
    });
  }

  getDescription(description: string): string {
    return description
      .split('\n').join('<br/>')
      .split('\t').join('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp');
  }

  isThereAnyActiveFilter(): boolean {
    return this.filterForm.value.userIds?.length ||
      this.filterForm.value.userEmails?.length ||
      this.filterForm.value.actions?.length ||
      this.filterForm.value.projectIds?.length ||
      this.filterForm.value.timeFrom ||
      this.filterForm.value.timeTo;
  }
}
