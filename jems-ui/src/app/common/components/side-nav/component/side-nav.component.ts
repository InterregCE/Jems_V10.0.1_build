import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {ResolveEnd, Router} from '@angular/router';
import {BehaviorSubject} from 'rxjs';
import {filter, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';

@UntilDestroy()
@Component({
  selector: 'jems-side-nav',
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SideNavComponent {

  @Input()
  headlines: HeadlineRoute[];

  currentUrl = new BehaviorSubject<string>(this.router.url);
  projectId: number;
  projectOverviewUrl: string;

  constructor(public sideNavService: SideNavService,
              private router: Router,
              private projectStore: ProjectStore) {
    this.router.events
      .pipe(
        filter(val => val instanceof ResolveEnd),
        tap((event: ResolveEnd) => this.currentUrl.next(event.url)),
        untilDestroyed(this)
      ).subscribe();

    this.projectStore.project$.pipe(
        tap(project => this.projectId = project.id),
        tap(project => this.projectOverviewUrl = '/app/project/detail/' + project.id),
        untilDestroyed(this)
    ).subscribe();
  }
}
