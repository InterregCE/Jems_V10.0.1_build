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
import {PageOutputProjectPartner, OutputProjectPartner} from '@cat/api'
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {Forms} from '../../../../../common/utils/forms';
import {filter, map, take} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';

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
  partnerPage: PageOutputProjectPartner;
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

  tableConfiguration: TableConfiguration;

  constructor(private dialog: MatDialog) {}

  ngOnInit(): void {
    this.tableConfiguration = new TableConfiguration({
      routerLink: '/app/project/detail/' + this.projectId + '/applicationForm/partner/detail',
      isTableClickable: true,
      columns: [
        {
          displayedColumn: 'project.application.form.partner.table.id',
          elementProperty: 'id',
          sortProperty: 'id'
        },
        {
          displayedColumn: 'project.application.form.partner.table.number',
          elementProperty: 'sortNumber',
          alternativeValueCondition: (element: any) => {return element === null},
          alternativeValue: 'project.application.form.partner.number.info.auto',
          sortProperty: 'sortNumber'
        },
        {
          displayedColumn: 'project.application.form.partner.table.name',
          elementProperty: 'name',
          sortProperty: 'name',
        },
        {
          displayedColumn: 'project.application.form.partner.table.role',
          elementProperty: 'role',
          elementTranslationKey: 'common.label.project.partner.role',
          sortProperty: 'role',
        },
        {
          displayedColumn: ' ',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.deletionCell
        }
      ]
    });
  }

  delete(project: OutputProjectPartner) {
    Forms.confirmDialog(
      this.dialog,
      'project.application.form.partner.table.action.delete.dialog.header',
      'project.application.form.partner.table.action.delete.dialog.message',
      {name: project.name,
        boldWarningMessage: 'project.application.form.partner.table.action.delete.dialog.warning' })
      .pipe(
        take(1),
        filter(answer => !!answer),
        map(() => this.deletePartner.emit(project.id)),
      ).subscribe();
  }
}
