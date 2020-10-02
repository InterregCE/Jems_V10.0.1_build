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
import {InputProgrammeData, OutputProgrammeData, SystemLanguageSelection} from '@cat/api';
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
  programme: OutputProgrammeData;

  @Output()
  saveProgrammeData: EventEmitter<InputProgrammeData> = new EventEmitter<InputProgrammeData>();

  displayedColumns: string[] = ['selection', 'name', 'translation'];
  langselection = new SelectionModel<SystemLanguageSelection>(true, []);
  dataSource: MatTableDataSource<SystemLanguageSelection>;

  editableLanguageForm = this.formBuilder.group({});

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSource = new MatTableDataSource(this.programme?.systemLanguageSelections);
    this.enterViewMode();
  }

  getForm(): FormGroup | null {
    return this.editableLanguageForm;
  }

  onSubmit(): void {
    this.saveProgrammeData.emit({
      cci: this.programme.cci,
      title: this.programme.title,
      version: this.programme.version,
      firstYear: this.programme.firstYear,
      lastYear: this.programme.lastYear,
      eligibleFrom: this.programme.eligibleFrom,
      eligibleUntil: this.programme.eligibleUntil,
      commissionDecisionNumber: this.programme.commissionDecisionNumber,
      commissionDecisionDate: this.programme.commissionDecisionDate,
      programmeAmendingDecisionNumber: this.programme.programmeAmendingDecisionNumber,
      programmeAmendingDecisionDate: this.programme.programmeAmendingDecisionDate,
      systemLanguageSelections: this.dataSource.data
        .map(element => ({
          selected: this.langselection.isSelected(element),
          name: element.name,
          translationKey: element.translationKey
        }))
    });
  }

  protected enterViewMode(): void {
    super.enterViewMode();
    if (!this.dataSource) {
      return;
    }
    this.dataSource.data = this.programme?.systemLanguageSelections;
    this.langselection.clear();
    this.langselection.select(...this.dataSource.data.filter(element => element.selected));
  }

}
