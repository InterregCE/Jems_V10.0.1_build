import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  InputProjectRelevance,
  InputProjectRelevanceBenefit,
  InputProjectRelevanceStrategy,
  InputProjectRelevanceSynergy
} from '@cat/api';
import {Observable} from 'rxjs';
import {takeUntil, tap} from 'rxjs/operators';
import {FormService} from '@common/components/section/form/form.service';
import {BaseComponent} from '@common/components/base-component';
import {HttpErrorResponse} from '@angular/common/http';
import {MultiLanguageInputService} from '../../../../../common/services/multi-language-input.service';
import {MultiLanguageInput} from '@common/components/forms/multi-language/multi-language-input';

@Component({
  selector: 'app-project-application-form-project-relevance-and-context-detail',
  templateUrl: './project-application-form-project-relevance-and-context-detail.component.html',
  styleUrls: ['./project-application-form-project-relevance-and-context-detail.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormProjectRelevanceAndContextDetailComponent extends BaseComponent implements OnInit {
  private static readonly TEXT_TOO_LONG = 'project.application.form.relevance.entered.text.size.too.long';

  // TODO: remove these and adapt the component to save independently
  @Input()
  error$: Observable<HttpErrorResponse | null>;
  @Input()
  success$: Observable<any>;

  @Input()
  editable: boolean;
  @Input()
  project: InputProjectRelevance;
  @Input()
  strategiesFromCall: InputProjectRelevanceStrategy.StrategyEnum[];
  @Output()
  updateData = new EventEmitter<InputProjectRelevance>();
  @Output()
  deleteData = new EventEmitter<InputProjectRelevance>();

  benefits: InputProjectRelevanceBenefit[];
  strategies: InputProjectRelevanceStrategy[];
  synergies: InputProjectRelevanceSynergy[];

  territorialChallenge: MultiLanguageInput;
  commonChallenge: MultiLanguageInput;
  transnationalCooperation: MultiLanguageInput;
  availableKnowledge: MultiLanguageInput;

  projectRelevanceForm: FormGroup = this.formBuilder.group({
    territorialChallenge: ['', Validators.maxLength(5000)],
    commonChallenge: ['', Validators.maxLength(5000)],
    transnationalCooperation: ['', Validators.maxLength(5000)],
    availableKnowledge: ['', Validators.maxLength(5000)],
    benefits: this.formBuilder.array([]),
    strategies: this.formBuilder.array([]),
    synergies: this.formBuilder.array([]),
  });

  territorialChallengeErrors = {
    maxlength: ProjectApplicationFormProjectRelevanceAndContextDetailComponent.TEXT_TOO_LONG
  };
  commonChallengeErrors = {
    maxlength: ProjectApplicationFormProjectRelevanceAndContextDetailComponent.TEXT_TOO_LONG
  };
  transnationalCooperationErrors = {
    maxlength: ProjectApplicationFormProjectRelevanceAndContextDetailComponent.TEXT_TOO_LONG
  };
  availableKnowledgeErrors = {
    maxlength: ProjectApplicationFormProjectRelevanceAndContextDetailComponent.TEXT_TOO_LONG
  };

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              public languageService: MultiLanguageInputService) {
    super();
  }

  ngOnInit(): void {
    this.resetForm();

    this.formService.init(this.projectRelevanceForm);
    this.formService.setAdditionalValidators([this.formValid.bind(this)]);
    this.error$
      .pipe(
        takeUntil(this.destroyed$),
        tap(err => this.formService.setError(err))
      )
      .subscribe();
    this.success$
      .pipe(
        takeUntil(this.destroyed$),
        tap(() => this.formService.setSuccess('project.application.form.relevance.save.success'))
      )
      .subscribe();
  }

  onSubmit(): void {
    this.updateData.emit({
      territorialChallenge: this.territorialChallenge.inputs,
      commonChallenge: this.commonChallenge.inputs,
      transnationalCooperation: this.transnationalCooperation.inputs,
      projectBenefits: this.buildBenefitsToSave(),
      projectStrategies: this.buildStrategiesToSave(),
      projectSynergies: this.buildSynergiesToSave(),
      availableKnowledge: this.availableKnowledge.inputs,
    });
  }

  resetForm(): void {
    this.territorialChallenge = this.languageService.initInput(this.project?.territorialChallenge, this.projectRelevanceForm.controls.territorialChallenge);
    this.commonChallenge = this.languageService.initInput(this.project?.commonChallenge, this.projectRelevanceForm.controls.commonChallenge);
    this.transnationalCooperation = this.languageService.initInput(this.project?.transnationalCooperation, this.projectRelevanceForm.controls.transnationalCooperation);
    this.availableKnowledge = this.languageService.initInput(this.project?.availableKnowledge, this.projectRelevanceForm.controls.availableKnowledge);

    this.benefits = this.project?.projectBenefits ? [...this.project.projectBenefits] : [];
    this.strategies = this.project?.projectStrategies ? [...this.project.projectStrategies] : [];
    this.synergies = this.project?.projectSynergies ? [...this.project.projectSynergies] : [];
  }

  private buildBenefitsToSave(): InputProjectRelevanceBenefit[] {
    return this.projectRelevanceForm.controls.benefits.value
      .map((element: any) => ({
        group: element.targetGroup,
        specification: element.specificationMultiInput.inputs
      }));
  }

  private buildStrategiesToSave(): InputProjectRelevanceStrategy[] {
    return this.projectRelevanceForm.controls.strategies.value
      .map((element: any) => ({
        strategy: element.strategy !== 'Other' ? element.strategy : null,
        specification: element.contributionMultiInput.inputs
      }));
  }

  private buildSynergiesToSave(): InputProjectRelevanceSynergy[] {
    return this.projectRelevanceForm.controls.synergies.value
      .map((element: any) => ({
        synergy: element.synergyMultiInput.inputs,
        specification: element.initiativeMultiInput.inputs
      }));
  }

  tableChanged(): void {
    this.formService.setDirty(true);
  }

  private formValid(): boolean {
    const benefitsValid = this.projectRelevanceForm.controls.benefits
      .value?.every((element: any) => element.specificationMultiInput?.isValid());
    const strategiesValid = this.projectRelevanceForm.controls.strategies
      .value?.every((element: any) => element.contributionMultiInput?.isValid());
    const synergiesValid = this.projectRelevanceForm.controls.synergies
      .value?.every((element: any) => element.initiativeMultiInput?.isValid() && element.synergyMultiInput?.isValid());

    return benefitsValid
      && strategiesValid
      && synergiesValid
      && this.territorialChallenge.isValid()
      && this.commonChallenge.isValid()
      && this.transnationalCooperation.isValid()
      && this.availableKnowledge.isValid();
  }
}
