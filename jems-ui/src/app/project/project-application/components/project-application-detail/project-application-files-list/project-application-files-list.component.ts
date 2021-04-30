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
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {ProjectDetailDTO, OutputProjectFile, PageOutputProjectFile} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';
import {MatSort} from '@angular/material/sort';
import {filter, map, take, takeUntil} from 'rxjs/operators';
import {FormState} from '@common/components/forms/form-state';
import {Forms} from '../../../../../common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {Permission} from '../../../../../security/permissions/permission';

@Component({
  selector: 'app-project-application-files-list',
  templateUrl: './project-application-files-list.component.html',
  styleUrls: ['./project-application-files-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFilesListComponent extends BaseComponent implements OnInit {
  FormState = FormState;

  @Input()
  filePage: PageOutputProjectFile;
  @Input()
  pageIndex: number;
  @Input()
  project: ProjectDetailDTO;
  @Input()
  fundingDecisionDefined: boolean;
  @Input()
  permission: Permission;

  @Output()
  deleteFile = new EventEmitter<OutputProjectFile>();
  @Output()
  downloadFile = new EventEmitter<OutputProjectFile>();
  @Output()
  saveDescription = new EventEmitter<OutputProjectFile>();
  @Output()
  newPageSize = new EventEmitter<number>();
  @Output()
  newPageIndex = new EventEmitter<number>();
  @Output()
  newSort = new EventEmitter<Partial<MatSort>>();

  @ViewChild('descriptionCell', {static: true})
  descriptionCell: TemplateRef<any>;
  @ViewChild('actionsCell', {static: true})
  actionsCell: TemplateRef<any>;

  tableConfiguration: TableConfiguration;
  descriptionState = new Map<number, FormState>();

  constructor(private dialog: MatDialog) {
    super();
  }

  ngOnInit(): void {
    this.tableConfiguration = new TableConfiguration({
      routerLink: '/app/project/',
      isTableClickable: false,
      columns: [
        {
          displayedColumn: 'file.table.column.name.name',
          elementProperty: 'name',
          sortProperty: 'name'
        },
        {
          displayedColumn: 'file.table.column.name.timestamp',
          elementProperty: 'updated',
          columnType: ColumnType.DateColumn,
          sortProperty: 'updated'
        },
        {
          displayedColumn: 'file.table.column.name.username',
          elementProperty: 'author.email',
          sortProperty: 'author.email'
        },
        {
          displayedColumn: 'file.table.column.name.description',
          elementProperty: 'description',
          sortProperty: 'description',
          customCellTemplate: this.descriptionCell
        },
        {
          displayedColumn: 'file.table.column.name.action',
          customCellTemplate: this.actionsCell
        }
      ]
    });
  }

  delete(file: OutputProjectFile): void {
    Forms.confirmDialog(
      this.dialog,
      file.name,
      'file.dialog.message',
      {name: file.name})
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        filter(answer => !!answer),
        map(() => this.deleteFile.emit(file)),
      ).subscribe();
  }
}
