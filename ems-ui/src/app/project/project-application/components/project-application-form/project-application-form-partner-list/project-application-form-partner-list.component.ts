import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {MatSort} from '@angular/material/sort';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {PageOutputProjectPartner} from '@cat/api'

@Component({
  selector: 'app-project-application-form-partner-list',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './project-application-form-partner-list.component.html',
  styleUrls: ['./project-application-form-partner-list.component.scss']
})
export class ProjectApplicationFormPartnerListComponent{
  @Input()
  partnerPage: PageOutputProjectPartner;
  @Input()
  pageIndex: number;

  @Output()
  newPageSize: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newPageIndex: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newSort: EventEmitter<Partial<MatSort>> = new EventEmitter<Partial<MatSort>>();

  tableConfiguration: TableConfiguration = new TableConfiguration({
    routerLink: '/partner/',
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
    ]
  });
}
