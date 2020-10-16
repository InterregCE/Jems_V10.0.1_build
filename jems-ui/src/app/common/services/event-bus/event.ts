import {EventType} from './event-type';

export class Event {
  id: string;
  source?: string;
  type?: EventType;
  context?: any;

  constructor(data: Partial<Event>) {
    this.id = Math.random().toString(36);
    this.source = data.source;
    this.type = data.type;
    this.context = data.context;
  }
}
