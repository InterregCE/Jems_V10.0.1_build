import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges
} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  InputProjectData,
  InputTranslation,
  OutputProgrammePrioritySimple,
  ProjectDetailDTO,
  ProjectDetailFormDTO, ProjectPartnerDetailDTO
} from '@cat/api';
import {Permission} from '../../../../security/permissions/permission';
import {Tools} from '@common/utils/tools';
import {catchError, distinctUntilChanged, take, takeUntil, tap} from 'rxjs/operators';
import {BaseComponent} from '@common/components/base-component';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectStore} from '../../containers/project-application-detail/services/project-store.service';
import {LanguageStore} from '@common/services/language-store.service';
import { APPLICATION_FORM } from '@project/common/application-form-model';
import {Alert} from '@common/components/forms/alert';

@Component({
  selector: 'app-project-application-form',
  templateUrl: './project-application-form.component.html',
  styleUrls: ['./project-application-form.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormComponent extends BaseComponent implements OnInit, OnChanges {
  Alert = Alert;
  Permission = Permission;
  tools = Tools;
  LANGUAGE = InputTranslation.LanguageEnum;
  APPLICATION_FORM = APPLICATION_FORM;
  @Input()
  project: ProjectDetailDTO;
  @Input()
  projectForm: ProjectDetailFormDTO;
  @Input()
  leadPartner: ProjectPartnerDetailDTO | null;
  @Input()
  priorities: OutputProgrammePrioritySimple[];
  @Input()
  objectivesWithPolicies: { [key: string]: InputProjectData.SpecificObjectiveEnum[] };

  currentPriority?: string;
  previousObjective: InputProjectData.SpecificObjectiveEnum;
  selectedSpecificObjective: InputProjectData.SpecificObjectiveEnum;

  applicationForm: FormGroup = this.formBuilder.group({
    projectId: [''],
    nameOfTheLeadPartner: [''],
    nameOfTheLeadPartnerInEnglish: [''],
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
              public languageStore: LanguageStore) {
    super();
  }

  ngOnInit(): void {
    this.formService.init(this.applicationForm, this.projectStore.projectEditable$);
    this.resetForm();

    this.applicationForm.controls.duration?.valueChanges
      .pipe(
        takeUntil(this.destroyed$),
        distinctUntilChanged()
      )
      .subscribe(duration =>
        this.applicationForm.controls.projectPeriodCount.setValue(this.projectPeriodCount(duration))
      );

    this.projectStore.projectEditable$
      .pipe(takeUntil(this.destroyed$))
      .subscribe(() => {
        this.applicationForm.controls.projectId.disable();
        this.applicationForm.controls.nameOfTheLeadPartner.disable();
        this.applicationForm.controls.nameOfTheLeadPartnerInEnglish.disable();
        this.applicationForm.controls.projectPeriodLength.disable();
        this.applicationForm.controls.projectPeriodCount.disable();
      });
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.resetForm();
  }

  save(): void {
    const data = {
      intro: this.applicationForm.controls.intro.value,
      acronym: this.applicationForm.controls.acronym.value,
      title: this.applicationForm.controls.title.value,
      duration: this.applicationForm.controls.duration.value,
      specificObjective: this.selectedSpecificObjective
    } as InputProjectData;
    const english = this.applicationForm.controls.introEn?.value?.find((translation: any) => translation.language === 'EN');
    if (english) {
      data.intro = data.intro.filter(translation => translation.language !== 'EN')
        .concat(english);
    }
    this.projectStore.updateProjectData(data)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.form.save.success')),
        catchError(error => this.formService.setError(error))
      ).subscribe();
  }

  resetForm(): void {
    this.applicationForm.controls.projectId.setValue(this.projectForm.customIdentifier);
    this.applicationForm.controls.nameOfTheLeadPartner.setValue(this.leadPartner?.nameInOriginalLanguage);
    this.applicationForm.controls.nameOfTheLeadPartnerInEnglish.setValue(this.leadPartner?.nameInEnglish);
    this.applicationForm.controls.acronym.setValue(this.projectForm.acronym);
    this.applicationForm.controls.title.setValue(this.projectForm?.title);
    this.applicationForm.controls.duration.setValue(this.projectForm?.duration);
    this.applicationForm.controls.projectPeriodLength.setValue(this.project?.callSettings.lengthOfPeriod);
    this.applicationForm.controls.projectPeriodCount.setValue(
      this.projectPeriodCount(this.projectForm?.duration)
    );
    this.applicationForm.controls.intro.setValue(this.projectForm?.intro || []);
    if (!this.languageStore.isInputLanguageExist(this.LANGUAGE.EN)) {
      this.applicationForm.controls.introEn.setValue(this.projectForm?.intro || []);
    }
    if (this.projectForm?.specificObjective) {
      this.previousObjective = this.projectForm?.specificObjective.programmeObjectivePolicy;
      this.selectedSpecificObjective = this.projectForm?.specificObjective.programmeObjectivePolicy;
      const prevPriority = this.projectForm?.programmePriority;
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
}
