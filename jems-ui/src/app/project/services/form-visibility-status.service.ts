import {Injectable} from '@angular/core';
import {ApplicationFormModel} from '@project/application-form-model';
import {ApplicationFormFieldConfigurationDTO} from '@cat/api';
import {ProjectStore} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import AvailableInStepEnum = ApplicationFormFieldConfigurationDTO.AvailableInStepEnum;

@Injectable()
export class FormVisibilityStatusService {

  constructor(private projectStore: ProjectStore) {
  }

  isVisible(fieldIds: string | ApplicationFormModel): boolean {
    return !this.projectStore.currentProject ?
      false :
      this.shouldBeVisible(fieldIds, this.projectStore.currentProject.callSettings.applicationFormFieldConfigurations, this.projectStore.currentProject.callSettings.endDateStep1 !== undefined, this.projectStore.currentProject.step2Active);
  }

  isVisible$(fieldIds: string | ApplicationFormModel): Observable<boolean> {
    return combineLatest([this.projectStore.project$, this.projectStore.projectCall$, this.projectStore.callHasTwoSteps$]).pipe(
      map(([project, callSetting, hasCallTwoSteps]) =>
        this.shouldBeVisible(fieldIds, callSetting.applicationFormFieldConfigurations, hasCallTwoSteps, project.step2Active)
      )
    );
  }

  private shouldBeVisible(fieldIds: string | ApplicationFormModel, applicationFormFieldConfigurations: ApplicationFormFieldConfigurationDTO[], hasCallTwoSteps: boolean, isProjectInStepTwo: boolean): boolean {
    return this.getFieldIdsToCheck(fieldIds).find(fieldId => this.isFieldVisible(fieldId, applicationFormFieldConfigurations, hasCallTwoSteps, isProjectInStepTwo)) !== undefined;
  }

  private getFieldIdsToCheck(fieldIds: string | ApplicationFormModel): string[] {
    if (typeof fieldIds === 'string') {
      return [fieldIds];
    } else {
      return [...Object.keys(fieldIds).flatMap(key => this.getFieldIdsToCheck(fieldIds[key]))];
    }
  }

  private isFieldVisible(fieldId: string, applicationFormFieldConfigurations: ApplicationFormFieldConfigurationDTO[], hasCallTwoSteps: boolean, isProjectInStepTwo: boolean): boolean {
    const fieldConfiguration = applicationFormFieldConfigurations.find(it => it.id === fieldId);

    if (fieldConfiguration === undefined || !fieldConfiguration.visible) {
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
