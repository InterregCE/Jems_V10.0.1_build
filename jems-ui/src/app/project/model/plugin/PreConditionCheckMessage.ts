import {PluginMessageType} from './PluginMessageType';
import {PreConditionCheckMessageDTO} from '@cat/api';
import {I18nMessage} from '../../../common/models/I18nMessage';

export class PreConditionCheckMessage {
  issueCount = 0;
  message: I18nMessage;
  messageType: PluginMessageType;
  subSectionMessages: PreConditionCheckMessage[];

  constructor(issueCount: number, message: I18nMessage, messageType: PluginMessageType, subSectionMessages: PreConditionCheckMessage[]) {
    this.message = message;
    this.messageType = messageType;
    this.subSectionMessages = subSectionMessages;
    this.issueCount = issueCount;
  }

  static newInstance(preConditionCheckMessageDTO: PreConditionCheckMessageDTO): PreConditionCheckMessage {
    return new PreConditionCheckMessage(
      preConditionCheckMessageDTO.issueCount,
      preConditionCheckMessageDTO.message,
      preConditionCheckMessageDTO.messageType as PluginMessageType,
      preConditionCheckMessageDTO.subSectionMessages.map(it => PreConditionCheckMessage.newInstance(it))
    );
  }
}
