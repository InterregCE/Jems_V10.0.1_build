package io.cloudflight.jems.api.programme.dto.language

/**
 * Language keys using ISO_639-1 codes.
 */
enum class SystemLanguage(val translationKey: String) {
    BE("language.be"),  // be-by
    BG("language.bg"),  // bg-bg
    CS("language.cs"),  // cs-cz
    DA("language.da"),  // da-dk
    DE("language.de"),  // de-de
    EL("language.el"),  // el-gr
    EN("language.en"),  // en-gb
    ES("language.es"),  // es-es
    ET("language.et"),  // et-ee
    FI("language.fi"),  // fi-fi
    FR("language.fr"),  // fr-fr
    GA("language.ga"),  // ga-ie
    HR("language.hr"),  // hr-hr
    HU("language.hu"),  // hu-hu
    IT("language.it"),  // it-it
    JA("language.ja"),  // ja-jp
    LB("language.lb"),  // lb-lu
    LT("language.lt"),  // lt-lt
    LV("language.lv"),  // lv-lv
    MT("language.mt"),  // mt-mt
    MK("language.mk"),  // mk-mk
    NL("language.nl"),  // nl-nl
    NO("language.no"),  // no-no
    PL("language.pl"),  // pl-pl
    PT("language.pt"),  // pt-pt
    RO("language.ro"),  // ro-ro
    RU("language.ru"),  // ru-ru
    SK("language.sk"),  // sk-sk
    SL("language.sl"),  // sl-si
    SQ("language.sq"),  // sq-al
    SR("language.sr"),  // sr-rs
    SV("language.sv"),  // sv-se
    TR("language.tr"),  // tr-tr
    UK("language.uk");  // uk-ua

    fun isSelected(languages: String) = languages.contains(name)

}
