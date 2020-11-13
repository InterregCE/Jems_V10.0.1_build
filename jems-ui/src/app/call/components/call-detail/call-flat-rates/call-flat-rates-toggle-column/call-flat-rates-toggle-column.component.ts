import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-call-flat-rates-toggle-column',
  templateUrl: './call-flat-rates-toggle-column.component.html',
  styleUrls: ['./call-flat-rates-toggle-column.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallFlatRatesToggleColumnComponent {
  @Output()
  selected = new EventEmitter<string>();
  @Input()
  previousSelection: string;
  @Input()
  disabled: boolean;
}
