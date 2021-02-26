import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {FormGroup} from '@angular/forms';
import {ProgrammeLanguageDTO} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {SelectionModel} from '@angular/cdk/collections';
import {ProgrammeEditableStateStore} from '../../services/programme-editable-state-store.service';
import {UntilDestroy} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-programme-languages',
  templateUrl: './programme-languages.component.html',
  styleUrls: ['./programme-languages.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeLanguagesComponent extends ViewEditForm implements OnInit {

  @Input()
  languages: ProgrammeLanguageDTO[];

  @Output()
  saveLanguages: EventEmitter<ProgrammeLanguageDTO[]> = new EventEmitter<ProgrammeLanguageDTO[]>();

  displayedColumns: string[] = ['system', 'input', 'name', 'translation'];
  dataSource: MatTableDataSource<ProgrammeLanguageDTO>;
  initialSystemLangSelection = new SelectionModel<ProgrammeLanguageDTO>(true, []);
  initialInputLangSelection = new SelectionModel<ProgrammeLanguageDTO>(true, []);
  systemLangSelection = new SelectionModel<ProgrammeLanguageDTO>(true, []);
  inputLangSelection = new SelectionModel<ProgrammeLanguageDTO>(true, []);


  constructor(protected changeDetectorRef: ChangeDetectorRef,
              public programmeEditableStateStore: ProgrammeEditableStateStore) {
    super(changeDetectorRef);

    this.programmeEditableStateStore.init();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSource = new MatTableDataSource(this.languages);
    this.enterViewMode();
  }

  getForm(): FormGroup | null {
    return null;
  }

  onSubmit(): void {
   this.saveLanguages.emit(
    this.dataSource.data
       .map(element => ({
         ui: this.systemLangSelection.isSelected(element),
         input: this.inputLangSelection.isSelected(element),
         code: element.code,
         fallback: element.fallback
       }))
   );
  }

  isSubmitDisabled(): boolean {
    return this.systemLangSelection.selected.length < 1
      || this.systemLangSelection.selected.length > 8
      || this.inputLangSelection.selected.length < 1
      || this.inputLangSelection.selected.length > 4
      || this.submitted;
  }

  protected enterViewMode(): void {
    if (!this.dataSource) {
      return;
    }
    this.dataSource.data = this.languages;
    this.initialSystemLangSelection.clear();
    this.initialInputLangSelection.clear();
    this.systemLangSelection.clear();
    this.inputLangSelection.clear();
    this.initialSystemLangSelection.select(...this.dataSource.data.filter(element => element.ui));
    this.initialInputLangSelection.select(...this.dataSource.data.filter(element => element.input));
    this.systemLangSelection.select(...this.dataSource.data.filter(element => element.ui));
    this.inputLangSelection.select(...this.dataSource.data.filter(element => element.input));
  }

}
