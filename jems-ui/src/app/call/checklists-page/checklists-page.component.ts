import { ChangeDetectionStrategy, Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { ChecklistsPageStore } from './checklists-page-store.service';
import { FormService } from '@common/components/section/form/form.service';
import { CallChecklistDTO } from '@cat/api';
import { SelectionModel } from '@angular/cdk/collections';
import { combineLatest, Observable } from 'rxjs';
import { catchError, map, take, tap } from 'rxjs/operators';
import { UntilDestroy } from '@ngneat/until-destroy';
import { TableConfiguration } from '@common/components/table/model/table.configuration';
import { ColumnWidth } from '@common/components/table/model/column-width';
import { ColumnType } from '@common/components/table/model/column-type.enum';

@UntilDestroy()
@Component({
  selector: 'jems-call-detail-page',
  templateUrl: './checklists-page.component.html',
  styleUrls: ['./checklists-page.component.scss'],
  providers: [ChecklistsPageStore, FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChecklistsPageComponent implements OnInit {

  selection = new SelectionModel<number>(true, []);

  data$: Observable<{
    checklists: CallChecklistDTO[];
    callIsEditable: boolean;
  }>;

  callIsEditable: boolean;

  tableConfiguration: TableConfiguration;

  @ViewChild('actionsCell', {static: true})
  actionsCell: TemplateRef<any>;

  constructor(public pageStore: ChecklistsPageStore,
              private formService: FormService) {
    this.data$ = combineLatest([
        this.pageStore.checklists$,
        this.pageStore.canEditCall$
    ]).pipe(
        map(([checklists, canEditCall]) => ({
          checklists,
          callIsEditable: canEditCall
        })),
        tap(data => this.resetForm(data.checklists, data.callIsEditable)),
        tap(data => this.callIsEditable = data.callIsEditable)
    );
  }

  ngOnInit(): void {
    this.initializeTableConfiguration();
  }

  private initializeTableConfiguration(): void {
    this.tableConfiguration = new TableConfiguration({
      isTableClickable: false,
      sortable: false,
      columns: [
        {
          displayedColumn: 'common.selected',
          columnWidth: ColumnWidth.SmallColumn,
          customCellTemplate: this.actionsCell,
          clickable: false
        },
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
        }
      ]
    });
  }

  resetForm(checklists: CallChecklistDTO[], isEditable: boolean): void {
    this.selection.clear();
    this.selection.select(...checklists.filter(c => c.selected).map(c => c.id));
    this.formService.setEditable(isEditable);
  }

  onSubmit(): void {
    const checklistIds = this.selection.selected;
    this.pageStore.saveSelectedChecklists(checklistIds).pipe(
        take(1),
        tap(() => this.formService.setSuccess('call.detail.checklists.updated.success')),
        catchError(err => this.formService.setError(err))
    ).subscribe();
  }

  toggleChecklist(checklistId: number): void {
    this.selection.toggle(checklistId);
    this.formChanged();
  }

  isSelected(checklistId: number): boolean {
    return this.selection.isSelected(checklistId);
  }

  formChanged(): void {
    this.formService.setDirty(true);
  }
}
