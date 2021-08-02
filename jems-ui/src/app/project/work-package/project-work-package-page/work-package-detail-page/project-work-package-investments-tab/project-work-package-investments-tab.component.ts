import {ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {WorkPackageInvestmentDTO, WorkPackageInvestmentService} from '@cat/api';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ActivatedRoute} from '@angular/router';
import {combineLatest, Subject} from 'rxjs';
import {filter, map, mergeMap, startWith, take, tap} from 'rxjs/operators';
import {ProjectWorkPackagePageStore} from '../project-work-package-page-store.service';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '@common/utils/forms';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {Log} from '@common/utils/log';
import {ProjectApplicationFormSidenavService} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {ColumnWidth} from '@common/components/table/model/column-width';

@Component({
  selector: 'app-project-work-package-investments-tab',
  templateUrl: './project-work-package-investments-tab.component.html',
  styleUrls: ['./project-work-package-investments-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectWorkPackageInvestmentsTabComponent implements OnInit {

  workPackageNumber: number;
  tableConfiguration: TableConfiguration;

  @ViewChild('deletionCell', {static: true})
  deletionCell: TemplateRef<any>;
  @ViewChild('numberingCell', {static: true})
  numberingCell: TemplateRef<any>;
  @ViewChild('titleCell', {static: true})
  titleCell: TemplateRef<any>;

  investmentsChanged$ = new Subject<void>();

  investments$ =
    combineLatest([
      this.projectStore.projectId$,
      this.projectVersionStore.currentRouteVersion$,
      this.workPackageStore.workPackage$
        .pipe(
          tap(workPackage => this.workPackageNumber = workPackage.number),
        ),
      this.investmentsChanged$.pipe(startWith(null))
    ])
      .pipe(
        filter(([projectId, version, workPackage]) => !!workPackage.id && !!projectId),
        mergeMap(([projectId, version, workPackage]) =>
          this.workPackageInvestmentService.getWorkPackageInvestments(projectId, workPackage.id, version)),
        tap(investments => Log.info('Fetched the work package investments:', this, investments)),
      );

  constructor(private activatedRoute: ActivatedRoute,
              public workPackageStore: ProjectWorkPackagePageStore,
              private workPackageInvestmentService: WorkPackageInvestmentService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private projectVersionStore: ProjectVersionStore,
              public projectStore: ProjectStore,
              private visibilityStatusService: FormVisibilityStatusService,
              private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.tableConfiguration = new TableConfiguration({
      isTableClickable: true,
      routerLink: '../investments/',
      columns: [
        {
          displayedColumn: 'project.application.form.workpackage.investments.number',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.numberingCell,
          sortProperty: 'investmentNumber'
        },
        ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.INVESTMENTS.TITLE) ?
          [{
            displayedColumn: 'project.application.form.workpackage.investments.title',
            columnType: ColumnType.CustomComponent,
            customCellTemplate: this.titleCell,
            sortProperty: 'title'
          }] : [],
        ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.INVESTMENTS.ADDRESS.COUNTRY_AND_NUTS) ?          [{
            displayedColumn: 'project.application.form.workpackage.investments.nuts3',
            elementProperty: 'address.region3',
            sortProperty: 'address.region3',
          }] : [],
        {
          displayedColumn: ' ',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.deletionCell,
          columnWidth: ColumnWidth.IdColumn
        },
      ]
    });
  }

  delete(workPackageInvestment: WorkPackageInvestmentDTO, title?: string | null): void {
    const messageKey = title
      ? 'project.application.form.workpackage.investment.table.action.delete.dialog.message'
      : 'project.application.form.workpackage.investment.table.action.delete.dialog.message.no.name';
    const messageArguments = title ? title : '';

    Forms.confirm(
      this.dialog,
      {
        title: 'project.application.form.workpackage.table.action.delete.dialog.header',
        message: {i18nKey: messageKey, i18nArguments: {title: messageArguments}},
        warnMessage: 'project.application.form.workpackage.investment.table.action.delete.dialog.warning'
      }).pipe(
      take(1),
      filter(answer => !!answer),
      map(() => this.workPackageStore.deleteWorkPackageInvestment(workPackageInvestment.id)
        .pipe(
          take(1),
          tap(() => this.investmentsChanged$.next()),
          tap(() => Log.info('Deleted investment: ', this, workPackageInvestment.id))
        ).subscribe()),
    ).subscribe();
  }
}
