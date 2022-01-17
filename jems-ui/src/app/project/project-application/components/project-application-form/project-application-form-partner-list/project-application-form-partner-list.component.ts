import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  TemplateRef,
  ViewChild
} from '@angular/core';
import {MatSort} from '@angular/material/sort';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {PageProjectBudgetPartnerSummaryDTO, ProjectStatusDTO} from '@cat/api';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {Forms} from '@common/utils/forms';
import {filter, map, take, tap} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import '@angular/common/locales/global/de';
import {ProjectBudgetPartner} from '@project/model/ProjectBudgetPartner';
import {Observable} from 'rxjs';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {ProjectUtil} from '@project/common/project-util';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';

@Component({
  selector: 'app-project-application-form-partner-list',
  templateUrl: './project-application-form-partner-list.component.html',
  styleUrls: ['./project-application-form-partner-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerListComponent implements OnInit {
  @Input()
  projectId: number;
  @Input()
  projectStatus: ProjectStatusDTO;
  @Input()
  partnerPage$: Observable<PageProjectBudgetPartnerSummaryDTO>;
  @Input()
  pageIndex: number;
  @Input()
  editable: boolean;

  @Output()
  newPageSize: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newPageIndex: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newSort: EventEmitter<Partial<MatSort>> = new EventEmitter<Partial<MatSort>>();
  @Output()
  deletePartner = new EventEmitter<number>();
  @Output()
  deactivatePartner = new EventEmitter<number>();

  @ViewChild('statusCell', {static: true})
  statusCell: TemplateRef<any>;

  @ViewChild('deletionCell', {static: true})
  deletionCell: TemplateRef<any>;

  @ViewChild('deactivationCell', {static: true})
  deactivationCell: TemplateRef<any>;

  @ViewChild('budgetCell', {static: true})
  budgetCell: TemplateRef<any>;

  tableConfiguration: TableConfiguration;

  tableRows$: Observable<ProjectBudgetPartner[]>;

  totalElements = 0;


  constructor(private dialog: MatDialog,
              private formVisibilityStatusService: FormVisibilityStatusService) {
  }

  ngOnInit(): void {
    this.tableRows$ = this.partnerPage$.pipe(
      tap(pageProjectBudgetPartnerSummaryDTO => this.totalElements = pageProjectBudgetPartnerSummaryDTO.totalElements),
      map(pageProjectBudgetPartnerSummaryDTO => this.getProjectPartnerSummary(pageProjectBudgetPartnerSummaryDTO))
    );

    this.tableConfiguration = new TableConfiguration({
      routerLink: '..',
      isTableClickable: true,
      columns: [
        {
          displayedColumn: 'project.application.form.partner.table.number',
          elementProperty: 'sortNumber',
          alternativeValueCondition: (element: any) => {
            return element === null;
          },
          alternativeValue: 'project.application.form.partner.number.info.auto',
          sortProperty: 'sortNumber',
          columnWidth : ColumnWidth.NarrowColumn
        },
        {
          displayedColumn: 'project.application.form.partner.table.status',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.statusCell
        },
        {
          displayedColumn: 'project.application.form.partner.table.name',
          elementProperty: 'abbreviation',
          sortProperty: 'abbreviation',
        },
        {
          displayedColumn: 'project.application.form.partner.table.role',
          elementProperty: 'role',
          elementTranslationKey: 'common.label.project.partner.role',
          sortProperty: 'role',
        },
        {
          displayedColumn: 'project.application.form.partner.list.nuts.title',
          elementProperty: 'region',
          sortProperty: 'addresses.address.country',
        },
        ...this.formVisibilityStatusService.isVisible(APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING) ?
          [ {
            displayedColumn: 'project.partner.coFinancing.total',
            columnType: ColumnType.CustomComponent,
            customCellTemplate: this.budgetCell,
          },] : [],
        ...ProjectUtil.isInModifiableStatusBeforeApproved(this.projectStatus) ?
        [{
          displayedColumn: ' ',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.deletionCell,
          columnWidth : ColumnWidth.NarrowColumn
        }] : [],
        ...ProjectUtil.isInModifiableStatusAfterApproved(this.projectStatus) ?
          [{
          displayedColumn: '   ',
          columnType: ColumnType.CustomComponent,
          columnWidth: ColumnWidth.WideColumn,
          customCellTemplate: this.deactivationCell
        }] : []
      ]
    });
  }

  getProjectPartnerSummary(projectPartnerSummary: PageProjectBudgetPartnerSummaryDTO): ProjectBudgetPartner[]{
    return projectPartnerSummary.content.map(projectPartnerBudgetSummary => ( {
      id: projectPartnerBudgetSummary.partnerSummary.id,
      active: projectPartnerBudgetSummary.partnerSummary.active,
      abbreviation: projectPartnerBudgetSummary.partnerSummary.abbreviation,
      role: projectPartnerBudgetSummary.partnerSummary.role,
      country: projectPartnerBudgetSummary.partnerSummary.country,
      region: projectPartnerBudgetSummary.partnerSummary.region,
      sortNumber: projectPartnerBudgetSummary.partnerSummary.sortNumber,
      totalBudget: projectPartnerBudgetSummary.totalBudget
    }));
  }

  delete(partner: ProjectBudgetPartner): void {
    Forms.confirm(
      this.dialog,
      {
        title: 'project.application.form.partner.table.action.delete.dialog.header',
        message: {
          i18nKey: 'project.application.form.partner.table.action.delete.dialog.message',
          i18nArguments: {name: partner.abbreviation}
        },
        warnMessage: 'project.application.form.partner.table.action.delete.dialog.warning'
      }).pipe(
        take(1),
        filter(answer => !!answer),
        map(() => this.deletePartner.emit(partner.id)),
      ).subscribe();
  }

  deactivate(partner: ProjectBudgetPartner): void {
    Forms.confirm(
      this.dialog,
      {
        title: 'project.application.form.partner.table.action.deactivate.dialog.header',
        message: {
          i18nKey: 'project.application.form.partner.table.action.deactivate.dialog.message',
          i18nArguments: {name: partner.abbreviation}
        },
        warnMessage: 'project.application.form.partner.table.action.deactivate.dialog.warning'
      }).pipe(
      take(1),
      filter(answer => !!answer),
      map(() => this.deactivatePartner.emit(partner.id)),
    ).subscribe();
  }

}
