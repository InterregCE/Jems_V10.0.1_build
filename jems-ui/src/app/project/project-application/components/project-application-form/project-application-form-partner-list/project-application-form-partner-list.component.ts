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
import {PageProjectBudgetPartnerSummaryDTO} from '@cat/api';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {Forms} from '@common/utils/forms';
import {filter, map, take, tap} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import '@angular/common/locales/global/de';
import {ProjectBudgetPartner} from '@project/model/ProjectBudgetPartner';
import {Observable} from 'rxjs';

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

  @ViewChild('deletionCell', {static: true})
  deletionCell: TemplateRef<any>;

  @ViewChild('budgetCell', {static: true})
  budgetCell: TemplateRef<any>;

  tableConfiguration: TableConfiguration;

  tableRows$: Observable<ProjectBudgetPartner[]>;

  totalElements = 0;


  constructor(private dialog: MatDialog) {
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
          sortProperty: 'sortNumber'
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
          sortProperty: 'nuts',
        },
        {
          displayedColumn: 'project.partner.coFinancing.total',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.budgetCell
        },
        {
          displayedColumn: ' ',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.deletionCell
        }
      ]
    });
  }

  getProjectPartnerSummary(projectPartnerSummary: PageProjectBudgetPartnerSummaryDTO): ProjectBudgetPartner[]{
    return projectPartnerSummary.content.map(projectPartnerBudgetSummary => ( {
      id: projectPartnerBudgetSummary.partnerSummary.id,
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

}
