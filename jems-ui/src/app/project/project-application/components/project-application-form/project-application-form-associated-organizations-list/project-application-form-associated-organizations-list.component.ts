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
import {MatDialog} from '@angular/material/dialog';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {OutputProjectAssociatedOrganization, PageOutputProjectAssociatedOrganization} from '@cat/api';
import {Forms} from '../../../../../common/utils/forms';
import {filter, map, take} from 'rxjs/operators';

@Component({
  selector: 'app-project-application-form-associated-organizations-list',
  templateUrl: './project-application-form-associated-organizations-list.component.html',
  styleUrls: ['./project-application-form-associated-organizations-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormAssociatedOrganizationsListComponent implements OnInit {
  @Input()
  projectId: number;
  @Input()
  associatedOrganizationPage: PageOutputProjectAssociatedOrganization;
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
  deleteAssociatedOrganization = new EventEmitter<number>();

  @ViewChild('deletionCellAssociatedOrganization', {static: true})
  deletionCell: TemplateRef<any>;

  tableConfiguration: TableConfiguration;

  constructor(private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.tableConfiguration = new TableConfiguration({
      routerLink: `/app/project/detail/${this.projectId}/applicationFormAssociatedOrganization/detail`,
      isTableClickable: true,
      columns: [
        {
          displayedColumn: 'project.application.form.associatedOrganization.table.number',
          elementProperty: 'sortNumber',
          i18nFixedKey: 'project.organization.number.format.short',
          i18nArgs: (element: any) => ({sortNumber: element.sortNumber}),
        },
        {
          displayedColumn: 'project.application.form.associatedOrganization.table.associatedOrganization',
          elementProperty: 'nameInOriginalLanguage',
          sortProperty: 'nameInOriginalLanguage',
        },
        {
          displayedColumn: 'project.application.form.associatedOrganization.table.partner',
          elementProperty: 'partnerAbbreviation',
          sortProperty: 'partner.abbreviation',
        },
        {
          displayedColumn: ' ',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.deletionCell
        }
      ]
    });
  }

  delete(associatedOrganization: OutputProjectAssociatedOrganization): void {
    Forms.confirm(
      this.dialog,
      {
        title: 'project.application.form.associatedOrganization.table.action.delete.dialog.header',
        message: {
          i18nKey: 'project.application.form.associatedOrganization.table.action.delete.dialog.message',
          i18nArguments: {name: associatedOrganization.nameInOriginalLanguage}
        },
        warnMessage: 'project.application.form.associatedOrganization.table.action.delete.dialog.warning'
      }).pipe(
      take(1),
      filter(answer => !!answer),
      map(() => this.deleteAssociatedOrganization.emit(associatedOrganization.id)),
    ).subscribe();
  }

}
