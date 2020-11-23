import {InputTranslation} from '@cat/api';

export class MultiLanguageInput {
  inputs: InputTranslation[];
  validators?: Function[];

  constructor(inputs: InputTranslation[], validators?: Function[]) {
    this.inputs = inputs;
    this.validators = validators;
  }

  valid(language: InputTranslation.LanguageEnum) {
    if (!this.inputs?.length) {
      return true;
    }
    return this.inputs
      .filter(input => input.language === language)
      .every(input => this.isValid(input.translation));
  }

  private isValid(value: string) {
    if (!this.validators?.length) {
      return true;
    }
    return this.validators.every(validator => !!validator(value));
  }
}
