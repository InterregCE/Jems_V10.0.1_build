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
import {MatTableDataSource} from '@angular/material/table';
import {ProjectRelevanceStrategy} from '../../dtos/project-relevance-strategy';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Permission} from 'src/app/security/permissions/permission';
import {InputProjectRelevanceStrategy, OutputCall} from '@cat/api';

@Component({
  selector: 'app-strategy-table',
  templateUrl: './strategy-table.component.html',
  styleUrls: ['./strategy-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class StrategyTableComponent implements OnInit, OnChanges {
  Permission = Permission;

  @Input()
  strategyDataSource: MatTableDataSource<ProjectRelevanceStrategy>;
  @Input()
  editableStrategyForm = new FormGroup({});
  @Input()
  editable: boolean;
  @Input()
  strategies: OutputCall.StrategiesEnum[];

  @Output()
  changed = new EventEmitter<void>();

  strategyEnum: string[] = [];

  displayedColumns: string[] = ['select', 'strategy', 'contribution', 'delete'];

  strategyCounter: number;

  strategyErrors = {
    required: 'project.application.form.relevance.strategy.not.empty',
  };
  contributionErrors = {
    maxlength: 'project.application.form.relevance.contribution.size.too.long'
  };

  ngOnInit(): void {
    if (this.editable) {
      this.strategyDataSource.data.forEach(strategy => this.addControl(strategy));
    }
    this.strategyCounter = this.strategyDataSource.data.length + 1;
    this.buildStrategyEnum();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.editableStrategyForm && this.editable) {
      this.strategyDataSource.data.forEach(strategy => this.addControl(strategy));
    }
  }

  addNewStrategy(): void {
    this.addControl(this.addLastStrategy());
    this.changed.emit();
  }

  strategy = (id: number): string => id + 'strat';
  contribution = (id: number): string => id + 'con';

  isValid(): boolean {
    return Object.keys(this.editableStrategyForm.controls)
      .every(control => this.editableStrategyForm.get(control)?.valid);
  }

  private addLastStrategy(): ProjectRelevanceStrategy {
    const lastStrategy = {
      id: this.strategyCounter,
      projectStrategy: 'Other',
      specification: ''
    } as ProjectRelevanceStrategy;
    this.strategyDataSource.data = [...this.strategyDataSource.data, lastStrategy];
    this.strategyCounter = this.strategyCounter + 1;
    return lastStrategy;
  }

  private addControl(strategy: ProjectRelevanceStrategy): void {
    this.editableStrategyForm.addControl(
      this.strategy(strategy.id),
      new FormControl(strategy?.projectStrategy, [])
    );
    this.editableStrategyForm.addControl(
      this.contribution(strategy.id),
      new FormControl(strategy?.specification, Validators.maxLength(2000))
    );
  }

  private buildStrategyEnum(): void {
    console.log(this.strategies);
    this.strategies.forEach((strategy) => {
      this.strategyEnum.push(InputProjectRelevanceStrategy.StrategyEnum[strategy]);
    });
    this.strategyEnum.push('Other');
  }

  deleteEntry(element: ProjectRelevanceStrategy): void {
    const index = this.strategyDataSource.data.indexOf(element);
    this.strategyDataSource.data.splice(index, 1);
    this.strategyDataSource._updateChangeSubscription();
    this.changed.emit();
  }
}
