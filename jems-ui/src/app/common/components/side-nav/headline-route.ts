import {I18nLabel} from '../../i18n/i18n-label';

export class HeadlineRoute {
  headline: I18nLabel;
  route?: string;
  scrollRoute?: string;
  scrollToTop ? = false;
  bullets?: HeadlineRoute[] = [];
}
