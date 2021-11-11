import {ChangeDetectionStrategy, Component, forwardRef, Input} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';

@Component({
  selector: 'app-filter-text-input',
  templateUrl: './filter-text-input.component.html',
  styleUrls: ['./filter-text-input.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => FilterTextInputComponent),
    multi: true
  }]
})
export class FilterTextInputComponent implements ControlValueAccessor {

  @Input()
  type: 'number' | 'text' = 'text';
  @Input()
  label: string;
  @Input()
  placeholder: string;

  value: any;
  onChange = (value: any) => {
    // Intentionally left blank
  };

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    // Intentionally left blank
  }

  writeValue(obj: any): void {
    this.value = obj;
  }

  changeValue(value: any): void {
    this.writeValue(value);
    this.onChange(value);
  }

}
