export class ProgrammeRegionCheckbox {
  id: string;
  title: string
  checked: boolean;
  someChecked: boolean;
  parent: ProgrammeRegionCheckbox | null;
  children: ProgrammeRegionCheckbox[] = [];

  static fromNuts(nuts: any): ProgrammeRegionCheckbox[] {
    if (!nuts) {
      return [];
    }
    return Object.keys(nuts).map(key => this.fromRegion(null, key, nuts[key]));
  }

  static fromRegion(parent: ProgrammeRegionCheckbox | null, title: string, region: any): ProgrammeRegionCheckbox {
    const checkbox = new ProgrammeRegionCheckbox();
    if (!region) {
      return checkbox;
    }
    checkbox.parent = parent;
    if (region.id && region.title) {
      checkbox.id = region.id;
      checkbox.title = region.title.replace('|', ' ');
      return checkbox;
    }
    checkbox.title = title.replace('|', ' ');
    checkbox.children = Object.keys(region)
      .map(key => this.fromRegion(checkbox, key, region[key]));
    return checkbox;
  }

  static fromSelected(all: ProgrammeRegionCheckbox[], selected: ProgrammeRegionCheckbox[]): ProgrammeRegionCheckbox[] {
    const selectedIds: string[] = [];
    selected.forEach(checkbox => ProgrammeRegionCheckbox.getAllIds(checkbox, selectedIds));
    all.forEach(checkbox => ProgrammeRegionCheckbox.checkAllWithIds(checkbox, selectedIds));
    return all;
  }

  static getAllIds(checkbox: ProgrammeRegionCheckbox, ids: string[]): void {
    if (checkbox.id) {
      ids.push(checkbox.id);
    }
    checkbox.children.forEach(child => this.getAllIds(child, ids));
  }

  static checkAllWithIds(checkbox: ProgrammeRegionCheckbox, ids: string[]): void {
    if (checkbox.id && ids.includes(checkbox.id)) {
      checkbox.checked = true;
      checkbox.updateChecked();
    }
    checkbox.children.forEach(child => ProgrammeRegionCheckbox.checkAllWithIds(child, ids));
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
