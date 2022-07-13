import {Component, forwardRef, Input} from '@angular/core';
import {FilterListInputComponent} from '@common/components/filter/filter-list-input/filter-list-input.component';
import {NG_VALUE_ACCESSOR} from '@angular/forms';
import {PartiallyLockableOption} from '@common/components/filter/filter-autocomplete-input/partially-lockable-option';

@Component({
  selector: 'jems-filter-autocomplete-input',
  templateUrl: './filter-autocomplete-input.component.html',
  styleUrls: ['./filter-autocomplete-input.component.scss'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => FilterAutocompleteInputComponent),
    multi: true
  }]
})
export class FilterAutocompleteInputComponent extends FilterListInputComponent {

  @Input()
  options: Map<any, string>;
  @Input()
  filterable = true;
  @Input()
  formFieldWidth: any = 'xx-large';
  @Input()
  isDisabled: boolean;
  @Input()
  hasPartialLockingOfValues: boolean;

  disabled = false;

  getNotSelectedOptions(inputValue: string): any[] {
    const notSelected = [...this.options.keys()]
      .filter(option => this.values.indexOf(option) === -1);
    return this.filterable
      ? notSelected.filter(option => this.options.get(option)?.toUpperCase().includes(inputValue.toUpperCase()))
      : notSelected;
  }

  getNotSelectedOptionsForPartiallyLockedData(inputValue: string): any[] {
    const textValues = this.values.map(it => it.value);
    const notSelected = [...this.options.keys()]
      .filter(option => textValues.indexOf(option.value) === -1);
    return this.filterable
      ? notSelected.filter(option => this.options.get(option)?.toUpperCase().includes(inputValue.toUpperCase())).map(it => it.value)
      : notSelected.map(it => it.value);
  }

  setDisabledState(isDisabled: boolean) {
    this.disabled = isDisabled;
  }

  addToFiltersForPartiallyLockedData(value: any): void {
    const textValues = this.values.map(it => it.value);
    if (textValues.indexOf(value) < 0) {
      this.values.push({value, canBeDeleted: true} as PartiallyLockableOption);
    }
    this.onChange(this.values);
  }
}
