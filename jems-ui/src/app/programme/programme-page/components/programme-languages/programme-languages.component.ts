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
import {FormBuilder, FormGroup} from '@angular/forms';
import {InputProgrammeLanguage, OutputProgrammeLanguage} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {SelectionModel} from '@angular/cdk/collections';

@Component({
  selector: 'app-programme-languages',
  templateUrl: './programme-languages.component.html',
  styleUrls: ['./programme-languages.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeLanguagesComponent extends ViewEditForm implements OnInit {

  @Input()
  languages: OutputProgrammeLanguage[];

  @Output()
  saveLanguages: EventEmitter<InputProgrammeLanguage[]> = new EventEmitter<InputProgrammeLanguage[]>();

  displayedColumns: string[] = ['system', 'input', 'name', 'translation'];
  dataSource: MatTableDataSource<OutputProgrammeLanguage>;
  systemLangSelection = new SelectionModel<OutputProgrammeLanguage>(true, []);
  inputLangSelection = new SelectionModel<OutputProgrammeLanguage>(true, []);
  editableLanguageForm = this.formBuilder.group({});

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSource = new MatTableDataSource(this.languages);
    this.enterViewMode();
  }

  getForm(): FormGroup | null {
    return this.editableLanguageForm;
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

  protected enterViewMode(): void {
    super.enterViewMode();
    if (!this.dataSource) {
      return;
    }
    this.dataSource.data = this.languages;
    this.systemLangSelection.clear();
    this.inputLangSelection.clear();
    this.systemLangSelection.select(...this.dataSource.data.filter(element => element.ui));
    this.inputLangSelection.select(...this.dataSource.data.filter(element => element.input));
  }

}
