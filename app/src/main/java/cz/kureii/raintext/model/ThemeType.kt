package cz.kureii.raintext.model

import cz.kureii.raintext.R

enum class ThemeType(val value: Int, val displayNameResId: Int) {
    SYSTEM(0, R.string.system),
    LIGHT(1, R.string.light),
    DARK(2, R.string.dark);
}