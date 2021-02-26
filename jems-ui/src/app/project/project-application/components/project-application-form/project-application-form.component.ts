import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  InputProjectData,
  InputTranslation,
  OutputCallProgrammePriority,
  OutputProgrammePrioritySimple,
  OutputProject
} from '@cat/api';
import {Permission} from '../../../../security/permissions/permission';
import {Tools} from '../../../../common/utils/tools';
import {catchError, distinctUntilChanged, take, takeUntil, tap} from 'rxjs/operators';
import {BaseComponent} from '@common/components/base-component';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectStore} from '../../containers/project-application-detail/services/project-store.service';
import {MultiLanguageInputService} from '../../../../common/services/multi-language-input.service';

@Component({
  selector: 'app-project-application-form',
  templateUrl: './project-application-form.component.html',
  styleUrls: ['./project-application-form.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormComponent extends BaseComponent implements OnInit {
  Permission = Permission;
  tools = Tools;
  LANGUAGE = InputTranslation.LanguageEnum;

  @Input()
  project: OutputProject;
  @Input()
  editable: boolean;
  @Input()
  priorities: OutputProgrammePrioritySimple[];
  @Input()
  objectivesWithPolicies: { [key: string]: InputProjectData.SpecificObjectiveEnum[] };

  currentPriority?: string;
  previousObjective: InputProjectData.SpecificObjectiveEnum;
  currentObjectives: any = [];
  selectedSpecificObjective: InputProjectData.SpecificObjectiveEnum;

  applicationForm: FormGroup = this.formBuilder.group({
    projectId: [''],
    acronym: ['', Validators.compose([
      Validators.maxLength(25),
      Validators.required])
    ],
    title: ['', Validators.maxLength(250)],
    duration: ['', Validators.compose([
      Validators.max(999),
      Validators.min(1)
    ])],
    projectPeriodLength: [''],
    projectPeriodCount: [''],
    introEn: [[], Validators.maxLength(2000)],
    intro: ['', Validators.maxLength(2000)],
    programmePriority: ['', Validators.required],
    specificObjective: ['', Validators.required]
  });

  specificObjectiveErrors = {
    required: 'project.objective.should.not.be.empty'
  };
  projectAcronymErrors = {
    maxlength: 'project.acronym.size.too.long',
    required: 'project.acronym.should.not.be.empty'
  };
  projectDurationErrors = {
    max: 'project.duration.size.max',
    min: 'project.duration.size.max',
  };
  programmePriorityErrors = {
    required: 'project.priority.should.not.be.empty'
  };

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              protected changeDetectorRef: ChangeDetectorRef,
              public projectStore: ProjectStore,
              public languageService: MultiLanguageInputService) {
    super();
  }

  ngOnInit(): void {
    this.formService.init(this.applicationForm);
    this.resetForm();
    if (this.editable) {
      this.applicationForm.controls.projectId.disable();
      this.applicationForm.controls.projectPeriodLength.disable();
      this.applicationForm.controls.projectPeriodCount.disable();
    } else {
      this.applicationForm.disable();
    }

    this.applicationForm.controls.duration?.valueChanges
      .pipe(
        takeUntil(this.destroyed$),
        distinctUntilChanged()
      )
      .subscribe(duration =>
        this.applicationForm.controls.projectPeriodCount.setValue(this.projectPeriodCount(duration))
      );
  }

  save(): void {
    const data = {
      intro: this.applicationForm.controls.intro.value,
      acronym: this.applicationForm.controls.acronym.value,
      title: this.applicationForm.controls.title.value,
      duration: this.applicationForm.controls.duration.value,
      specificObjective: this.selectedSpecificObjective
    };
    this.projectStore.updateProjectData(data)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.form.save.success')),
        catchError(error => this.formService.setError(error))
      ).subscribe();
  }

  resetForm(): void {
    this.applicationForm.controls.projectId.setValue(this.project.id);
    this.applicationForm.controls.acronym.setValue(this.project.acronym);
    this.applicationForm.controls.title.setValue(this.project?.projectData?.title);
    this.applicationForm.controls.duration.setValue(this.project?.projectData?.duration);
    this.applicationForm.controls.projectPeriodLength.setValue(this.project?.callSettings.lengthOfPeriod);
    this.applicationForm.controls.projectPeriodCount.setValue(
      this.projectPeriodCount(this.project?.projectData?.duration)
    );
    this.applicationForm.controls.intro.setValue(this.project?.projectData?.intro || []);
    if (!this.englishLanguageActive()) {
      this.applicationForm.controls.introEn.setValue(this.project?.projectData?.intro || []);
    }
    if (this.project?.projectData?.specificObjective) {
      this.previousObjective = this.project?.projectData?.specificObjective.programmeObjectivePolicy;
      this.selectedSpecificObjective = this.project?.projectData?.specificObjective.programmeObjectivePolicy;
      const prevPriority = this.project?.projectData?.programmePriority;
      this.currentPriority = prevPriority.code;
      this.applicationForm.controls.programmePriority.setValue(prevPriority.code);
      this.applicationForm.controls.specificObjective.setValue(this.selectedSpecificObjective);
    } else {
      this.currentPriority = undefined;
      this.applicationForm.controls.programmePriority.setValue(null);
      this.applicationForm.controls.specificObjective.setValue(null);
    }
  }

  private projectPeriodCount(projectDuration: number): number {
    return projectDuration ? Math.ceil(projectDuration / this.project?.callSettings.lengthOfPeriod) : 0;
  }

  changeCurrentPriority(selectedPriority: string): void {
    this.currentPriority = selectedPriority;
    this.applicationForm.controls.specificObjective.setValue('');
  }

  englishLanguageActive(): boolean {
    return !!this.languageService.inputLanguages.find(lang => this.LANGUAGE.EN === lang);
  }

  priorityTranslated(priority: OutputProgrammePrioritySimple, currentSystemLanguage: string | null): string {
    if (!currentSystemLanguage || !priority.code) {
      return '';
    }
    const elementInSystemLang = priority.title.find((it: InputTranslation) => it.language === currentSystemLanguage);
    return `${priority.code} - ${!!elementInSystemLang ? elementInSystemLang.translation : ''}`;
  }
}
