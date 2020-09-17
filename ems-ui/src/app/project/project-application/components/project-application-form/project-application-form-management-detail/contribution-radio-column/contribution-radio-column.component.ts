import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-contribution-radio-column',
  templateUrl: './contribution-radio-column.component.html',
  styleUrls: ['./contribution-radio-column.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class ContributionRadioColumnComponent {
  @Input()
  disabled: boolean;
  @Output()
  selected = new EventEmitter<string>();
  @Input()
  previousSelection: string;
}
