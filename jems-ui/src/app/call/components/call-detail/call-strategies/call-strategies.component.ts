import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {OutputProgrammeStrategy} from '@cat/api';

@Component({
  selector: 'app-call-strategies',
  templateUrl: './call-strategies.component.html',
  styleUrls: ['./call-strategies.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallStrategiesComponent {
  @Input()
  disabled: boolean;
  @Input()
  strategies: OutputProgrammeStrategy[];

  @Output()
  selectionChanged = new EventEmitter<void>();
}
