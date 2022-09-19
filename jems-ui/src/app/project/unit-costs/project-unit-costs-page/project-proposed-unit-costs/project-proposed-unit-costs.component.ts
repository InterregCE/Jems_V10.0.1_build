import {ChangeDetectionStrategy, Component, TemplateRef, ViewChild} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {ProgrammeUnitCostListDTO, ProjectStatusDTO} from '@cat/api';
import { Alert } from '@common/components/forms/alert';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {catchError, map, take, tap} from 'rxjs/operators';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {ProjectUnitCostsStore} from '@project/unit-costs/project-unit-costs-page/project-unit-costs-store.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import { ActivatedRoute } from '@angular/router';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {APIError} from '@common/models/APIError';
import {ColumnWidth} from '@common/components/table/model/column-width';

@Component({
  selector: 'jems-project-proposed-unit-costs',
  templateUrl: './project-proposed-unit-costs.component.html',
  styleUrls: ['./project-proposed-unit-costs.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectProposedUnitCostsComponent {
  @ViewChild('nameCell', {static: true})
  nameCell: TemplateRef<any>;

  @ViewChild('unitTypeCell', {static: true})
  unitTypeCell: TemplateRef<any>;

  @ViewChild('categoriesCell', {static: true})
  categoriesCell: TemplateRef<any>;

  @ViewChild('valueCell', {static: true})
  valueCell: TemplateRef<any>;

  @ViewChild('actionCell', {static: true})
  actionCell: TemplateRef<any>;

  APPLICATION_FORM = APPLICATION_FORM;
  Alert = Alert;
  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  displayedColumns: string[] = ['name', 'type', 'category', 'costPerUnit', 'delete'];
  error$ = new BehaviorSubject<APIError | null>(null);

  isDeleteAvailable = false;

  tableConfiguration: TableConfiguration;
  unitCostDataSource$: Observable<MatTableDataSource<ProgrammeUnitCostListDTO>> = this.unitCostStore.projectProposedUnitCosts$
    .pipe(
      map(list => new MatTableDataSource(list))
    );

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              public unitCostStore: ProjectUnitCostsStore,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService) {
    combineLatest([
      this.projectStore.projectStatus$,
      this.projectStore.projectEditable$
    ]).pipe(
      tap(([status, isEditable]) => {
        this.isDeleteAvailable = status.status !== ProjectStatusDTO.StatusEnum.INMODIFICATION && isEditable;
        this.resetTable();
      })
    ).subscribe();
  }

  private resetTable() {
    this.tableConfiguration = new TableConfiguration({
      isTableClickable: true,
      sortable: false,
      columns: this.isDeleteAvailable
        ? [
          {
            displayedColumn: 'unit.cost.table.column.name.name',
            columnType: ColumnType.CustomComponent,
            customCellTemplate: this.nameCell,
          },
          {
            displayedColumn: 'unit.cost.table.column.name.unit.type',
            columnType: ColumnType.CustomComponent,
            customCellTemplate: this.unitTypeCell,
          },
          {
            displayedColumn: 'unit.cost.table.column.name.unit.category',
            columnType: ColumnType.CustomComponent,
            customCellTemplate: this.categoriesCell,
          },
          {
            displayedColumn: 'unit.cost.table.column.name.cost.unit',
            columnType: ColumnType.CustomComponent,
            customCellTemplate: this.valueCell,
          },
          {
            displayedColumn: 'common.delete.entry',
            columnType: ColumnType.CustomComponent,
            customCellTemplate: this.actionCell,
            columnWidth: ColumnWidth.IdColumn,
            clickable: false
          },
        ]
        : [
          {
            displayedColumn: 'unit.cost.table.column.name.name',
            columnType: ColumnType.CustomComponent,
            customCellTemplate: this.nameCell,
          },
          {
            displayedColumn: 'unit.cost.table.column.name.unit.type',
            columnType: ColumnType.CustomComponent,
            customCellTemplate: this.unitTypeCell,
          },
          {
            displayedColumn: 'unit.cost.table.column.name.unit.category',
            columnType: ColumnType.CustomComponent,
            customCellTemplate: this.categoriesCell,
          },
          {
            displayedColumn: 'unit.cost.table.column.name.cost.unit',
            columnType: ColumnType.CustomComponent,
            customCellTemplate: this.valueCell,
          }
        ]
    });

    this.tableConfiguration.routerLink = `/app/project/detail/${this.projectId}/applicationFormUnitCosts/projectProposed`;
  }

  deleteEntry(unitCost: ProgrammeUnitCostListDTO): void {
    this.unitCostStore.deleteUnitCost(unitCost.id).pipe(
      take(1),
      catchError((error) => this.showErrorMessage(error.error)),
    ).subscribe();
  }

  private showErrorMessage(error: APIError): Observable<null> {
    this.error$.next(error);
    setTimeout(() => {
      this.error$.next(null);
    },         4000);
    return of(null);
  }
}
