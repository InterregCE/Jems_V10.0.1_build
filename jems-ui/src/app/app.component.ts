import {Component, HostBinding} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {Title} from '@angular/platform-browser';
import {BaseComponent} from '@common/components/base-component';
import {ThemeService} from './theme/theme.service';

@Component({
  selector: 'app-root',
  styleUrls: ['./app.component.scss'],
  templateUrl: './app.component.html'
})
export class AppComponent extends BaseComponent {

  @HostBinding('class') componentCssClass = 'light-theme';

  constructor(public translate: TranslateService,
              public themeService: ThemeService,
              private titleService: Title) {
    super();
    this.titleService.setTitle('Jems');
    themeService.$currentTheme
      .subscribe(theme => this.componentCssClass = theme);
  }

  onSetTheme(theme: string) {
    this.themeService.$currentTheme.next(theme);
  }

}
