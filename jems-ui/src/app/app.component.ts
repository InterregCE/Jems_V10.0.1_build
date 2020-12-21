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

  isChristmas = false;

  @HostBinding('class') componentCssClass = 'light-theme';

  constructor(public translate: TranslateService,
              public themeService: ThemeService,
              private titleService: Title) {
    super();
    this.titleService.setTitle('Jems');
    themeService.$currentTheme
      .pipe(
        takeUntil(this.destroyed$)
      )
      .subscribe(theme => this.componentCssClass = theme);

    const today = new Date();
    this.isChristmas = today.getMonth() === 11 && today.getDate() >= 24;
  }

  onSetTheme(theme: string): void {
    this.themeService.$currentTheme.next(theme);
  }

}
