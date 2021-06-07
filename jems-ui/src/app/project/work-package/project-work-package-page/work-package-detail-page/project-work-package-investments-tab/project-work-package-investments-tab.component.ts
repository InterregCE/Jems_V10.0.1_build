import {ChangeDetectionStrategy, Component, Input, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {WorkPackageInvestmentDTO, WorkPackageInvestmentService} from '@cat/api';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {MatSort} from '@angular/material/sort';
import {ActivatedRoute} from '@angular/router';
import {combineLatest, Subject} from 'rxjs';
import {filter, map, mergeMap, startWith, take, tap} from 'rxjs/operators';
import {ProjectWorkPackagePageStore} from '../project-work-package-page-store.service';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '../../../../../common/utils/forms';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {Log} from '../../../../../common/utils/log';
import {ProjectApplicationFormSidenavService} from '../../../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {FormService} from '@common/components/section/form/form.service';
import {Tables} from '../../../../../common/utils/tables';

@Component({
  selector: 'app-project-work-package-investments-tab',
  templateUrl: './project-work-package-investments-tab.component.html',
  styleUrls: ['./project-work-package-investments-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectWorkPackageInvestmentsTabComponent implements OnInit {
  projectId = this.activatedRoute.snapshot.params.projectId;
  workPackageId = this.activatedRoute.snapshot.params.workPackageId;
  workPackageNumber: number;
  tableConfiguration: TableConfiguration;

  @Input()
  pageIndex: number;
  @Input()
  editable: boolean;

  @ViewChild('deletionCell', {static: true})
  deletionCell: TemplateRef<any>;
  @ViewChild('numberingCell', {static: true})
  numberingCell: TemplateRef<any>;
  @ViewChild('titleCell', {static: true})
  titleCell: TemplateRef<any>;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  currentPage$ =
    combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith({active: 'id', direction: 'asc'}),
        map(sort => sort?.direction ? sort : Tables.DEFAULT_INITIAL_SORT),
        map(sort => `${sort.active},${sort.direction}`)
      ),
      this.workPackageStore.workPackage$
        .pipe(
          tap(workPackage => this.workPackageId = workPackage.id),
          tap(workPackage => this.workPackageNumber = workPackage.number),
          tap(() => this.setRouterLink())
        )
    ])
      .pipe(
        filter(() => !!this.workPackageId),
        mergeMap(([pageIndex, pageSize, sort]) =>
          this.workPackageInvestmentService.getWorkPackageInvestments(this.workPackageId, pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the work package investments:', this, page.content)),
      );

  constructor(private activatedRoute: ActivatedRoute,
              public workPackageStore: ProjectWorkPackagePageStore,
              private workPackageInvestmentService: WorkPackageInvestmentService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.tableConfiguration = new TableConfiguration({
      isTableClickable: true,
      routerLink: `/app/project/detail/${this.projectId}/applicationFormWorkPackage/detail/${this.workPackageId}/investment/detail`,
      columns: [
        {
          displayedColumn: 'project.application.form.workpackage.investments.number',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.numberingCell,
          sortProperty: 'investmentNumber'
        },
        {
          displayedColumn: 'project.application.form.workpackage.investments.title',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.titleCell,
          sortProperty: 'title'
        },
        {
          displayedColumn: 'project.application.form.workpackage.investments.nuts3',
          elementProperty: 'address.region3',
          sortProperty: 'address.region3',
        },
        {
          displayedColumn: ' ',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.deletionCell
        },
      ]
    });
    this.setRouterLink();
  }

  delete(workPackageInvestment: WorkPackageInvestmentDTO, title: string): void {
    const messageKey = title
      ? 'project.application.form.workpackage.investment.table.action.delete.dialog.message'
      : 'project.application.form.workpackage.investment.table.action.delete.dialog.message.no.name';

    Forms.confirm(
      this.dialog,
      {
        title: 'project.application.form.workpackage.table.action.delete.dialog.header',
        message: {i18nKey: messageKey, i18nArguments: {title}},
        warnMessage: 'project.application.form.workpackage.investment.table.action.delete.dialog.warning'
      }).pipe(
      take(1),
      filter(answer => !!answer),
      map(() => this.workPackageStore.deleteWorkPackageInvestment(workPackageInvestment.id)
        .pipe(
          take(1),
          tap(() => this.newPageIndex$.next(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
          tap(() => Log.info('Deleted investment: ', this, workPackageInvestment.id))
        ).subscribe()),
    ).subscribe();
  }

  private setRouterLink(): void {
    this.tableConfiguration.routerLink = `/app/project/detail/${this.projectId}/applicationFormWorkPackage/detail/${this.workPackageId}/investment/detail`;
  }
}

