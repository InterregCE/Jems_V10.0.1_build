import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {combineLatest, Observable, Subject} from 'rxjs';
import {map, startWith, switchMap, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {ProgrammePageSidenavService} from '../../services/programme-page-sidenav.service';
import {LumpSumsStore} from '../../services/lump-sums-store.service';
import {ProgrammeCostOptionService, ProgrammeLumpSumListDTO, ProgrammeUnitCostListDTO, UserRoleDTO} from '@cat/api';
import {UnitCostStore} from '../../services/unit-cost-store.service';
import {MatTableDataSource} from '@angular/material/table';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'jems-programme-simplified-cost-options',
  templateUrl: './programme-simplified-cost-options.component.html',
  styleUrls: ['./programme-simplified-cost-options.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeSimplifiedCostOptionsComponent extends BaseComponent {

  PermissionsEnum = PermissionsEnum;

  lumpSumDeleted$ = new Subject<void>();
  unitCostDeleted$ = new Subject<void>();

  lumpSumsDataSource$: Observable<MatTableDataSource<ProgrammeLumpSumListDTO>> =
    combineLatest(
    [this.lumpSumDeleted$.pipe(startWith(null)
    )])
    .pipe(
      switchMap(() => this.programmeCostOptionService.getProgrammeLumpSums()),
      tap(list => Log.info('Fetched the Lump Sums:', this, list)),
      map(list => new MatTableDataSource(list))
    );
  unitCostDataSource$: Observable<MatTableDataSource<ProgrammeUnitCostListDTO>> =
    combineLatest(
      [this.unitCostDeleted$.pipe(startWith(null)
    )])
    .pipe(
      switchMap(() => this.programmeCostOptionService.getProgrammeUnitCosts()),
      tap(list => Log.info('Fetched the Unit Costs:', this, list)),
      map(list => new MatTableDataSource(list))
    );

  constructor(private lumpSumsStore: LumpSumsStore,
              private unitCostStore: UnitCostStore,
              private programmeCostOptionService: ProgrammeCostOptionService,
              private programmePageSidenavService: ProgrammePageSidenavService) {
    super();
  }

}
