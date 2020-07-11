import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
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
import {combineLatest} from 'rxjs';
import {PermissionService} from '../../../../../security/permissions/permission.service';
import {OutputProjectFile, OutputProjectStatus, PageOutputProjectFile} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';
import {MatSort} from '@angular/material/sort';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';
import {takeUntil} from 'rxjs/operators';
import {FormState} from '@common/components/forms/form-state';
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
  editActionVisible = false;
  downloadActionVisible = true;
  deleteActionVisible = false;

  constructor(private permissionService: PermissionService,
              private projectStore: ProjectStore,
              private changeDetectorRef: ChangeDetectorRef) {
    super();
  }

  ngOnInit() {
    this.assignActionsToUser();

    this.tableConfiguration = new TableConfiguration({
      routerLink: '/project/',
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
          columnType: ColumnType.Date,
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
          displayedColumn: 'Actions',
          customCellTemplate: this.actionsCell
        }
      ]
    });
  }

  private assignActionsToUser(): void {
    combineLatest([
      this.permissionService.hasPermission(Permission.APPLICANT_USER),
      this.permissionService.hasPermission(Permission.ADMINISTRATOR),
      this.projectStore.getStatus()
    ])
      .pipe(
        takeUntil(this.destroyed$)
      )
      .subscribe(([isApplicant, isAdmin, projectStatus]) => {
        this.editActionVisible = isAdmin;
        this.deleteActionVisible = isAdmin;
        this.downloadActionVisible = true;

        if (isApplicant) {
          this.editActionVisible = projectStatus === OutputProjectStatus.StatusEnum.DRAFT;
          this.deleteActionVisible = projectStatus === OutputProjectStatus.StatusEnum.DRAFT;
        }
        this.changeDetectorRef.markForCheck();
      });
  }
}
