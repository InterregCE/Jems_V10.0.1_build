import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {InputProjectData} from '@cat/api'
import {FormGroup} from '@angular/forms';

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
  applicationForm: FormGroup
  @Output()
  selected = new EventEmitter<InputProjectData.SpecificObjectiveEnum>();

  specificObjectiveErrors = {
    required: 'project.objective.should.not.be.empty'
  }
}
