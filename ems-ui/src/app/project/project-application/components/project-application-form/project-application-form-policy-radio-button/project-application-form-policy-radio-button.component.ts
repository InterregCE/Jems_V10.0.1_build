import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {InputProjectData} from '@cat/api'

@Component({
  selector: 'app-project-application-form-policy-radio-button',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './project-application-form-policy-radio-button.component.html',
  styleUrls: ['./project-application-form-policy-radio-button.component.scss']
})
export class ProjectApplicationFormPolicyRadioButtonComponent{
  @Input()
  objectives: InputProjectData.SpecificObjectiveEnum[];
  @Input()
  checked: InputProjectData.SpecificObjectiveEnum;
  @Output()
  selected = new EventEmitter<InputProjectData.SpecificObjectiveEnum>();
}
