import {Component, forwardRef, Input} from '@angular/core';
import {NG_VALUE_ACCESSOR, ControlValueAccessor} from '@angular/forms';

@Component({
  selector: 'jems-filter-list-input',
  templateUrl: './filter-list-input.component.html',
  styleUrls: ['./filter-list-input.component.scss'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => FilterListInputComponent),
    multi: true
  }]
})
export class FilterListInputComponent implements ControlValueAccessor {

  @Input()
  type: 'number' | 'text' = 'text';
  @Input()
  label: string;
  @Input()
  placeholder: string;

  values: any[];
  currentValue: any;

  onChange = (value: any) => {
    // Intentionally left blank
  };

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    // Intentionally left blank
  }

  writeValue(obj: any[]): void {
    this.values = obj;
  }

  removeFromFilters(value: any): void {
    this.values.splice(this.values.indexOf(value), 1);
    this.onChange(this.values);
  }

  addToFilters(value: any): void {
    if (this.values.indexOf(value) < 0) {
      this.values.push(value);
    }
    this.onChange(this.values);
  }

}
