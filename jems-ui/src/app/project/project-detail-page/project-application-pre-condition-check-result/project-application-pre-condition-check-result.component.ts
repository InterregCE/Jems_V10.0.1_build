import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {PreConditionCheckResult} from '../../model/plugin/PreConditionCheckResult';
import {PluginMessageType} from '../../model/plugin/PluginMessageType';

@Component({
  selector: 'jems-project-application-pre-condition-check-result',
  templateUrl: './project-application-pre-condition-check-result.component.html',
  styleUrls: ['./project-application-pre-condition-check-result.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationPreConditionCheckResultComponent {

  @Input()
  preConditionCheckResult: PreConditionCheckResult | null;

  PluginMessageType = PluginMessageType;
}
