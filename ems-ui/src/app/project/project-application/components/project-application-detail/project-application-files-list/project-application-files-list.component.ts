import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ActionConfiguration} from '@common/components/table/model/action.configuration';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {DescriptionCellComponent} from '@common/components/table/cell-renderers/description-cell/description-cell.component';
import {combineLatest, Observable} from 'rxjs';
import {take, takeUntil} from 'rxjs/operators';
import {TableComponent} from '@common/components/table/table.component';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {InputProjectFileDescription, OutputProjectFile, PageOutputProjectFile} from '@cat/api';
import {Permission} from '../../../../../security/permissions/permission';
import {BaseComponent} from '@common/components/base-component';

@Component({
  selector: 'app-project-application-files-list',
  templateUrl: './project-application-files-list.component.html',
  styleUrls: ['./project-application-files-list.component.scss']
})
export class ProjectApplicationFilesListComponent extends BaseComponent implements OnInit {
  @Input()
  filePage: PageOutputProjectFile;
  @Input()
  refreshCustomColumns: Observable<null>;
  @Output()
  deleteFile$ = new EventEmitter<OutputProjectFile>();
  @Output()
  downloadFile$ = new EventEmitter<OutputProjectFile>();
  @Output()
  saveDescription$ = new EventEmitter<any>();

  @ViewChild(TableComponent) table: TableComponent;

  tableConfiguration: TableConfiguration = new TableConfiguration({
    routerLink: '/project/',
    isTableClickable: false,
    actionColumn: true,
    actions: [],
    columns: [
      {
        displayedColumn: 'file.table.column.name.name',
        elementProperty: 'name',
        columnType: ColumnType.String,
      },
      {
        displayedColumn: 'file.table.column.name.timestamp',
        elementProperty: 'updated',
        columnType: ColumnType.Date,
      },
      {
        displayedColumn: 'file.table.column.name.username',
        elementProperty: 'author.email',
        columnType: ColumnType.String
      },
      {
        displayedColumn: 'file.table.column.name.description',
        elementProperty: 'description',
        columnType: ColumnType.CustomComponent,
        component: DescriptionCellComponent,
        extraProps: {
          onSave: (value: string, index: number, fileId: number) => this.onSave(value, index, fileId),
          onCancel: (index: number) => this.onCancel(index),
          readOnly: true,
        },
      }
    ]
  });

  editAction = new ActionConfiguration('fas fa-edit', (element: any, index: number) => this.editFileDescription(element, index));
  downloadAction = new ActionConfiguration('fas fa-file-download', (element: OutputProjectFile) => this.downloadFile(element));
  deleteAction = new ActionConfiguration('fas fa-trash', (element: OutputProjectFile) => this.deleteFile(element));

  constructor(private permissionService: PermissionService) {
    super();
  }

  ngOnInit() {
    this.assignActionsToUser();
    this.refreshCustomColumns.subscribe(() => {
      if (this.table) {
        setTimeout(() => this.table.createCustomComponents(), 0);
      }
    })
  }

  downloadFile(element: OutputProjectFile) {
    this.downloadFile$.emit(element);
  }

  editFileDescription(element: any, rowIndex: number) {
    this.table.changeCustomColumnData(rowIndex, {
      onSave: (value: string, index: number, fileId: number) => this.onSave(value, index, fileId),
      onCancel: (index: number) => this.onCancel(index),
      readOnly: false,
    });
  }

  deleteFile(element: OutputProjectFile) {
    this.deleteFile$.emit(element);
  }

  onCancel(rowIndex: number): void {
    this.closeInputFieldAndMakeReadonly(rowIndex);
  }

  onSave(saveValue: string, rowIndex: number, fileId: number): void {
    const descriptionText = {description: saveValue} as InputProjectFileDescription;
    this.saveDescription$.emit({fileIdentifier: fileId, description: descriptionText})
    this.closeInputFieldAndMakeReadonly(rowIndex);
  }

  closeInputFieldAndMakeReadonly(rowIndex: number): void {
    this.table.changeCustomColumnData(rowIndex, {
      onSave: (value: string, index: number, fileId: number) => this.onSave(value, index, fileId),
      onCancel: (index: number) => this.onCancel(index),
      readOnly: true,
    });
  }

  assignActionsToUser(): void {
    combineLatest([
      this.permissionService.hasPermission(Permission.APPLICANT_USER),
      this.permissionService.hasPermission(Permission.PROGRAMME_USER),
      this.permissionService.hasPermission(Permission.ADMINISTRATOR)
    ])
      .pipe(
        take(1),
        takeUntil(this.destroyed$)
      )
      .subscribe(([isApplicant, isProgramme,isAdmin]) => {
        if(isApplicant || isAdmin) {
          this.tableConfiguration.actions = [this.editAction, this.downloadAction, this.deleteAction];
          return;
        }
        if (isProgramme) {
          this.tableConfiguration.actions = [this.downloadAction];
          return;
        }
      });
  }
}
