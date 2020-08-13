import {OutputProgrammePriority, OutputProgrammePriorityPolicy} from '@cat/api';

export class CallPriorityCheckbox {
  name: string;
  checked: boolean;
  children: CallPriorityCheckbox[] = [];
  policy: OutputProgrammePriorityPolicy.ProgrammeObjectivePolicyEnum;

  static fromPriority(from: OutputProgrammePriority): CallPriorityCheckbox {
    const checkbox = new CallPriorityCheckbox();
    checkbox.name = from.code + ' ' + from.title;
    checkbox.children = from.programmePriorityPolicies.map(policy => CallPriorityCheckbox.fromPriorityPolicy(policy));
    return checkbox;
  }

  static fromPriorityPolicy(from: OutputProgrammePriorityPolicy): CallPriorityCheckbox {
    const checkbox = new CallPriorityCheckbox();
    checkbox.name = 'programme.policy.' + from.programmeObjectivePolicy;
    checkbox.children = [];
    checkbox.policy = from.programmeObjectivePolicy;
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

  getCheckedChildPolicies(): OutputProgrammePriorityPolicy.ProgrammeObjectivePolicyEnum[] {
    return this.children
      .filter(child => child.checked)
      .map(child => child.policy)
  }

  updateCheckedPolicies(checked: OutputProgrammePriorityPolicy.ProgrammeObjectivePolicyEnum[]): void {
    this.children
      .filter(policy => checked.includes(policy.policy))
      .forEach(checkbox => checkbox.checked = true)
    this.updateChecked();
  }
}
