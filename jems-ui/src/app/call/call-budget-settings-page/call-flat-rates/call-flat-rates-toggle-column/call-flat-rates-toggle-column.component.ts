import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'jems-call-flat-rates-toggle-column',
  templateUrl: './call-flat-rates-toggle-column.component.html',
  styleUrls: ['./call-flat-rates-toggle-column.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallFlatRatesToggleColumnComponent {
  @Output()
  selected = new EventEmitter<boolean>();
  @Input()
  previousSelection: boolean;
  @Input()
  disabled: boolean;
}
