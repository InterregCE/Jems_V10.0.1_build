import {PluginMessageType} from './PluginMessageType';
import {PreConditionCheckMessageDTO} from '@cat/api';

export class PreConditionCheckMessage {
  issueCount = 0;
  messageKey: string;
  messageType: PluginMessageType;
  subSectionMessages: PreConditionCheckMessage[];

  constructor(issueCount: number, messageKey: string, messageType: PluginMessageType, subSectionMessages: Array<PreConditionCheckMessage>) {
    this.messageKey = messageKey;
    this.messageType = messageType;
    this.subSectionMessages = subSectionMessages;
    this.issueCount = issueCount;
  }

  static newInstance(preConditionCheckMessageDTO: PreConditionCheckMessageDTO): PreConditionCheckMessage {
    return new PreConditionCheckMessage(
      preConditionCheckMessageDTO.issueCount,
      preConditionCheckMessageDTO.messageKey,
      preConditionCheckMessageDTO.messageType as PluginMessageType,
      preConditionCheckMessageDTO.subSectionMessages.map(it => PreConditionCheckMessage.newInstance(it))
    );
  }
}
