import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ControllerInstitutionDTO, NutsImportService, OutputNuts, ProgrammeDataService} from '@cat/api';
import {combineLatest, merge, Observable, of, ReplaySubject, Subject} from 'rxjs';
import {map, startWith, takeUntil, tap} from 'rxjs/operators';
import {BaseComponent} from '@common/components/base-component';
import {MatTreeNestedDataSource} from '@angular/material/tree';
import {Permission} from '../../../../security/permissions/permission';
import {JemsRegionCheckbox} from '@common/models/jems-region-checkbox';
import {InstitutionsPageStore} from '../../institutions-page-store.service';

@Component({
  selector: 'jems-controller-institution-nuts',
  templateUrl: './controller-institution-nuts.component.html',
  styleUrls: ['./controller-institution-nuts.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ControllerInstitutionNutsComponent extends BaseComponent implements OnInit {
  @Input()
  controllerData: ControllerInstitutionDTO | null;

  @Input()
  cancelEdit$: Observable<boolean>;
  @Input()
  isEditable: boolean;

  @Output()
  selectedNuts = new EventEmitter<string[]>();

  isLocked$: Observable<boolean> = of(false);
  Permission = Permission;
  regionTreeDataSource = new MatTreeNestedDataSource<JemsRegionCheckbox>();

  regionSaveSuccess$ = new Subject<boolean>();
  regionsAvailable$ = new Subject<boolean>();

  controllerInstitutionNuts$: Observable<OutputNuts[]>;

  saveSelectedRegions$ = new Subject<void>();

  selectionChanged$ = new ReplaySubject<JemsRegionCheckbox[]>(1);
  selectedRegions$ = this.selectionChanged$
    .pipe(
      tap(selected => this.selectedNuts.emit(this.collectSelectedIds(selected))),
      map(selected => this.getSelected(selected)),
    );

  constructor(private nutsService: NutsImportService,
              private programmeDataService: ProgrammeDataService,
              private controllerInstitutionStore: InstitutionsPageStore,
  ) {
    super();
  }

  ngOnInit(): void {
    this.controllerInstitutionNuts$ = of(this.controllerData?.institutionNuts || []);

    const savedInstitutionNuts$ = this.controllerInstitutionStore.updatedControllerInstitution.pipe(
      map(data => data.institutionNuts)
    );

    combineLatest([
      merge(this.controllerInstitutionNuts$, savedInstitutionNuts$),
      this.nutsService.getNuts(),
      this.cancelEdit$.pipe(
        startWith(null)
      )
    ])
      .pipe(
        takeUntil(this.destroyed$),
        map(([saved, nuts]) =>
          JemsRegionCheckbox.fromSelected(
            JemsRegionCheckbox.fromNuts(nuts), JemsRegionCheckbox.fromNuts(saved)
          )
        ),
        tap(regions => this.selectionChanged$.next(regions)),
        tap(regions => this.regionTreeDataSource.data = regions),
        tap(regions => this.regionsAvailable$.next(!!regions?.length))
      ).subscribe();
  }

  private getSelected(checkboxes: JemsRegionCheckbox[]): Map<string, JemsRegionCheckbox[]> {
    const selected = new Map<string, JemsRegionCheckbox[]>();
    checkboxes
      .filter(checkbox => checkbox.someChecked)
      .forEach(checkbox => {

        if (checkbox.checked) {
          selected.set(checkbox.title, []);
          return;
        }
        const children: JemsRegionCheckbox[] = [];
        this.collectSelectedGrouped(checkbox, children);
        selected.set(checkbox.title, children);
      });
    return selected;
  }

  private collectSelectedGrouped(checkbox: JemsRegionCheckbox, results: JemsRegionCheckbox[]): void {
    if (checkbox.allChildrenChecked() || (checkbox.code && checkbox.checked)) {
      results.push(checkbox);
      return;
    }
    checkbox.children.forEach(child => {
      this.collectSelectedGrouped(child, results);
    });
  }

  private collectSelectedChildren(checkbox: JemsRegionCheckbox, results: string[]): void {
    if (checkbox.code && checkbox.checked) {
      results.push(checkbox.code);
    }
    checkbox.children.forEach(child => {
      this.collectSelectedChildren(child, results);
    });
  }

  private collectSelectedIds(regions: JemsRegionCheckbox[]): string[] {
    return regions.flatMap(region => {
      const children: string[] = [];
      this.collectSelectedChildren(region, children);
      return children;
    });
  }
}
