import {ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import { Permission } from 'src/app/security/permissions/permission';
import {Forms} from '@common/utils/forms';
import {map, switchMap, tap} from 'rxjs/operators';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {OutputWorkPackageSimple} from '@cat/api';
import {combineLatest, Observable} from 'rxjs';
import {filter, take} from 'rxjs/internal/operators';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute} from '@angular/router';
import {ProjectWorkPackagePageStore} from './project-work-package-page-store.service';
import {ProjectApplicationFormSidenavService} from '../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {TableConfiguration} from '@common/components/table/model/table.configuration';

@Component({
  selector: 'app-project-work-package-page',
  templateUrl: './project-work-package-page.component.html',
  styleUrls: ['./project-work-package-page.component.scss'],
  providers: [ProjectWorkPackagePageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectWorkPackagePageComponent implements OnInit {

  Permission = Permission;

  @ViewChild('deletionCell', {static: true})
  deletionCell: TemplateRef<any>;
  @ViewChild('titleCell', {static: true})
  titleCell: TemplateRef<any>;

  projectId = this.activatedRoute.snapshot.params.projectId;
  tableConfiguration: TableConfiguration;

  data$: Observable<{
    workPackages: OutputWorkPackageSimple[],
    projectTitle: string
  }>;
  projectEditable$: Observable<boolean>;

  constructor(private pageStore: ProjectWorkPackagePageStore,
              private activatedRoute: ActivatedRoute,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private dialog: MatDialog) {
    this.data$ = combineLatest([
      this.pageStore.workPackages$,
      this.pageStore.projectTitle$
    ]).pipe(
      map(([workPackages, projectTitle]) => ({workPackages, projectTitle}))
    );
    this.projectEditable$ = this.pageStore.projectEditable$;
  }

  ngOnInit(): void {
    this.tableConfiguration = new TableConfiguration({
      routerLink: `/app/project/detail/${this.projectId}/applicationFormWorkPackage`,
      isTableClickable: true,
      sortable: false,
      columns: [
        {
          displayedColumn: 'project.application.form.workpackage.number',
          elementProperty: 'number',
          alternativeValueCondition: (element: any) => {
            return element === null;
          },
          alternativeValue: 'project.application.form.partner.number.info.auto',
          sortProperty: 'number'
        },
        {
          displayedColumn: 'project.application.form.workpackage.name',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.titleCell
        },
        {
          displayedColumn: ' ',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.deletionCell
        },
      ]
    });
  }

  delete(workPackage: OutputWorkPackageSimple, name: string): void {
    const i18nKey = name
      ? 'project.application.form.workpackage.table.action.delete.dialog.message'
      : 'project.application.form.workpackage.table.action.delete.dialog.message.no.name';

    Forms.confirm(
      this.dialog,
      {
        title: 'project.application.form.workpackage.table.action.delete.dialog.header',
        message: {i18nKey, i18nArguments: {name}},
        warnMessage: 'project.application.form.workpackage.table.action.delete.dialog.warning'
      }).pipe(
      take(1),
      filter(answer => !!answer),
      switchMap(() => this.pageStore.deleteWorkPackage(workPackage.id)),
      tap(() => this.projectApplicationFormSidenavService.refreshPackages(this.projectId))
    ).subscribe();
  }

}
