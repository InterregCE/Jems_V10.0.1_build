import {ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {ProgrammeUnitCostListDTO, ProjectStatusDTO} from '@cat/api';
import { Alert } from '@common/components/forms/alert';
import {BehaviorSubject, combineLatest, Observable, of} from 'rxjs';
import {catchError, map, take} from 'rxjs/operators';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {ProjectUnitCostsStore} from '@project/unit-costs/project-unit-costs-page/project-unit-costs-store.service';
import { ActivatedRoute } from '@angular/router';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {APIError} from '@common/models/APIError';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {UntilDestroy, untilDestroyed} from "@ngneat/until-destroy";

@UntilDestroy()
@Component({
  selector: 'jems-project-proposed-unit-costs',
  templateUrl: './project-proposed-unit-costs.component.html',
  styleUrls: ['./project-proposed-unit-costs.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectProposedUnitCostsComponent implements OnInit {

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

  Alert = Alert;
  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  error$ = new BehaviorSubject<APIError | null>(null);

  data$: Observable<{
    tableConfiguration: TableConfiguration;
    dataSource: MatTableDataSource<ProgrammeUnitCostListDTO>,
  }>;

  constructor(
    public projectStore: ProjectStore,
    private activatedRoute: ActivatedRoute,
    public unitCostStore: ProjectUnitCostsStore,
    private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService) {
  }

  ngOnInit(): void {
    this.data$ = combineLatest([
      this.projectStore.projectStatus$,
      this.projectStore.projectEditable$,
      this.unitCostStore.projectProposedUnitCosts$,
    ]).pipe(
      map(([status, isEditable, unitCosts]) => ({
        tableConfiguration: this.getTableConfig(this.projectId, status.status !== ProjectStatusDTO.StatusEnum.INMODIFICATION && isEditable),
        dataSource: new MatTableDataSource(unitCosts),
      })),
      untilDestroyed(this),
    );
  }

  private getTableConfig(projectId: number, isDeleteAvailable: boolean): TableConfiguration {
    return new TableConfiguration({
      isTableClickable: true,
      sortable: false,
      routerLink: `/app/project/detail/${projectId}/applicationFormUnitCosts/projectProposed`,
      columns: [
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
        ...isDeleteAvailable ? [
          {
            displayedColumn: 'common.delete.entry',
            columnType: ColumnType.CustomComponent,
            customCellTemplate: this.actionCell,
            columnWidth: ColumnWidth.IdColumn,
            clickable: false
          }
        ] : [],
      ],
    });
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
