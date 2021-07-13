import {Directive, Input, TemplateRef, ViewContainerRef} from '@angular/core';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {map} from 'rxjs/operators';
import {combineLatest, Observable} from 'rxjs';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ApplicationFormFieldConfigurationDTO} from '@cat/api';
import {ProjectCallSettings} from '@project/model/projectCallSettings';
import AvailableInStepEnum = ApplicationFormFieldConfigurationDTO.AvailableInStepEnum;
import {ApplicationFormModel} from '@project/application-form-model';

@UntilDestroy()
@Directive({
  selector: '[appFormFieldVisibilityStatus]'
})
export class FormFieldVisibilityStatusDirective {

  private hasView = false;

  @Input() set appFormFieldVisibilityStatus(fieldIds: string | ApplicationFormModel ) {
    if (fieldIds === undefined || fieldIds === null || (typeof fieldIds === 'string' && fieldIds.length === 0) || (typeof fieldIds === 'object' && Object.keys(fieldIds).length === 0)) {
      return;
    }
    this.shouldBeVisible(this.getFieldIdsToCheck(fieldIds)).pipe(untilDestroyed(this)).subscribe(shouldBeVisible => {
      this.handleElementVisibility(shouldBeVisible);
    });
  }

  constructor(private templateRef: TemplateRef<any>, private viewContainer: ViewContainerRef, private projectStore: ProjectStore) {
  }


  private getFieldIdsToCheck(fieldIds: string | ApplicationFormModel): string[] {
    if (typeof fieldIds === 'string') {
      return [fieldIds];
    } else {
      return [...Object.keys(fieldIds).flatMap(key => this.getFieldIdsToCheck(fieldIds[key]))];
    }
  }

  private handleElementVisibility(shouldBeVisible: boolean): void {
    if (shouldBeVisible) {
      if (!this.hasView) {
        this.viewContainer.createEmbeddedView(this.templateRef);
        this.hasView = true;
      }
    } else if (this.hasView) {
      this.viewContainer.clear();
      this.hasView = false;
    }
  }

  private shouldBeVisible(fieldIds: string[]): Observable<boolean> {
    return combineLatest([this.projectStore.project$, this.projectStore.projectCall$, this.projectStore.callHasTwoSteps$]).pipe(
      map(([project, callSetting, hasCallTwoSteps]) =>
          fieldIds.find(fieldId => this.isFieldVisible(fieldId, callSetting, hasCallTwoSteps, project.step2Active)) !== undefined
      )
    );
  }

  private isFieldVisible(fieldId: string, callSetting: ProjectCallSettings, hasCallTwoSteps: boolean, isProjectInStepTwo: boolean): boolean {
    const fieldConfiguration = callSetting.applicationFormFieldConfigurations.find(it => it.id === fieldId);

    if (fieldConfiguration === undefined || !fieldConfiguration.isVisible) {
      return false;
    }

    return !hasCallTwoSteps ||
      (hasCallTwoSteps && this.shouldBeVisibleForTwoStepCall(fieldConfiguration, isProjectInStepTwo));
  }

  private shouldBeVisibleForTwoStepCall(fieldConfiguration: ApplicationFormFieldConfigurationDTO, isApplicationInStep2: boolean): boolean {
    return fieldConfiguration.availableInStep === AvailableInStepEnum.STEPONEANDTWO ||
      (fieldConfiguration.availableInStep === AvailableInStepEnum.STEPTWOONLY && isApplicationInStep2);
  }

}
