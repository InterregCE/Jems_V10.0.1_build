import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges
} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  CallDetailDTO,
  InputProjectRelevance,
  InputProjectRelevanceBenefit, ProjectRelevanceSpfRecipientDTO,
  InputProjectRelevanceStrategy,
  InputProjectRelevanceSynergy
} from '@cat/api';
import {Observable} from 'rxjs';
import {takeUntil, tap} from 'rxjs/operators';
import {FormService} from '@common/components/section/form/form.service';
import {BaseComponent} from '@common/components/base-component';
import {HttpErrorResponse} from '@angular/common/http';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';
import { APPLICATION_FORM } from '@project/common/application-form-model';

@Component({
  selector: 'jems-project-application-form-project-relevance-and-context-detail',
  templateUrl: './project-application-form-project-relevance-and-context-detail.component.html',
  styleUrls: ['./project-application-form-project-relevance-and-context-detail.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormProjectRelevanceAndContextDetailComponent extends BaseComponent implements OnInit, OnChanges {
  APPLICATION_FORM = APPLICATION_FORM;
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
              public projectStore: ProjectStore) {
    super();
    this.callType$ = projectStore.projectCallType$;
  }

  ngOnInit(): void {

    this.formService.init(this.projectRelevanceForm, this.projectStore.projectEditable$);
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

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.project) {
      this.resetForm();
    }
  }

  onSubmit(): void {
    this.updateData.emit({
      ...this.projectRelevanceForm.value,
      projectBenefits: this.buildBenefitsToSave(),
      projectSpfRecipients: this.buildSpfRecipientsToSave(),
      projectStrategies: this.buildStrategiesToSave(),
      projectSynergies: this.buildSynergiesToSave(),
    });
  }

  resetForm(): void {
    this.projectRelevanceForm.get('territorialChallenge')?.setValue(this.project?.territorialChallenge || []);
    this.projectRelevanceForm.get('commonChallenge')?.setValue(this.project?.commonChallenge || []);
    this.projectRelevanceForm.get('transnationalCooperation')?.setValue(this.project?.transnationalCooperation || []);
    this.projectRelevanceForm.get('availableKnowledge')?.setValue(this.project?.availableKnowledge || []);
    this.benefits = this.project?.projectBenefits ? [...this.project.projectBenefits] : [];
    this.spfRecipients = this.project?.projectSpfRecipients ? [...this.project.projectSpfRecipients] : [];
    this.strategies = this.project?.projectStrategies ? [...this.project.projectStrategies] : [];
    this.synergies = this.project?.projectSynergies ? [...this.project.projectSynergies] : [];
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
