import {ChangeDetectionStrategy, Component, EventEmitter, Output} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {animate, style, transition, trigger} from '@angular/animations';
import {combineLatest} from 'rxjs';
import {map} from 'rxjs/operators';

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

  showSaveDiscard$ = combineLatest([this.formService.dirty$, this.formService.pending$])
    .pipe(
      map(([dirty, pending]) => dirty || pending)
    );

  constructor(public formService: FormService) {
  }

  submit(): void {
    this.formService.pending$.next(true);
    this.formService.setDirty(false);
    this.save.emit();
  }
}
