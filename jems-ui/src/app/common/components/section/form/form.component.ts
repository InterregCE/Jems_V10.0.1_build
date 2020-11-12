import {ChangeDetectionStrategy, Component, EventEmitter, Output} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {animate, style, transition, trigger} from '@angular/animations';

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.scss'],
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
export class FormComponent {

  @Output()
  save = new EventEmitter<void>();
  @Output()
  discard = new EventEmitter<void>();

  constructor(public formService: FormService) {
  }
}
