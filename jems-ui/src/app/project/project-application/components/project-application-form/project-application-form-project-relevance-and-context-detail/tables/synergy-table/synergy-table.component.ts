import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {MatTableDataSource} from '@angular/material/table';
import {ProjectRelevanceSynergy} from '../../dtos/project-relevance-synergy';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {Permission} from 'src/app/security/permissions/permission';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'app-synergy-table',
  templateUrl: './synergy-table.component.html',
  styleUrls: ['./synergy-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SynergyTableComponent extends BaseComponent implements OnInit {
  Permission = Permission;

  @Input()
  synergyDataSource: MatTableDataSource<ProjectRelevanceSynergy>;
  @Input()
  editableSynergyForm = new FormGroup({});
  @Input()
  disabled: boolean;
  @Input()
  changedFormState$: Observable<null>;

  displayedColumns: string[] = ['select', 'project', 'synergy', 'delete'];

  synergyCounter: number;

  projectErrors = {
    maxlength: 'project.application.form.relevance.project.size.too.long',
  };
  synergyErrors = {
    maxlength: 'project.application.form.relevance.synergy.size.too.long'
  };

  constructor(private dialog: MatDialog) {
    super();
  }

  ngOnInit(): void {
    this.changedFormState$
      .pipe(
        takeUntil(this.destroyed$)
      )
      .subscribe(() => {
        this.synergyDataSource.data.forEach(synergy => this.addControl(synergy));
      });
    this.synergyCounter = this.synergyDataSource.data.length + 1;
  }

  addNewSynergy(): void {
    this.addControl(this.addLastSynergy());
  }

  projectInitiative = (id: number): string => id + 'projIn';
  synergy = (id: number): string => id + 'syn';

  isValid(): boolean {
    return Object.keys(this.editableSynergyForm.controls)
      .every(control => this.editableSynergyForm.get(control)?.valid);
  }

  private addLastSynergy(): ProjectRelevanceSynergy {
    const lastSynergy = {
      id: this.synergyCounter,
      specification: '',
      synergy: ''
    } as ProjectRelevanceSynergy;
    this.synergyDataSource.data = [...this.synergyDataSource.data, lastSynergy];
    this.synergyCounter = this.synergyCounter + 1;
    return lastSynergy;
  }

  private addControl(synergy: ProjectRelevanceSynergy): void {
    this.editableSynergyForm.addControl(
      this.projectInitiative(synergy.id),
      new FormControl(synergy?.specification, Validators.maxLength(500))
    );
    this.editableSynergyForm.addControl(
      this.synergy(synergy.id),
      new FormControl(synergy?.synergy, Validators.maxLength(2000))
    );
  }

  deleteEntry(element: ProjectRelevanceSynergy): void {
    const index = this.synergyDataSource.data.indexOf(element);
    this.synergyDataSource.data.splice(index, 1);
    this.synergyDataSource._updateChangeSubscription();
  }
}
