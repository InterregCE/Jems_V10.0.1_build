import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'jems-contribution-toggle-column',
  templateUrl: './contribution-toggle-column.component.html',
  styleUrls: ['./contribution-toggle-column.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})

export class ContributionToggleColumnComponent {
  @Input()
  disabled: boolean;
  @Output()
  selected = new EventEmitter<string>();
  @Input()
  previousSelection: string;
}
