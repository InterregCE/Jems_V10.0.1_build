import {InputTranslation} from '@cat/api';

export class MultiLanguageInput {
  inputs: InputTranslation[];
  private valid = new Map<InputTranslation.LanguageEnum, boolean>();

  constructor(inputs: InputTranslation[]) {
    this.inputs = inputs;
    this.inputs.forEach(input => this.valid.set(input.language, true));
  }

  setValue(value: string, language: InputTranslation.LanguageEnum, valid: boolean): void {
    const input = this.inputs?.find(trans => trans.language === language);
    if (input) {
      input.translation = value;
      this.valid.set(language, valid);
    }
  }

  isValidForLanguage(language: InputTranslation.LanguageEnum): boolean {
    return this.valid.has(language) ? this.valid.get(language) as boolean : true;
  }

  isValid(): boolean {
    return [...this.valid.values()].every(isValid => isValid);
  }
}
