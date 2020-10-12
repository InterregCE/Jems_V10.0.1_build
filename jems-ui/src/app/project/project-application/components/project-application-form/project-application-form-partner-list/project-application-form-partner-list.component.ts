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
import {OutputProjectPartner, PageOutputProjectPartner} from '@cat/api'
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {Forms} from '../../../../../common/utils/forms';
import {filter, map, take} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {Router} from '@angular/router';
import {ProjectApplicationFormPartnerDetailComponent} from '../../../containers/project-application-form-page/project-application-form-partner-section/project-application-form-partner-detail/project-application-form-partner-detail.component';
import {TabService} from '../../../../../common/services/tab.service';

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

  @ViewChild('budgetCell', {static: true})
  budgetCell: TemplateRef<any>;

  tableConfiguration: TableConfiguration;

  constructor(private dialog: MatDialog,
              private router: Router,
              private tabService: TabService) {
  }

  ngOnInit(): void {
    this.tableConfiguration = new TableConfiguration({
      routerLink: this.getPartnerLink(),
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
          alternativeValueCondition: (element: any) => {
            return element === null
          },
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
        },
        {
          displayedColumn: 'project.application.form.partner.list.budget',
          columnType: ColumnType.CustomComponent,
          customCellTemplate: this.budgetCell
        }
      ]
    });
  }

  delete(partner: OutputProjectPartner) {
    Forms.confirmDialog(
      this.dialog,
      'project.application.form.partner.table.action.delete.dialog.header',
      'project.application.form.partner.table.action.delete.dialog.message',
      {
        name: partner.name,
        boldWarningMessage: 'project.application.form.partner.table.action.delete.dialog.warning'
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        map(() => this.deletePartner.emit(partner.id)),
      ).subscribe();
  }

  goToPartnerBudget(id: number): void {
    this.router.navigate([this.getPartnerLink(), id]);
    this.tabService.changeTab(ProjectApplicationFormPartnerDetailComponent.name + id, 4);
  }

  private getPartnerLink(): string {
    return '/app/project/detail/' + this.projectId + '/applicationForm/partner/detail';
  }

}
