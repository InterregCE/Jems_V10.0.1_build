import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {OutputProgrammeStrategy} from '@cat/api';
import {SelectionModel} from '@angular/cdk/collections';


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
  selection: SelectionModel<OutputProgrammeStrategy>;
}
