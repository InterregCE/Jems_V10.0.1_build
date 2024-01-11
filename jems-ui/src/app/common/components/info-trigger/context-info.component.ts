import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {TooltipPosition} from '@angular/material/tooltip';
import {TranslateService} from "@ngx-translate/core";
import {LanguageStore} from "@common/services/language-store.service";

@Component({
  selector: 'jems-context-info',
  templateUrl: './context-info.component.html',
  styleUrls: ['./context-info.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ContextInfoComponent implements OnInit{

  @Input()
  infoText?: string;

  @Input()
  icon?: string = 'info_outline';

  /*
    InfoPosition
    Position of context-info icon in relation to parent.
    Use before/after/above/below to absolutely position in relation to parent (parent needs rel. positioning)
    Use left/right to position for inline context like headlines (wrap both inside span)
   */
  @Input()
  infoPosition: TooltipPosition = 'after';

  @Input()
  noWidth: Boolean = false;

  extractedUrl = '';
  isDisplayed = true;

  isLinkActive = false;
  // CSS Class name to enable newline character (\n) in ToolTip component
  newLineClass = 'newline';

  constructor(private translateService: TranslateService, private languageStore: LanguageStore) { }

  ngOnInit(): void {
    if(!this.infoText) {
      this.isDisplayed = false;
      return;
    }

      const urlRegex = /\[\[([^[\]]*)\]\]/;
      const match = this.infoText.match(urlRegex);

      if (match && match.length > 1) {
          this.extractedUrl = match[1];

          // remove url from infoText
          this.infoText = this.infoText.replace(match[0], '');

          this.infoText = this.infoText + '\n'
              + this.translateService.instant('common.infobubble.follow.link.text') + '\n'
              + this.trimLink(this.extractedUrl, 64);
          this.isLinkActive = true;
      }
  }

  openLink() {
    if (this.isLinkActive) {
      window.open(this.extractedUrl, '_blank');
    }
  }

  trimLink(link: string, size: number): string {
    if (link.length <= size ){
      return link;
    }
    return link.slice(0, size) + '...';
  }

}
