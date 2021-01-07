import {ChangeDetectionStrategy, Component, HostBinding} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {Title} from '@angular/platform-browser';
import {BaseComponent} from '@common/components/base-component';
import {ThemeService} from './theme/theme.service';
import {takeUntil} from 'rxjs/operators';

@Component({
  selector: 'app-root',
  styleUrls: ['./app.component.scss'],
  templateUrl: './app.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent extends BaseComponent {

  @HostBinding('class') componentCssClass = 'light-theme';

  constructor(public translate: TranslateService,
              public themeService: ThemeService,
              private readonly titleService: Title) {
    super();
    this.titleService.setTitle('Jems');
    themeService.$currentTheme
      .pipe(
        takeUntil(this.destroyed$)
      )
      .subscribe(theme => this.componentCssClass = theme);
  }

  onSetTheme(theme: string): void {
    this.themeService.$currentTheme.next(theme);
  }

}
