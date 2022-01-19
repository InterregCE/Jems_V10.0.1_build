import {ChangeDetectionStrategy, Component} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {Observable} from 'rxjs';
import {map, tap} from 'rxjs/operators';
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
  lumpSum$ = this.lumpSumsStore.lumpSum();
  unitCost$ = this.unitCostStore.unitCost();

  lumpSumsDataSource$: Observable<MatTableDataSource<ProgrammeLumpSumListDTO>> = this.programmeCostOptionService.getProgrammeLumpSums()
    .pipe(
      tap(list => Log.info('Fetched the Lump Sums:', this, list)),
      map(list => new MatTableDataSource(list))
    );
  unitCostDataSource$: Observable<MatTableDataSource<ProgrammeUnitCostListDTO>> = this.programmeCostOptionService.getProgrammeUnitCosts()
    .pipe(
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
