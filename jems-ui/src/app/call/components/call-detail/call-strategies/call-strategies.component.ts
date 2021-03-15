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
  @Input()
  isApplicant: boolean;
  @Input()
  initialStrategies: OutputProgrammeStrategy[];

  @Output()
  selectionChanged = new EventEmitter<void>();

  isStrategyAlreadySelected(strategy: OutputProgrammeStrategy): boolean {
    const foundStrategy = this.initialStrategies.find(initialStrategy => initialStrategy.strategy === strategy.strategy);
    return !!(foundStrategy && foundStrategy.active);
  }
}
