import {ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfiguration} from '@common/components/table/model/table.configuration';
import {ProgrammeChecklistListPageStore} from './programme-checklist-list-page-store.service';
import {ProgrammeChecklistDetailPageStore} from './programme-checklist-detail-page/programme-checklist-detail-page-store.service';
import {ColumnType} from '@common/components/table/model/column-type.enum';
import {ColumnWidth} from '@common/components/table/model/column-width';
import {filter, switchMap, take, tap} from 'rxjs/operators';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {ProgrammeChecklistDetailDTO} from '@cat/api';
import {RoutingService} from "@common/services/routing.service";
import {ActivatedRoute} from '@angular/router';
import {ProgrammePageSidenavService} from "../programme-page/services/programme-page-sidenav.service";

@Component({
  selector: 'jems-programme-checklist-list-page',
  templateUrl: './programme-checklist-list-page.component.html',
  styleUrls: ['./programme-checklist-list-page.component.scss'],
  providers: [ProgrammeChecklistListPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeChecklistListPageComponent implements OnInit {
  DETAIL_PATH = ProgrammeChecklistDetailPageStore.CHECKLIST_DETAIL_PATH;

  checklists$ = this.pageStore.checklists$;
  tableConfiguration: TableConfiguration;

  @ViewChild('actionCell', {static: true})
  actionCell: TemplateRef<any>;

  @ViewChild('copyCell', {static: true})
  copyCell: TemplateRef<any>;

  constructor(public pageStore: ProgrammeChecklistListPageStore,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute,
              private dialog: MatDialog,
              private programmePageSidenavService: ProgrammePageSidenavService) { }

  ngOnInit(): void {
    this.pageStore.canEditProgramme$
      .pipe(
        take(1),
        tap(canEditProgramme => this.initializeTableConfiguration(canEditProgramme))
      ).subscribe();
  }

  delete(checklist: ProgrammeChecklistDetailDTO): void {
    Forms.confirm(
      this.dialog, {
        title: 'programme.checklists.delete.dialog.header',
        message: {i18nKey: 'programme.checklists.delete.confirm', i18nArguments: {name: checklist.name}}
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        switchMap(() => this.pageStore.deleteChecklist(checklist.id)),
      ).subscribe();
  }

  copy(checklist: ProgrammeChecklistDetailDTO): void {
    Forms.confirm(
      this.dialog, {
        title: 'programme.checklists.copy.dialog.header',
        message: {i18nKey: 'programme.checklists.copy.confirm', i18nArguments: {name: checklist.name}}
      })
      .pipe(
        take(1),
        filter(answer => !!answer),
        switchMap(() => this.pageStore.cloneChecklist(checklist.id)),
        tap(cloneChecklistId =>
          this.routingService.navigate([cloneChecklistId], {relativeTo: this.activatedRoute})
        )
      ).subscribe();
  }

  private initializeTableConfiguration(canEditProgramme: boolean): void {
    this.tableConfiguration = new TableConfiguration({
      isTableClickable: true,
      sortable: false,
      routerLink: this.DETAIL_PATH,
      columns: [
        {
          displayedColumn: 'common.type',
          elementTranslationKey: 'programme.checklists.type',
          elementProperty: 'type',
          columnWidth: ColumnWidth.DateColumn,
          sortProperty: 'type',
        },
        {
          displayedColumn: 'common.name',
          elementProperty: 'name',
          columnWidth: ColumnWidth.extraWideColumn,
          sortProperty: 'name',
        },
        {
          displayedColumn: 'programme.checklists.modification.date',
          elementProperty: 'lastModificationDate',
          columnType: ColumnType.DateColumn,
          columnWidth: ColumnWidth.DateColumn,
          sortProperty: 'lastModificationDate',
        },
        ...canEditProgramme ? [{
          displayedColumn: 'common.action',
          customCellTemplate: this.actionCell,
          columnWidth: ColumnWidth.IdColumn
        }] : [],
      ]
    });
  }
}
