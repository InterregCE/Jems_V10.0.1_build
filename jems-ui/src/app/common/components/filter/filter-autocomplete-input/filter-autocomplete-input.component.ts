import {ChangeDetectionStrategy, Component, forwardRef, Input} from '@angular/core';
import {FilterListInputComponent} from '@common/components/filter/filter-list-input/filter-list-input.component';
import {NG_VALUE_ACCESSOR} from '@angular/forms';

@Component({
  selector: 'jems-filter-autocomplete-input',
  templateUrl: './filter-autocomplete-input.component.html',
  styleUrls: ['./filter-autocomplete-input.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
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

  getNotSelectedOptions(inputValue: string): any[] {
    const notSelected = [...this.options.keys()]
      .filter(option => this.values.indexOf(option) === -1);
    return this.filterable
      ? notSelected.filter(option => this.options.get(option)?.toUpperCase().includes(inputValue.toUpperCase()))
      : notSelected;
  }
}
