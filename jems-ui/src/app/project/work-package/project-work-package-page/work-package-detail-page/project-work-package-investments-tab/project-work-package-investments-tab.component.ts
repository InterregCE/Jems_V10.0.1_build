import {ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {WorkPackageInvestmentDTO} from '@cat/api';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ActivatedRoute} from '@angular/router';
import {combineLatest, Observable} from 'rxjs';
import {filter, map, take, tap} from 'rxjs/operators';
import {WorkPackagePageStore} from '../work-package-page-store.service';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '@common/utils/forms';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {Log} from '@common/utils/log';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {AFTER_CONTRACTED_STATUSES, ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {ColumnWidth} from '@common/components/table/model/column-width';

@Component({
  selector: 'jems-project-work-package-investments-tab',
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

  investments$: Observable<WorkPackageInvestmentDTO[]>;
  projectEditable$: Observable<boolean>;

  constructor(
    private activatedRoute: ActivatedRoute,
    public workPackageStore: WorkPackagePageStore,
    private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
    private projectVersionStore: ProjectVersionStore,
    private projectStore: ProjectStore,
    private visibilityStatusService: FormVisibilityStatusService,
    private dialog: MatDialog,
  ) {
    this.investments$ = combineLatest([
      this.workPackageStore.investments$,
      this.workPackageStore.workPackage$,
      this.projectStore.projectStatus$,
    ])
      .pipe(
        tap(([, workPackage]) => {
          this.workPackageNumber = workPackage.number;
        }),
        map(([investments, , status]) => investments.map((investment) => ({
          ...investment,
          isAlreadyContracted: AFTER_CONTRACTED_STATUSES.includes(status.status),
        }))),
      );
    this.projectEditable$ = this.workPackageStore.isProjectEditable$;
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
          sortProperty: 'investmentNumber',
        },
        ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.INVESTMENTS.TITLE) ?
          [{
            displayedColumn: 'project.application.form.workpackage.investments.title',
            columnType: ColumnType.CustomComponent,
            customCellTemplate: this.titleCell,
            sortProperty: 'title'
          }] : [],
        ...this.visibilityStatusService.isVisible(APPLICATION_FORM.SECTION_C.PROJECT_WORK_PLAN.INVESTMENTS.ADDRESS.COUNTRY_AND_NUTS) ? [{
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

  delete(workPackageInvestment: WorkPackageInvestmentDTO, isAlreadyContracted: boolean, title?: string | null): void {
    const titleKey = isAlreadyContracted
      ? 'project.application.form.workpackage.investment.table.action.deactivate.dialog.header'
      : 'project.application.form.workpackage.investment.table.action.delete.dialog.header';

    let messageKey = 'project.application.form.workpackage.investment.table.action.';
    messageKey += isAlreadyContracted ? 'deactivate' : 'delete';
    messageKey += '.dialog.message';
    messageKey += title ? '' : '.no.name';
    const messageArguments = title ? title : '';

    const warnKey = isAlreadyContracted
      ? 'project.application.form.workpackage.investment.table.action.deactivate.dialog.warning'
      : 'project.application.form.workpackage.investment.table.action.delete.dialog.warning';

    Forms.confirm(
      this.dialog,
      {
        title: titleKey,
        message: {i18nKey: messageKey, i18nArguments: {title: messageArguments}},
        warnMessage: warnKey,
      }).pipe(
      take(1),
      filter(answer => !!answer),
      map(() => this.workPackageStore.deleteWorkPackageInvestment(workPackageInvestment.id)
        .pipe(
          take(1),
          tap(() => this.workPackageStore.investmentsChanged$.next()),
          tap(() => Log.info('Deleted investment: ', this, workPackageInvestment.id))
        ).subscribe()),
    ).subscribe();
  }
}
