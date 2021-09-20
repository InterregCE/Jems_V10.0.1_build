import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {ResolveEnd, Router} from '@angular/router';
import {BehaviorSubject} from 'rxjs';
import {filter, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-side-nav',
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SideNavComponent {

  @Input()
  headlines: HeadlineRoute[];

  currentUrl$ = new BehaviorSubject<string>(this.router.url);

  constructor(public sideNavService: SideNavService, private router: Router) {
    this.router.events
      .pipe(
        filter(val => val instanceof ResolveEnd),
        tap((event: ResolveEnd) => this.currentUrl$.next(event.url)),
        untilDestroyed(this)
      ).subscribe();
  }
}
