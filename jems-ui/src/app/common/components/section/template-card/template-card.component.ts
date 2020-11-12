import {ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, TemplateRef} from '@angular/core';
import {Observable} from 'rxjs';
import {animate, style, transition, trigger} from '@angular/animations';
import {map, tap} from 'rxjs/operators';
import {Event} from '../../../services/event-bus/event';
import {EventBusService} from '../../../services/event-bus/event-bus.service';
import {EventType} from '../../../services/event-bus/event-type';

@Component({
  selector: 'app-template-card',
  templateUrl: './template-card.component.html',
  styleUrls: ['./template-card.component.scss'],
  animations: [
    trigger('slideInOut', [
      transition(':enter', [
        style({transform: 'translateY(40%)'}),
        animate('300ms ease-in', style({transform: 'translateY(0%)'}))
      ]),
      transition(':leave', [
        animate('300ms ease-in', style({transform: 'translateY(-40%)'}))
      ]),
    ]),
  ],
  changeDetection: ChangeDetectionStrategy.OnPush

})
export class TemplateCardComponent implements OnInit, OnDestroy {

  /**
   * A unique identifier for the component. Make sure the provided id is unique
   * across the whole context. Avoid using static strings like `Component.name`
   * which is changed during minification.
   */
  @Input()
  componentId: string;
  @Input()
  cardTemplateRef: TemplateRef<any>;

  event$: Observable<any | null>;
  currentEventId: string;

  constructor(public eventBusService: EventBusService) {
  }

  ngOnInit(): void {
    if (!this.componentId) {
      return;
    }
    this.event$ = this.eventBusService.getEvent(this.componentId)
      .pipe(
        tap(event => this.setExpiration(event)),
        map(event => event.context)
      );
  }

  ngOnDestroy(): void {
    if (this.componentId) {
      this.eventBusService.setDirty(this.componentId, false);
    }
  }

  private setExpiration(event: Event): void {
    this.currentEventId = event?.id;
    if (!event?.context) {
      return;
    }
    if (event.type !== EventType.SUCCESS_MESSAGE) {
      return;
    }
    setTimeout(() => {
      if (this.currentEventId === event.id) {
        this.eventBusService.newSuccessMessage(event.source as any, null);
      }
    },         3000);
  }
}
