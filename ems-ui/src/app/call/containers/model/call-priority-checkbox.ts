import {OutputProgrammePriority, OutputProgrammePriorityPolicySimple} from '@cat/api';

export class CallPriorityCheckbox {
  name: string;
  checked: boolean;
  children: CallPriorityCheckbox[] = [];
  policy: OutputProgrammePriorityPolicySimple.ProgrammeObjectivePolicyEnum;

  static fromPriority(from: OutputProgrammePriority): CallPriorityCheckbox {
    const checkbox = new CallPriorityCheckbox();
    checkbox.name = from.code + ' ' + from.title;
    checkbox.children = from.programmePriorityPolicies.map(policy => CallPriorityCheckbox.fromPriorityPolicy(policy));
    return checkbox;
  }

  static fromPriorityPolicy(from: OutputProgrammePriorityPolicySimple): CallPriorityCheckbox {
    const checkbox = new CallPriorityCheckbox();
    checkbox.name = 'programme.policy.' + from.programmeObjectivePolicy;
    checkbox.children = [];
    checkbox.policy = from.programmeObjectivePolicy;
    return checkbox;
  }

  static fromSavedPolicies(priorityCheckbox: CallPriorityCheckbox,
                           checked: OutputProgrammePriorityPolicySimple.ProgrammeObjectivePolicyEnum[]): CallPriorityCheckbox {
    const checkbox = new CallPriorityCheckbox();
    checkbox.name = priorityCheckbox.name;
    checkbox.children = priorityCheckbox.children.map(child => {
        const copy = new CallPriorityCheckbox();
        copy.name = child.name;
        copy.children = [];
        copy.policy = child.policy;
        copy.checked = checked.includes(child.policy)
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

  getCheckedChildPolicies(): OutputProgrammePriorityPolicySimple.ProgrammeObjectivePolicyEnum[] {
    return this.children
      .filter(child => child.checked)
      .map(child => child.policy)
  }
}
