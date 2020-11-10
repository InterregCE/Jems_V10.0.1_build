import {OutputNuts} from '@cat/api';

export class ProgrammeRegionCheckbox {
  code: string;
  title: string
  checked: boolean;
  someChecked: boolean;
  parent: ProgrammeRegionCheckbox | null;
  children: ProgrammeRegionCheckbox[] = [];

  static fromNuts(nuts: OutputNuts[]): ProgrammeRegionCheckbox[] {
    if (!nuts) {
      return [];
    }
    return nuts.map(area => this.fromRegion(null, area));
  }

  static fromRegion(parent: ProgrammeRegionCheckbox | null, region: OutputNuts): ProgrammeRegionCheckbox {
    const checkbox = new ProgrammeRegionCheckbox();
    if (!region) {
      return checkbox;
    }
    checkbox.parent = parent;
    checkbox.code = region.code;
    checkbox.title = region.title;
    if (region.areas?.length) {
      checkbox.children = region.areas.map(area => this.fromRegion(checkbox, area));
    }
    return checkbox;
  }

  static fromSelected(all: ProgrammeRegionCheckbox[], selected: ProgrammeRegionCheckbox[]): ProgrammeRegionCheckbox[] {
    const selectedCodes: string[] = [];
    selected.forEach(checkbox => ProgrammeRegionCheckbox.getAllCodes(checkbox, selectedCodes));
    all.forEach(checkbox => ProgrammeRegionCheckbox.checkAllWithCodes(checkbox, selectedCodes));
    return all;
  }

  static getAllCodes(checkbox: ProgrammeRegionCheckbox, codes: string[]): void {
    if (checkbox.code) {
      codes.push(checkbox.code);
    }
    checkbox.children.forEach(child => this.getAllCodes(child, codes));
  }

  static checkAllWithCodes(checkbox: ProgrammeRegionCheckbox, codes: string[]): void {
    if (checkbox.code && codes.includes(checkbox.code)) {
      checkbox.checked = true;
      checkbox.updateChecked();
    }
    checkbox.children.forEach(child => ProgrammeRegionCheckbox.checkAllWithCodes(child, codes));
  }

  updateChecked(): void {
    if (this.parent) {
      this.parent.updateChecked();
    }
    this.checked = this.allChildrenChecked();
    this.someChecked = this.someChildrenChecked();
  }

  allChildrenChecked(): boolean {
    if (this.children.length === 0) {
      return this.checked;
    }
    return this.children.every(child => child.allChildrenChecked());
  }

  someChildrenChecked(): boolean {
    if (this.children.length === 0) {
      return this.checked;
    }
    return this.children.some(child => child.someChildrenChecked());
  }

  checkOrUncheckAll(value: boolean): void {
    this.checked = value;
    if (this.children.length === 0) {
      this.checked = value
      return;
    }
    this.children.forEach(child => child.checkOrUncheckAll(value));
    this.updateChecked();
  }
}
