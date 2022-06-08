import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {Alert} from '@common/components/forms/alert';

@Component({
  selector: 'jems-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AlertComponent {
  Alert = Alert;

  @Input()
  show = false;

  @Input()
  closable = true;

  @Input()
  type: Alert;

  @Input()
  disableMargins = false;

  @Output()
  closed = new EventEmitter<void>();

}
