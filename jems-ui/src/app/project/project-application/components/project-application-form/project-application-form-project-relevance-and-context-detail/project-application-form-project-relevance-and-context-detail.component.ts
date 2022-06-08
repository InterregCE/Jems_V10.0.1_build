import {
  ChangeDetectionStrategy,
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges
} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  CallDetailDTO,
  InputProjectRelevance,
  InputProjectRelevanceBenefit, ProjectRelevanceSpfRecipientDTO,
  InputProjectRelevanceStrategy,
  InputProjectRelevanceSynergy, ProjectDescriptionService
} from '@cat/api';
import {Observable} from 'rxjs';
import {catchError, tap} from 'rxjs/operators';
import {FormService} from '@common/components/section/form/form.service';
import {BaseComponent} from '@common/components/base-component';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {Log} from '@common/utils/log';

@Component({
  selector: 'jems-project-application-form-project-relevance-and-context-detail',
  templateUrl: './project-application-form-project-relevance-and-context-detail.component.html',
  styleUrls: ['./project-application-form-project-relevance-and-context-detail.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormProjectRelevanceAndContextDetailComponent extends BaseComponent implements OnInit, OnChanges {
  APPLICATION_FORM = APPLICATION_FORM;

  @Input()
  projectId: number;
  @Input()
  editable: boolean;
  @Input()
  inputProjectRelevance: InputProjectRelevance;
  @Input()
  strategiesFromCall: InputProjectRelevanceStrategy.StrategyEnum[];

  benefits: InputProjectRelevanceBenefit[];
  spfRecipients: ProjectRelevanceSpfRecipientDTO[];
  strategies: InputProjectRelevanceStrategy[];
  synergies: InputProjectRelevanceSynergy[];

  callType$: Observable<CallDetailDTO.TypeEnum>;

  projectRelevanceForm: FormGroup = this.formBuilder.group({
    territorialChallenge: [[], Validators.maxLength(5000)],
    commonChallenge: [[], Validators.maxLength(5000)],
    transnationalCooperation: [[], Validators.maxLength(5000)],
    availableKnowledge: [[], Validators.maxLength(5000)],
    benefits: this.formBuilder.array([]),
    spfRecipients: this.formBuilder.array([]),
    strategies: this.formBuilder.array([]),
    synergies: this.formBuilder.array([]),
  });

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              public projectStore: ProjectStore,
              private projectDescriptionService: ProjectDescriptionService) {
    super();
    this.callType$ = projectStore.projectCallType$;
  }

  ngOnInit(): void {
    this.formService.init(this.projectRelevanceForm, this.projectStore.projectEditable$);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.inputProjectRelevance) {
      this.resetForm();
    }
  }

  onSubmit(): void {
    this.projectDescriptionService.updateProjectRelevance(this.projectId, this.createInputProjectRelevance())
      .pipe(
        tap(saved => Log.info('Updated project relevance and context:', this, saved)),
        tap(() => this.formService.setSuccess('project.application.form.save.success')),
        catchError(error => this.formService.setError(error))
      ).subscribe();
  }

  createInputProjectRelevance(): InputProjectRelevance {
    return {
      ...this.projectRelevanceForm.value,
      projectBenefits: this.buildBenefitsToSave(),
      projectSpfRecipients: this.buildSpfRecipientsToSave(),
      projectStrategies: this.buildStrategiesToSave(),
      projectSynergies: this.buildSynergiesToSave()
    };
  }

  resetForm(): void {
    this.projectRelevanceForm.get('territorialChallenge')?.setValue(this.inputProjectRelevance?.territorialChallenge || []);
    this.projectRelevanceForm.get('commonChallenge')?.setValue(this.inputProjectRelevance?.commonChallenge || []);
    this.projectRelevanceForm.get('transnationalCooperation')?.setValue(this.inputProjectRelevance?.transnationalCooperation || []);
    this.projectRelevanceForm.get('availableKnowledge')?.setValue(this.inputProjectRelevance?.availableKnowledge || []);
    this.benefits = this.inputProjectRelevance?.projectBenefits ? [...this.inputProjectRelevance.projectBenefits] : [];
    this.spfRecipients = this.inputProjectRelevance?.projectSpfRecipients ? [...this.inputProjectRelevance.projectSpfRecipients] : [];
    this.strategies = this.inputProjectRelevance?.projectStrategies ? [...this.inputProjectRelevance.projectStrategies] : [];
    this.synergies = this.inputProjectRelevance?.projectSynergies ? [...this.inputProjectRelevance.projectSynergies] : [];
    this.formService.resetEditable();
  }

  private buildBenefitsToSave(): InputProjectRelevanceBenefit[] {
    return this.projectRelevanceForm.controls.benefits.value
      .map((element: any) => ({
        group: element.targetGroup,
        specification: element.specification
      }));
  }

  private buildSpfRecipientsToSave(): ProjectRelevanceSpfRecipientDTO[] {
    return this.projectRelevanceForm.controls.spfRecipients.value
      .map((element: any) => ({
        recipientGroup: element.recipientGroup,
        specification: element.specification
      }));
  }

  private buildStrategiesToSave(): InputProjectRelevanceStrategy[] {
    return this.projectRelevanceForm.controls.strategies.value
      .map((element: any) => ({
        strategy: element.strategy,
        specification: element.contribution
      }));
  }

  private buildSynergiesToSave(): InputProjectRelevanceSynergy[] {
    return this.projectRelevanceForm.controls.synergies.value
      .map((element: any) => ({
        synergy: element.synergy,
        specification: element.initiative
      }));
  }

  tableChanged(): void {
    this.formService.setDirty(true);
  }
}
