import {BehaviorSubject} from 'rxjs';
import {FormService} from '@common/components/section/form/form.service';
import {map, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Injectable} from '@angular/core';

@UntilDestroy()
@Injectable()
export class ProjectPartnerBudgetTabService {

  private isBudgetOptionFormInEditMode$ = new BehaviorSubject(false);
  private isBudgetFormInEditModeSubject$ = new BehaviorSubject(false);
  isBudgetFormDisabled$ = this.isBudgetOptionFormInEditMode$.asObservable().pipe(map(it => !it));
  isBudgetOptionsFormDisabled$ = this.isBudgetFormInEditModeSubject$.asObservable().pipe(map(it => !it));

  trackBudgetOptionsFormState(formService: FormService): void {
    formService.dirty$.pipe(
      tap(dirty => this.isBudgetOptionFormInEditMode$.next(dirty)),
      untilDestroyed(this)
    ).subscribe();
  }

  trackBudgetFormState(formService: FormService): void {
    formService.dirty$.pipe(
      tap(dirty => this.isBudgetFormInEditModeSubject$.next(dirty)),
      untilDestroyed(this)
    ).subscribe();
  }
}
