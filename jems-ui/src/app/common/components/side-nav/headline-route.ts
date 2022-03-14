import {I18nLabel} from '../../i18n/i18n-label';
import {TemplateRef} from '@angular/core';

export class HeadlineRoute {
  headline?: I18nLabel;
  route?: string;
  baseRoute?: string;
  scrollRoute?: string;
  scrollToTop ? = false;
  bullets?: HeadlineRoute[] = [];
  badgeText?: string;
  badgeTooltip?: string;
  icon?: string;
  versionedSection ? = false;
  expanded ? = false;

  // TODO: refactor this out in a HeadlineTemplate or a generic base Headline
  headlineTemplate?: TemplateRef<any>;
}
