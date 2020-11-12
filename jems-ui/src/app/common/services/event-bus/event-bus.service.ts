import {Injectable} from '@angular/core';
import {Observable, ReplaySubject} from 'rxjs';
import {filter, map} from 'rxjs/operators';
import {Event} from './event';
import {EventType} from './event-type';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {I18nLabel} from '../../i18n/i18n-label';
import {Log} from '../../utils/log';

@Injectable({providedIn: 'root'})
export class EventBusService {

  private event$ = new ReplaySubject<Event>(2);

  getEvent(source: string): Observable<Event> {
    return this.event$
      .pipe(
        filter(event => event.source === source)
      );
  }

  getEventByType(source: string, type: string): Observable<any | null> {
    return this.event$
      .pipe(
        filter(event => event.source === source && event.type === type),
        map(event => event.context)
      );
  }

  newEvent(source: string, type: EventType, context: any | null): void {
    this.event$.next(new Event({source, type, context}));
    Log.debug('Setting section event', this, source, type, context);
  }

  newSuccessMessage(source: string, message: I18nLabel | string | null): void {
    this.newEvent(source, EventType.SUCCESS_MESSAGE, message);
  }

  newErrorMessage(source: string, error: I18nValidationError | null): void {
    this.newEvent(source, EventType.ERROR_MESSAGE, error);
  }

  setDirty(source: string, dirty: boolean): void {
    this.newEvent(source, EventType.DIRTY_FORM, dirty);
  }
}
