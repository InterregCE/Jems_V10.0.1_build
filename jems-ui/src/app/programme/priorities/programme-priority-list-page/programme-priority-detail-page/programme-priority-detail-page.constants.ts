import {FormArray, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators} from '@angular/forms';
import {AppControl} from '@common/components/section/form/app-control';

export class ProgrammePriorityDetailPageConstants {

  public static CODE: AppControl = {
    name: 'code',
    errorMessages: {
      required: 'programme.priority.code.should.not.be.empty'
    },
    validators: [Validators.maxLength(50), Validators.required, Validators.pattern(/(?!^\s+$)^.*$/m)]
  };

  public static TITLE: AppControl = {
    name: 'title',
    validators: [Validators.maxLength(300), Validators.required]
  };

  public static OBJECTIVE: AppControl = {
    name: 'objective',
    errorMessages: {
      required: 'programme.priority.objective.should.not.be.empty'
    },
    validators: [Validators.required]
  };

  public static SPECIFIC_OBJECTIVES: AppControl = {
    name: 'specificObjectives',
    errorMessages: {
      required: 'programme.priority.priorityPolicies.should.not.be.empty'
    }
  };

  public static POLICY_OBJECTIVE: AppControl = {
    name: 'programmeObjectivePolicy'
  };

  public static POLICY_OFFICIAL_CODE: AppControl = {
    name: 'officialCode',
  };

  public static DIMENSION_CODES: AppControl = {
    name: 'dimensionCodes',
    errorMessages: {
      maxSize: 'programme.priority.dimensions.exceeded'
    }
  };

  public static POLICY_CODE: AppControl = {
    name: 'code',
    errorMessages: {
      required: 'programme.priority.specific.objective.code.should.not.be.empty'
    },
    validators: [Validators.maxLength(50)]
  };

  public static POLICY_SELECTED: AppControl = {
    name: 'selected'
  };

  public static selectedSpecificObjectiveCodeRequired: (control: FormGroup) => ValidatorFn = (objective: FormGroup) => (valueControl: FormControl): ValidationErrors | null => {
    const selected = objective.get(ProgrammePriorityDetailPageConstants.POLICY_SELECTED.name)?.value;
    const code = objective.get(ProgrammePriorityDetailPageConstants.POLICY_CODE.name)?.value;
    if (selected && !code) {
      return {required: true} as any;
    }
    return null;
  };

  public static dimensionCodesSize: (control: FormGroup) => ValidatorFn = (objective: FormGroup) => (valueControl: FormControl): ValidationErrors | null => {
    const selected = objective.get(ProgrammePriorityDetailPageConstants.POLICY_SELECTED.name)?.value;
    if (selected && !valueControl?.value?.length) {
      return {required: true} as any;
    }
    if (selected && valueControl?.value?.length > 20) {
      return {maxSize: true} as any;
    }
    return null;
  };

  public static mustHaveSpecificObjectiveSelected(objectives: FormArray): ValidatorFn | null {
    const oneSelected = objectives.controls.some(
      objective => !!objective.get(ProgrammePriorityDetailPageConstants.POLICY_SELECTED.name)?.value
    );
    if (!objectives?.controls?.length || oneSelected) {
      return null;
    }
    return {required: true} as any;
  }

}
