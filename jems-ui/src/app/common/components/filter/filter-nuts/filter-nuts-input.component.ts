import {ChangeDetectionStrategy, Component, forwardRef, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR} from '@angular/forms';
import {OutputNuts} from '@cat/api';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';

@Component({
  selector: 'jems-filter-nuts-input',
  templateUrl: './filter-nuts-input.component.html',
  styleUrls: ['./filter-nuts-input.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => FilterNutsInputComponent),
    multi: true
  }]
})
export class FilterNutsInputComponent implements ControlValueAccessor, OnChanges, OnInit {

  mySearchControl = new FormControl('');
  options: OutputNutsWithLevel[] = [];
  filteredOptions: Observable<OutputNutsWithLevel[]>;

  valuesFull: OutputNutsWithLevel[] | null = [];
  values: string[] | null = [];

  @Input()
  availableRegions: OutputNuts[];
  @Input()
  label: string;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.availableRegions) {
      this.options = this.toLeveledView(this.availableRegions);
    }
  }

  ngOnInit(): void {
    this.filteredOptions = this.mySearchControl.valueChanges.pipe(
      startWith(''),
      map(value => {
        const name = typeof value === 'string' ? value : '';
        return this._filter(name ).slice(0, 100);
      }),
    );
  }

  displayFn(nuts: OutputNutsWithLevel): string {
    return nuts && nuts.code ? nuts.code : '?';
  }

  private _filter(name: string): OutputNutsWithLevel[] {
    const alreadySelectedValues = this.values || [];
    const filterValue = name.toLowerCase();
    return this.options
      // search by key
      .filter(option => option.code.toLowerCase().startsWith(filterValue)
        || (option.level === 2 && option.code.toLowerCase().startsWith(filterValue.substring(0, 2))))
      // remove already selected
      .filter(option =>
        alreadySelectedValues.map(prefix => !option.code.startsWith(prefix)).every(Boolean));
  }

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

  removeFromFilters(value: OutputNutsWithLevel): void {
    this.valuesFull?.splice(this.valuesFull.indexOf(value), 1);
    this.values?.splice(this.values.indexOf(value.code), 1);
    this.mySearchControl.setValue('');
    this.onChange(this.values);
  }

  addToFilters(value: OutputNutsWithLevel): void {
    if (this.values) {
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      this.valuesFull = [...this.valuesFull!!, value];
      this.values = [...this.values, value.code];
    } else {
      this.valuesFull = [value];
      this.values = [value.code];
    }
    this.mySearchControl.setValue('');
    this.onChange(this.values);
  }

  private toLeveledView(nuts: OutputNuts[]): OutputNutsWithLevel[] {
    return nuts.flatMap(country => ([
      ...[this.toLevelNuts(country, 2)],
      ...country.areas.flatMap(nuts1 => ([
        ...[this.toLevelNuts(nuts1, 3)],
        ...nuts1.areas.flatMap(nuts2 => ([
          ...[this.toLevelNuts(nuts2, 4)],
          ...nuts2.areas.map(nuts3 => this.toLevelNuts(nuts3, 5)),
        ])),
      ])),
    ]));
  }

  private toLevelNuts(nuts: OutputNuts, level: number): OutputNutsWithLevel {
    return {
      code: nuts.code,
      title: nuts.title,
      level,
    };
  }
}

export interface OutputNutsWithLevel {
  code: string;
  title: string;
  level: number;
}
