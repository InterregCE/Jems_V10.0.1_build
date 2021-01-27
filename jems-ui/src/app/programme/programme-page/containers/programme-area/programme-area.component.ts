import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {NutsImportService, ProgrammeDataService, OutputNuts} from '@cat/api';
import {combineLatest, merge, ReplaySubject, Subject} from 'rxjs';
import {catchError, mergeMap, map, startWith, take, takeUntil, tap} from 'rxjs/operators';
import {BaseComponent} from '@common/components/base-component';
import {Log} from '../../../../common/utils/log';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';
import {ProgrammeRegionCheckbox} from '../../model/programme-region-checkbox';
import {MatTreeNestedDataSource} from '@angular/material/tree';
import {ProgrammePageSidenavService} from '../../services/programme-page-sidenav.service';
import {Permission} from '../../../../security/permissions/permission';

@Component({
  selector: 'app-programme-area',
  templateUrl: './programme-area.component.html',
  styleUrls: ['./programme-area.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeAreaComponent extends BaseComponent implements OnInit {
  Permission = Permission;

  regionTreeDataSource = new MatTreeNestedDataSource<ProgrammeRegionCheckbox>();

  downloadLatestNuts$ = new Subject<void>();
  downloadSuccess$ = new Subject<boolean>();
  downloadError$ = new Subject<I18nValidationError | null>();
  regionSaveSuccess$ = new Subject<boolean>();

  private programmeNuts$ = this.programmeDataService.get()
    .pipe(
      tap(programmeData => Log.info('Fetched programme data:', this, programmeData)),
      map(programmeData => programmeData.programmeNuts)
    );

  private latestNutsMetadata$ = this.downloadLatestNuts$
    .pipe(
      mergeMap(() => this.nutsService.downloadLatestNuts()),
      tap(() => this.downloadSuccess$.next(true)),
      tap(() => this.downloadError$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.downloadError$.next(error.error);
        throw error;
      }),
      tap(metadata => Log.info('Download latest nuts', this, metadata))
    );
  private initialNutsMetadata$ = this.nutsService.getNutsMetadata()
    .pipe(
      tap(metadata => Log.info('Fetched initial metadata', this, metadata))
    );

  metaData$ = merge(this.initialNutsMetadata$, this.latestNutsMetadata$)
    .pipe(
      tap(metadata => {
        if (!metadata) {
          return;
        }
        this.nutsService.getNuts()
          .pipe(
            take(1),
            takeUntil(this.destroyed$),
            tap(nuts => this.savedNuts$.next(nuts))
          ).subscribe();
      })
    );

  savedNuts$ = new Subject<OutputNuts[]>();
  saveSelectedRegions$ = new Subject<void>();
  savedSelectedRegions$ = this.saveSelectedRegions$
    .pipe(
      mergeMap(() => this.programmeDataService.updateNuts(
        this.collectSelectedIds(this.regionTreeDataSource.data))
      ),
      tap(saved => Log.info('Saved selected regions', this, saved)),
      tap(() => this.regionSaveSuccess$.next(true)),
      map(programmeData => programmeData.programmeNuts)
    );
  selectionChanged$ = new ReplaySubject<ProgrammeRegionCheckbox[]>(1);
  selectedRegions$ = this.selectionChanged$
    .pipe(
      map(selected => this.getSelected(selected)),
    );
  cancelEdit$ = new Subject<void>();

  constructor(private nutsService: NutsImportService,
              private programmeDataService: ProgrammeDataService,
              private programmePageSidenavService: ProgrammePageSidenavService) {
    super();
    this.programmePageSidenavService.init(this.destroyed$);
  }

  ngOnInit(): void {
    combineLatest([
      merge(this.programmeNuts$, this.savedSelectedRegions$),
      this.savedNuts$,
      this.cancelEdit$.pipe(
        startWith(null)
      )
    ])
      .pipe(
        takeUntil(this.destroyed$),
        map(([saved, nuts]) =>
          ProgrammeRegionCheckbox.fromSelected(
            ProgrammeRegionCheckbox.fromNuts(nuts), ProgrammeRegionCheckbox.fromNuts(saved)
          )
        ),
        tap(regions => this.selectionChanged$.next(regions)),
        tap(regions => this.regionTreeDataSource.data = regions)
      ).subscribe();
  }

  private getSelected(checkboxes: ProgrammeRegionCheckbox[]): Map<string, ProgrammeRegionCheckbox[]> {
    const selected = new Map<string, ProgrammeRegionCheckbox[]>();
    checkboxes
      .filter(checkbox => checkbox.someChecked)
      .forEach(checkbox => {
        if (checkbox.checked) {
          selected.set(checkbox.title, []);
          return;
        }
        const children: ProgrammeRegionCheckbox[] = [];
        this.collectSelectedGrouped(checkbox, children);
        selected.set(checkbox.title, children);
      });
    return selected;
  }

  private collectSelectedGrouped(checkbox: ProgrammeRegionCheckbox, results: ProgrammeRegionCheckbox[]): void {
    if (checkbox.allChildrenChecked() || (checkbox.code && checkbox.checked)) {
      results.push(checkbox);
      return;
    }
    checkbox.children.forEach(child => {
      this.collectSelectedGrouped(child, results);
    });
  }

  private collectSelectedChildren(checkbox: ProgrammeRegionCheckbox, results: string[]): void {
    if (checkbox.code && checkbox.checked) {
      results.push(checkbox.code);
    }
    checkbox.children.forEach(child => {
      this.collectSelectedChildren(child, results);
    });
  }

  private collectSelectedIds(regions: ProgrammeRegionCheckbox[]): string[] {
    return regions.flatMap(region => {
      const children: string[] = [];
      this.collectSelectedChildren(region, children);
      return children;
    });
  }
}
