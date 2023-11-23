import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {TooltipPosition} from '@angular/material/tooltip';
import { LanguageStore } from '@common/services/language-store.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'jems-context-info-bubble',
    templateUrl: './context-info-bubble.component.html',
    styleUrls: ['./context-info-bubble.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ContextInfoBubbleComponent implements OnInit {

    @Input()
    infoText?: string;
    @Input()
    icon?: string = 'info_outline';
    @Input()
    link?: string;
    @Input()
    infoPosition: TooltipPosition = 'after';

    isDisplayed = false;
    infoTextTranslated = '';

    linkTranslated = '';
    isLinkActive = false;
    // CSS Class name to enable newline character (\n) in ToolTip component
    newLineClass = 'newline';

    constructor(private translateService: TranslateService, private languageStore: LanguageStore) { }

    ngOnInit(): void {
        if(!this.infoText) {
            this.isDisplayed = false;
            return;
        }

        this.infoTextTranslated = this.translateService.instant(this.infoText);
            if (this.infoTextTranslated !== this.infoText && this.infoTextTranslated !== '') {
                this.isDisplayed = true;
            } else {
                this.isDisplayed = false;
                return;
            }

            if(this.link) {
                this.linkTranslated = this.translateService.instant(this.link);
                if (this.linkTranslated !== this.link && this.linkTranslated !== '') {
                    this.infoTextTranslated = this.infoTextTranslated+'\n'
                        +this.translateService.instant('common.infobubble.follow.link.text')+'\n'
                        +this.trimLink(this.linkTranslated,64);
                    this.isLinkActive = true;
                } else {
                    this.isLinkActive = false;
                }
            }
        }

    openLink() {
        if(this.isLinkActive) {window.open(this.linkTranslated, '_blank');}
    }

    // Trim Function to limit url size showing in the info bubble (preventing to show huge ToolTip) 64 Lenght shows 2 lines in the bubble
    trimLink(link: string, size: number): string {
        if (link.length <= size ){
            return link;
        }
        return link.slice(0, size) + '...';
    }
}
