import {PreConditionCheckMessage} from './PreConditionCheckMessage';
import {PreConditionCheckResultDTO} from '@cat/api';

export class PreConditionCheckResult {
  messages: PreConditionCheckMessage[];
  submissionAllowed: boolean;

  constructor(messages: PreConditionCheckMessage[], submissionAllowed: boolean) {
    this.messages = messages;
    this.submissionAllowed = submissionAllowed;
  }

  static newInstance(preConditionCheckResultDTO: PreConditionCheckResultDTO): PreConditionCheckResult {
    return new PreConditionCheckResult(
      preConditionCheckResultDTO.messages.map(it => PreConditionCheckMessage.newInstance(it)),
      preConditionCheckResultDTO.submissionAllowed
    );

  }

}
