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
import {ProjectPartnerDTO, PageProjectPartnerDTO} from '@cat/api';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {Forms} from '@common/utils/forms';
import {filter, map, take} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {RoutingService} from '@common/services/routing.service';

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
  partnerPage: PageProjectPartnerDTO;
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

  constructor(private dialog: MatDialog) {
  }

  ngOnInit(): void {
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
          displayedColumn: 'project.application.form.partner.list.budget',
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

  delete(partner: ProjectPartnerDTO): void {
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
