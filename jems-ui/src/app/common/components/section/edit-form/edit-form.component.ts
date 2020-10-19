import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output, TemplateRef, ViewChild
} from '@angular/core';
import {merge, Observable, of, ReplaySubject} from 'rxjs';
import {FormGroup} from '@angular/forms';
import {
  delay,
  filter,
  map,
  takeUntil,
  tap
} from 'rxjs/operators';
import {BaseComponent} from '@common/components/base-component';
import {Alert} from '@common/components/forms/alert';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {Log} from '../../../utils/log';
import {EventBusService} from '../../../services/event-bus/event-bus.service';
import {EventType} from '../../../services/event-bus/event-type';

@Component({
  selector: 'app-edit-form',
  templateUrl: './edit-form.component.html',
  styleUrls: ['./edit-form.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditFormComponent extends BaseComponent implements OnInit {
  Alert = Alert;

  @Input()
  componentName: string;
  @Input()
  createNew: boolean;
  @Input()
  form: FormGroup;

  @Output()
  save = new EventEmitter<void>();
  @Output()
  discard = new EventEmitter<void>();

  @ViewChild('saveDiscard', {static: true})
  saveDiscard: TemplateRef<any>;
  @ViewChild('saveSuccess', {static: true})
  saveSuccess: TemplateRef<any>;
  @ViewChild('saveError', {static: true})
  saveError: TemplateRef<any>;

  footerTemplateRef: TemplateRef<any> | null;
  formValid$ = new ReplaySubject<boolean>(1);
  editActive$: Observable<boolean>;

  constructor(private eventBusService: EventBusService,
              private changeDetectorRef: ChangeDetectorRef) {
    super();
  }

  ngOnInit(): void {
    if (!this.form) {
      return;
    }
    this.eventBusService.getEventByType(this.componentName, EventType.SUCCESS_MESSAGE)
      .pipe(
        takeUntil(this.destroyed$),
        tap(() => this.footerTemplateRef = this.saveSuccess),
      ).subscribe();

    this.eventBusService.getEventByType(this.componentName, EventType.ERROR_MESSAGE)
      .pipe(
        takeUntil(this.destroyed$),
        filter(error => !!error),
        tap(error => this.setFieldErrors(error as I18nValidationError)),
        tap(error => {
          if (!error?.i18nKey) (error as any).i18nKey = 'incomplete.form';
        }),
        tap(() => this.footerTemplateRef = this.saveError),
      ).subscribe();

    this.form.valueChanges.pipe(map(() => true))
      .pipe(
        takeUntil(this.destroyed$),
        filter(() => this.form.dirty),
        tap(footer => this.eventBusService.setDirty(this.componentName, footer)),
      ).subscribe();

    const createNew$ = of(this.createNew)
      .pipe(
        filter(isCreate => isCreate),
        delay(500),
        tap(() => this.footerTemplateRef = this.saveDiscard),
        tap(isCreate => this.eventBusService.setDirty(this.componentName, isCreate)),
      )

    const dirtyChanged$ = this.eventBusService.getEventByType(this.componentName, EventType.DIRTY_FORM)
      .pipe(
        tap(() => this.formValid$.next(this.form.valid)),
        tap(() => this.footerTemplateRef = this.saveDiscard),
      );

    this.editActive$ = merge(createNew$, dirtyChanged$)
      .pipe(
        map(active => !!active)
      );
  }

  onSave(): void {
    // mark form as pristine in order to ignore 'dirty' statuses from formChanged$
    this.form.markAsPristine();
    this.save.emit();
    this.eventBusService.setDirty(this.componentName, false);
  }

  onDiscard(): void {
    // mark form as pristine in order to ignore 'dirty' statuses from formChanged$
    this.form.markAsPristine();
    this.discard.emit();
    this.eventBusService.setDirty(this.componentName, false);
  }

  private setFieldErrors(error: I18nValidationError): void {
    Log.debug('Set form backend errors.', this, error);
    Object.keys(this.form.controls).forEach(key => {
      if (!error?.i18nFieldErrors || !error.i18nFieldErrors[key]) {
        return;
      }
      this.form.controls[key].setErrors({i18nError: error.i18nFieldErrors[key].i18nKey});
      this.form.controls[key].markAsTouched();
      this.changeDetectorRef.markForCheck();
    });
  }
}
