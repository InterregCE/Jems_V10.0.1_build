import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl, ValidationErrors} from '@angular/forms';
import {MatButtonToggleChange} from '@angular/material/button-toggle';
import {Observable} from 'rxjs';
import {InputTranslation, OutputProgrammeLanguage} from '@cat/api';
import {takeUntil} from 'rxjs/operators';
import {BaseComponent} from '@common/components/base-component';

@Component({
  selector: 'app-multi-language-field',
  templateUrl: './multi-language-field.component.html',
  styleUrls: ['./multi-language-field.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MultiLanguageFieldComponent extends BaseComponent implements OnInit {
  @Input()
  label: string;
  @Input()
  errors: ValidationErrors | null;
  @Input()
  messages: {[key: string]: string};
  @Input()
  characterLimit: number;
  @Input()
  disabled: boolean;
  @Input()
  control: FormControl;
  @Input()
  languages: string[];
  @Input()
  currentControlValues: InputTranslation[];
  @Input()
  currentLanguage$: Observable<OutputProgrammeLanguage.CodeEnum>;
  @Input()
  validity: Map<OutputProgrammeLanguage.CodeEnum, boolean>;
  @Input()
  customStyle?: string;
  @Input()
  minRows ? = 3;
  @Input()
  maxRows ? = 50;
  @Input()
  changeControlValue$: Observable<InputTranslation[]>;
  @Output()
  changeLanguage: EventEmitter<OutputProgrammeLanguage.CodeEnum> = new EventEmitter<OutputProgrammeLanguage.CodeEnum>();

  currentLanguage: OutputProgrammeLanguage.CodeEnum;
  isFieldFocused = false;
  selectedLanguage: string;

  ngOnInit(): void {
    this.control.setValue(this.currentControlValues[0]?.translation);
    this.selectedLanguage = this.languages[0];
    this.currentLanguage$
      .pipe(
        takeUntil(this.destroyed$)
      )
      .subscribe(language => {
      this.changeFieldValueBasedOnLanguage(language, false);
    });
    this.changeControlValue$
      .pipe(
        takeUntil(this.destroyed$)
      )
      .subscribe((values) => {
        this.changeFieldValuesOnReset(values);
    });
  }

  changeCurrentSelectedLanguage($event: MatButtonToggleChange): void {
    this.changeFieldValueBasedOnLanguage($event.value, true);
  }

  storeChanges(): void {
    this.currentControlValues.forEach(value => {
      if (value.language === this.currentLanguage) {
        value.translation = this.control.value;
      }
    });
    this.validity.set(this.currentLanguage, this.control.invalid);
    this.isFieldFocused = false;
  }

  changeFocus(): void {
    this.isFieldFocused = true;
  }

  private changeFieldValueBasedOnLanguage(language: OutputProgrammeLanguage.CodeEnum, isButtonEvent: boolean): void {
    const currentLanguageValue = this.currentControlValues.find(value => value.language === language);
    this.control.setValue(currentLanguageValue?.translation || '');
    this.validity.set(language, this.control.invalid);
    if (isButtonEvent) {
      this.changeLanguage.emit(language);
    } else {
      this.currentLanguage = language;
      this.selectedLanguage = language;
    }
  }

  private changeFieldValuesOnReset(values: InputTranslation[]): void {
    this.currentControlValues = values;
    const currentLanguageValue = this.currentControlValues.find(value => value.language === this.currentLanguage);
    this.control.setValue(currentLanguageValue?.translation || '');
  }
}
