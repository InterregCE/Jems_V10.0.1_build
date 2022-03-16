import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Alert} from '@common/components/forms/alert';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {PreSubmissionCheckSettingsPageStore} from './pre-submission-check-settings-page-store.service';
import {PluginInfoDTO} from '@cat/api';
import {catchError, map, tap} from 'rxjs/operators';
import {CallPageSidenavService} from '../services/call-page-sidenav.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {PluginKeys} from './plugin-keys';

@UntilDestroy()
@Component({
  selector: 'jems-pre-submission-check-settings-page',
  templateUrl: './pre-submission-check-settings-page.component.html',
  styleUrls: ['./pre-submission-check-settings-page.component.scss'],
  providers: [FormService, PreSubmissionCheckSettingsPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PreSubmissionCheckSettingsPageComponent {

  Alert = Alert;

  data$: Observable<{
    pluginKeys: PluginKeys;
    preSubmissionCheckPlugins: PluginInfoDTO[];
    callIsEditable: boolean;
  }>;

  form = this.formBuilder.group({
    pluginKey: ['', Validators.required],
    callHasTwoSteps: false
  });

  constructor(private formService: FormService,
              private formBuilder: FormBuilder,
              private pageStore: PreSubmissionCheckSettingsPageStore,
              private callSidenavService: CallPageSidenavService) {
    this.formService.init(this.form);
    this.data$ = combineLatest([
      pageStore.callIsEditable$,
      pageStore.preSubmissionCheckPlugins,
      pageStore.pluginKeys$,
    ]).pipe(
      map(([callIsEditable, preSubmissionCheckPlugins, pluginKeys]) => ({
          preSubmissionCheckPlugins,
          callIsEditable,
          pluginKeys
      })),
      tap((data ) => this.resetForm(data.pluginKeys))
    );
  }

  resetForm(pluginKeys: PluginKeys): void {
    if (pluginKeys.callHasTwoSteps) {
      this.form.addControl('firstStepPluginKey', new FormControl(['', Validators.required]));
      this.callHasTwoSteps.setValue(true);
      this.firstStepPluginKey.setValue(pluginKeys.firstStepPluginKey);
    }
    this.pluginKey.setValue(pluginKeys.pluginKey);
  }

  save(): void {
    this.pageStore.save(this.pluginKeys.value).pipe(
      tap(() => this.formService.setSuccess('call.detail.application.form.config.saved.success')),
      catchError(error => this.formService.setError(error)),
      untilDestroyed(this)
    ).subscribe();
  }

  get pluginKey(): FormControl {
    return this.form.get('pluginKey') as FormControl;
  }

  get firstStepPluginKey(): FormControl {
    return this.form.get('firstStepPluginKey') as FormControl;
  }

  get callHasTwoSteps(): FormControl {
    return this.form.get('callHasTwoSteps') as FormControl;
  }

  get pluginKeys(): FormGroup {
    return this.form as FormGroup;
  }
}
