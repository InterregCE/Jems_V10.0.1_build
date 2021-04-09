import {
  InputTranslation,
  OutputProgrammePriorityPolicySimpleDTO,
  ProgrammePriorityDTO,
  ProgrammeSpecificObjectiveDTO
} from '@cat/api';

export class CallPriorityCheckbox {
  name: string;
  translatableTitle: InputTranslation[];
  checked: boolean;
  children: CallPriorityCheckbox[] = [];
  policy: OutputProgrammePriorityPolicySimpleDTO.ProgrammeObjectivePolicyEnum;

  static fromPriority(from: ProgrammePriorityDTO): CallPriorityCheckbox {
    const checkbox = new CallPriorityCheckbox();
    checkbox.name = `${from.code} `;
    checkbox.translatableTitle = from.title;
    checkbox.children = from.specificObjectives.map(policy => CallPriorityCheckbox.fromPriorityPolicy(policy));
    return checkbox;
  }

  static fromPriorityPolicy(from: OutputProgrammePriorityPolicySimpleDTO): CallPriorityCheckbox {
    const checkbox = new CallPriorityCheckbox();
    checkbox.name = 'programme.policy.' + from.programmeObjectivePolicy;
    checkbox.children = [];
    checkbox.policy = from.programmeObjectivePolicy;
    return checkbox;
  }

  static fromSavedPolicies(priorityCheckbox: CallPriorityCheckbox,
                           checked: ProgrammeSpecificObjectiveDTO.ProgrammeObjectivePolicyEnum[]): CallPriorityCheckbox {
    const checkbox = new CallPriorityCheckbox();
    checkbox.name = priorityCheckbox.name;
    checkbox.translatableTitle = priorityCheckbox.translatableTitle;
    checkbox.children = priorityCheckbox.children.map(child => {
        const copy = new CallPriorityCheckbox();
        copy.name = child.name;
        copy.children = [];
        copy.policy = child.policy;
        copy.checked = checked.includes(child.policy);
        return copy;
      }
    );
    checkbox.policy = priorityCheckbox.policy;
    checkbox.updateChecked();
    return checkbox;
  }

  updateChecked(): void {
    this.checked = this.children.every(child => child.checked);
  }

  someChecked(): boolean {
    return !this.checked && this.children.some(child => child.checked);
  }

  checkOrUncheckAll(): void {
    this.children.forEach(child => child.checked = !this.checked);
    this.updateChecked();
  }

  getCheckedChildPolicies(): OutputProgrammePriorityPolicySimpleDTO.ProgrammeObjectivePolicyEnum[] {
    return this.children
      .filter(child => child.checked)
      .map(child => child.policy);
  }
}
