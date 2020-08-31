import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {MatSort} from '@angular/material/sort';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {PageOutputWorkPackageSimple} from '@cat/api'

@Component({
  selector: 'app-project-application-form-work-packages-list',
  templateUrl: './project-application-form-work-packages-list.component.html',
  styleUrls: ['./project-application-form-work-packages-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormWorkPackagesListComponent {
  @Input()
  workPackagePage: PageOutputWorkPackageSimple;
  @Input()
  pageIndex: number;
  @Output()
  newPageSize: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newPageIndex: EventEmitter<number> = new EventEmitter<number>();
  @Output()
  newSort: EventEmitter<Partial<MatSort>> = new EventEmitter<Partial<MatSort>>();

  tableConfiguration: TableConfiguration = new TableConfiguration({
    routerLink: '/workPackage/',
    isTableClickable: true,
    columns: [
      {
        displayedColumn: 'project.application.form.work.package.number',
        elementProperty: 'number',
        sortProperty: 'number'
      },
      {
        displayedColumn: 'project.application.form.work.package.name',
        elementProperty: 'name',
        sortProperty: 'name',
      },
    ]
  });
}
