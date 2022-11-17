import {OutputNuts} from '@cat/api';

export class JemsRegionCheckbox {
  code: string;
  title: string;
  checked: boolean;
  someChecked: boolean;
  initiallyChecked: boolean;
  parent: JemsRegionCheckbox | null;
  children: JemsRegionCheckbox[] = [];

  static fromNuts(nuts: OutputNuts[]): JemsRegionCheckbox[] {
    if (!nuts) {
      return [];
    }
    return nuts.map(area => this.fromRegion(null, area));
  }

  static fromRegion(parent: JemsRegionCheckbox | null, region: OutputNuts): JemsRegionCheckbox {
    const checkbox = new JemsRegionCheckbox();
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

  static fromSelected(all: JemsRegionCheckbox[], selected: JemsRegionCheckbox[]): JemsRegionCheckbox[] {
    const selectedCodes: string[] = [];
    selected.forEach(checkbox => JemsRegionCheckbox.getAllCodes(checkbox, selectedCodes));
    all.forEach(checkbox => JemsRegionCheckbox.checkAllWithCodes(checkbox, selectedCodes));
    return all;
  }

  static getAllCodes(checkbox: JemsRegionCheckbox, codes: string[]): void {
    if (checkbox.code) {
      codes.push(checkbox.code);
    }
    checkbox.children.forEach(child => this.getAllCodes(child, codes));
  }

  static checkAllWithCodes(checkbox: JemsRegionCheckbox, codes: string[]): void {
    if (checkbox.code && codes.includes(checkbox.code)) {
      checkbox.checked = true;
      checkbox.initiallyChecked = true;
      checkbox.updateChecked();
    }
    checkbox.children.forEach(child => JemsRegionCheckbox.checkAllWithCodes(child, codes));
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
      this.checked = value;
      return;
    }
    this.children.forEach(child => child.checkOrUncheckAll(value));
    this.updateChecked();
  }
}
