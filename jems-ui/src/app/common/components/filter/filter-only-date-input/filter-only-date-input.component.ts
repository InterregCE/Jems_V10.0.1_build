import {ChangeDetectionStrategy, Component, forwardRef} from '@angular/core';
import {NG_VALUE_ACCESSOR} from '@angular/forms';
import {FilterTextInputComponent} from '@common/components/filter/filter-text-input/filter-text-input.component';
import moment from 'moment';
import {Moment} from 'moment';

@Component({
  selector: 'jems-filter-only-date-input',
  templateUrl: './filter-only-date-input.component.html',
  styleUrls: ['./filter-only-date-input.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => FilterOnlyDateInputComponent),
    multi: true
  }]
})
export class FilterOnlyDateInputComponent extends FilterTextInputComponent {

  override changeValue(value: Moment | null) {
    // without specific null at the end field is removed from form (? not sure why)
    super.changeValue(value?.format(moment.HTML5_FMT.DATE) || null);
  }

}
