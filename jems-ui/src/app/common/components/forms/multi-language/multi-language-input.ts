import {InputTranslation} from '@cat/api';

export class MultiLanguageInput {
  inputs: InputTranslation[];
  validators?: ((arg: string) => boolean)[];

  constructor(inputs: InputTranslation[], validators?: ((arg: string) => boolean)[]) {
    this.inputs = inputs;
    this.validators = validators;
  }

  valid(language: InputTranslation.LanguageEnum): boolean {
    if (!this.inputs?.length) {
      return true;
    }
    return this.inputs
      .filter(input => input.language === language)
      .every(input => this.isValid(input.translation));
  }

  private isValid(value: string): boolean {
    if (!this.validators?.length) {
      return true;
    }
    return this.validators.every(validator => !!validator(value));
  }
}
