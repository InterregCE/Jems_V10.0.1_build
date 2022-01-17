import {ChangeDetectionStrategy, Component, forwardRef} from '@angular/core';
import {NG_VALUE_ACCESSOR} from '@angular/forms';
import {FilterTextInputComponent} from '@common/components/filter/filter-text-input/filter-text-input.component';

@Component({
  selector: 'app-filter-date-input',
  templateUrl: './filter-date-input.component.html',
  styleUrls: ['./filter-date-input.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => FilterDateInputComponent),
    multi: true
  }]
})
export class FilterDateInputComponent extends FilterTextInputComponent {
}
