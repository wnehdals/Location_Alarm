package com.jdm.designsystem.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color

@Stable
class JdsColors(
    white: Color,
    black: Color,
    gray50: Color,
    gray100: Color,
    gray200: Color,
    gray300: Color,
    gray400: Color,
    gray500: Color,
    gray600: Color,
    gray700: Color,
    gray800: Color,
    gray900: Color,
    red50: Color,
    red100: Color,
    red200: Color,
    red300: Color,
    red400: Color,
    red500: Color,
    red600: Color,
    red700: Color,
    orange50: Color,
    orange100: Color,
    orange200: Color,
    orange300: Color,
    orange400: Color,
    orange500: Color,
    orange600: Color,
    orange700: Color,
    yellow50: Color,
    yellow100: Color,
    yellow200: Color,
    yellow300: Color,
    yellow400: Color,
    yellow500: Color,
    yellow600: Color,
    yellow700: Color,
    green50: Color,
    green100: Color,
    green200: Color,
    green300: Color,
    green400: Color,
    green500: Color,
    green600: Color,
    green700: Color,
    blue50: Color,
    blue100: Color,
    blue200: Color,
    blue300: Color,
    blue400: Color,
    blue500: Color,
    blue600: Color,
    blue700: Color,
    purple50: Color,
    purple100: Color,
    purple200: Color,
    purple300: Color,
    purple400: Color,
    purple500: Color,
    purple600: Color,
    purple700: Color,
    magenta50: Color,
    magenta100: Color,
    magenta200: Color,
    magenta300: Color,
    magenta400: Color,
    magenta500: Color,
    magenta600: Color,
    magenta700: Color,
    transparent: Color
) {
    var white by mutableStateOf(white, structuralEqualityPolicy())
        internal  set
    var black by mutableStateOf(black, structuralEqualityPolicy())
        internal  set
    var gray50 by mutableStateOf(gray50, structuralEqualityPolicy())
        internal  set
    var gray100 by mutableStateOf(gray100, structuralEqualityPolicy())
        internal  set
    var gray200 by mutableStateOf(gray200, structuralEqualityPolicy())
        internal  set
    var gray300 by mutableStateOf(gray300, structuralEqualityPolicy())
        internal  set
    var gray400 by mutableStateOf(gray400, structuralEqualityPolicy())
        internal  set
    var gray500 by mutableStateOf(gray500, structuralEqualityPolicy())
        internal  set
    var gray600 by mutableStateOf(gray600, structuralEqualityPolicy())
        internal  set
    var gray700 by mutableStateOf(gray700, structuralEqualityPolicy())
        internal  set
    var gray800 by mutableStateOf(gray800, structuralEqualityPolicy())
        internal  set
    var gray900 by mutableStateOf(gray900, structuralEqualityPolicy())
        internal  set
    var red50 by mutableStateOf(red50, structuralEqualityPolicy())
        internal  set
    var red100 by mutableStateOf(red100, structuralEqualityPolicy())
        internal  set
    var red200 by mutableStateOf(red200, structuralEqualityPolicy())
        internal  set
    var red300 by mutableStateOf(red300, structuralEqualityPolicy())
        internal  set
    var red400 by mutableStateOf(red400, structuralEqualityPolicy())
        internal  set
    var red500 by mutableStateOf(red500, structuralEqualityPolicy())
        internal  set
    var red600 by mutableStateOf(red600, structuralEqualityPolicy())
        internal  set
    var red700 by mutableStateOf(red700, structuralEqualityPolicy())
        internal  set
    var orange50 by mutableStateOf(orange50, structuralEqualityPolicy())
        internal  set
    var orange100 by mutableStateOf(orange100, structuralEqualityPolicy())
        internal  set
    var orange200 by mutableStateOf(orange200, structuralEqualityPolicy())
        internal  set
    var orange300 by mutableStateOf(orange300, structuralEqualityPolicy())
        internal  set
    var orange400 by mutableStateOf(orange400, structuralEqualityPolicy())
        internal  set
    var orange500 by mutableStateOf(orange500, structuralEqualityPolicy())
        internal  set
    var orange600 by mutableStateOf(orange600, structuralEqualityPolicy())
        internal  set
    var orange700 by mutableStateOf(orange700, structuralEqualityPolicy())
        internal  set
    var yellow50 by mutableStateOf(yellow50, structuralEqualityPolicy())
        internal  set
    var yellow100 by mutableStateOf(yellow100, structuralEqualityPolicy())
        internal  set
    var yellow200 by mutableStateOf(yellow200, structuralEqualityPolicy())
        internal  set
    var yellow300 by mutableStateOf(yellow300, structuralEqualityPolicy())
        internal  set
    var yellow400 by mutableStateOf(yellow400, structuralEqualityPolicy())
        internal  set
    var yellow500 by mutableStateOf(yellow500, structuralEqualityPolicy())
        internal  set
    var yellow600 by mutableStateOf(yellow600, structuralEqualityPolicy())
        internal  set
    var yellow700 by mutableStateOf(yellow700, structuralEqualityPolicy())
        internal  set
    var green50 by mutableStateOf(green50, structuralEqualityPolicy())
        internal  set
    var green100 by mutableStateOf(green100, structuralEqualityPolicy())
        internal  set
    var green200 by mutableStateOf(green200, structuralEqualityPolicy())
        internal  set
    var green300 by mutableStateOf(green300, structuralEqualityPolicy())
        internal  set
    var green400 by mutableStateOf(green400, structuralEqualityPolicy())
        internal  set
    var green500 by mutableStateOf(green500, structuralEqualityPolicy())
        internal  set
    var green600 by mutableStateOf(green600, structuralEqualityPolicy())
        internal  set
    var green700 by mutableStateOf(green700, structuralEqualityPolicy())
        internal  set
    var blue50 by mutableStateOf(blue50, structuralEqualityPolicy())
        internal  set
    var blue100 by mutableStateOf(blue100, structuralEqualityPolicy())
        internal  set
    var blue200 by mutableStateOf(blue200, structuralEqualityPolicy())
        internal  set
    var blue300 by mutableStateOf(blue300, structuralEqualityPolicy())
        internal  set
    var blue400 by mutableStateOf(blue400, structuralEqualityPolicy())
        internal  set
    var blue500 by mutableStateOf(blue500, structuralEqualityPolicy())
        internal  set
    var blue600 by mutableStateOf(blue600, structuralEqualityPolicy())
        internal  set
    var blue700 by mutableStateOf(blue700, structuralEqualityPolicy())
        internal  set
    var purple50 by mutableStateOf(purple50, structuralEqualityPolicy())
        internal  set
    var purple100 by mutableStateOf(purple100, structuralEqualityPolicy())
        internal  set
    var purple200 by mutableStateOf(purple200, structuralEqualityPolicy())
        internal  set
    var purple300 by mutableStateOf(purple300, structuralEqualityPolicy())
        internal  set
    var purple400 by mutableStateOf(purple400, structuralEqualityPolicy())
        internal  set
    var purple500 by mutableStateOf(purple500, structuralEqualityPolicy())
        internal  set
    var purple600 by mutableStateOf(purple600, structuralEqualityPolicy())
        internal  set
    var purple700 by mutableStateOf(purple700, structuralEqualityPolicy())
        internal  set
    var magenta50 by mutableStateOf(magenta50, structuralEqualityPolicy())
        internal  set
    var magenta100 by mutableStateOf(magenta100, structuralEqualityPolicy())
        internal  set
    var magenta200 by mutableStateOf(magenta200, structuralEqualityPolicy())
        internal  set
    var magenta300 by mutableStateOf(magenta300, structuralEqualityPolicy())
        internal  set
    var magenta400 by mutableStateOf(magenta400, structuralEqualityPolicy())
        internal  set
    var magenta500 by mutableStateOf(magenta500, structuralEqualityPolicy())
        internal  set
    var magenta600 by mutableStateOf(magenta600, structuralEqualityPolicy())
        internal  set
    var magenta700 by mutableStateOf(magenta700, structuralEqualityPolicy())
        internal  set
    var transparent by mutableStateOf(transparent, structuralEqualityPolicy())
        internal  set

    fun copy(
        white: Color = this.white,
        black: Color = this.black,
        gray50: Color = this.gray50,
        gray100: Color = this.gray100,
        gray200: Color = this.gray200,
        gray300: Color = this.gray300,
        gray400: Color = this.gray400,
        gray500: Color = this.gray500,
        gray600: Color = this.gray600,
        gray700: Color = this.gray700,
        gray800: Color = this.gray800,
        gray900: Color = this.gray900,
        red50: Color = this.red50,
        red100: Color = this.red100,
        red200: Color = this.red200,
        red300: Color = this.red300,
        red400: Color = this.red400,
        red500: Color = this.red500,
        red600: Color = this.red600,
        red700: Color = this.red700,
        orange50: Color = this.orange50,
        orange100: Color = this.orange100,
        orange200: Color = this.orange200,
        orange300: Color = this.orange300,
        orange400: Color = this.orange400,
        orange500: Color = this.orange500,
        orange600: Color = this.orange600,
        orange700: Color = this.orange700,
        yellow50: Color = this.yellow50,
        yellow100: Color = this.yellow100,
        yellow200: Color = this.yellow200,
        yellow300: Color = this.yellow300,
        yellow400: Color = this.yellow400,
        yellow500: Color = this.yellow500,
        yellow600: Color = this.yellow600,
        yellow700: Color = this.yellow700,
        green50: Color = this.green50,
        green100: Color = this.green100,
        green200: Color = this.green200,
        green300: Color = this.green300,
        green400: Color = this.green400,
        green500: Color = this.green500,
        green600: Color = this.green600,
        green700: Color = this.green700,
        blue50: Color = this.blue50,
        blue100: Color = this.blue100,
        blue200: Color = this.blue200,
        blue300: Color = this.blue300,
        blue400: Color = this.blue400,
        blue500: Color = this.blue500,
        blue600: Color = this.blue600,
        blue700: Color = this.blue700,
        purple50: Color = this.purple50,
        purple100: Color = this.purple100,
        purple200: Color = this.purple200,
        purple300: Color = this.purple300,
        purple400: Color = this.purple400,
        purple500: Color = this.purple500,
        purple600: Color = this.purple600,
        purple700: Color = this.purple700,
        magenta50: Color = this.magenta50,
        magenta100: Color = this.magenta100,
        magenta200: Color = this.magenta200,
        magenta300: Color = this.magenta300,
        magenta400: Color = this.magenta400,
        magenta500: Color = this.magenta500,
        magenta600: Color = this.magenta600,
        magenta700: Color = this.magenta700,
        transparent: Color = this.transparent
    ): JdsColors = JdsColors(
        white,
        black,
        gray50,
        gray100,
        gray200,
        gray300,
        gray400,
        gray500,
        gray600,
        gray700,
        gray800,
        gray900,
        red50,
        red100,
        red200,
        red300,
        red400,
        red500,
        red600,
        red700,
        orange50,
        orange100,
        orange200,
        orange300,
        orange400,
        orange500,
        orange600,
        orange700,
        yellow50,
        yellow100,
        yellow200,
        yellow300,
        yellow400,
        yellow500,
        yellow600,
        yellow700,
        green50,
        green100,
        green200,
        green300,
        green400,
        green500,
        green600,
        green700,
        blue50,
        blue100,
        blue200,
        blue300,
        blue400,
        blue500,
        blue600,
        blue700,
        purple50,
        purple100,
        purple200,
        purple300,
        purple400,
        purple500,
        purple600,
        purple700,
        magenta50,
        magenta100,
        magenta200,
        magenta300,
        magenta400,
        magenta500,
        magenta600,
        magenta700,
        transparent
    )



}
fun lightHdsColors(
    white: Color = Color(0xFFFFFFFF),
    black: Color = Color(0xFF000000),
    gray50: Color = Color(0xFFF6F6F6),
    gray100: Color = Color(0xFFEEEEEE),
    gray200: Color = Color(0xFFE2E2E2),
    gray300: Color = Color(0xFFCBCBCB),
    gray400: Color = Color(0xFFAFAFAF),
    gray500: Color = Color(0xFF6B6B6B),
    gray600: Color = Color(0xFF545454),
    gray700: Color = Color(0xFF333333),
    gray800: Color = Color(0xFF1F1F1F),
    gray900: Color = Color(0xFF141414),
    red50: Color = Color(0xFFFFEFED),
    red100: Color = Color(0xFFFED7D2),
    red200: Color = Color(0xFFF1998E),
    red300: Color = Color(0xFFE85C4A),
    red400: Color = Color(0xFFE11900),
    red500: Color = Color(0xFFAB1300),
    red600: Color = Color(0xFF870F00),
    red700: Color = Color(0xFF5A0A00),
    orange50: Color = Color(0xFFFFF3EF),
    orange100: Color = Color(0xFFFFE1D6),
    orange200: Color = Color(0xFFFABDA5),
    orange300: Color = Color(0xFFFA9269),
    orange400: Color = Color(0xFFFF6937),
    orange500: Color = Color(0xFFC14F29),
    orange600: Color = Color(0xFF9A3F21),
    orange700: Color = Color(0xFF672A16),
    yellow50: Color = Color(0xFFFFFAF0),
    yellow100: Color = Color(0xFFFFF2D9),
    yellow200: Color = Color(0xFFFFE3AC),
    yellow300: Color = Color(0xFFFFCF70),
    yellow400: Color = Color(0xFFFFC043),
    yellow500: Color = Color(0xFFBC8B2C),
    yellow600: Color = Color(0xFF996F00),
    yellow700: Color = Color(0xFF674D1B),
    green50: Color = Color(0xFFE6F2ED),
    green100: Color = Color(0xFFCEEBDF),
    green200: Color = Color(0xFF66D19E),
    green300: Color = Color(0xFF06C167),
    green400: Color = Color(0xFF048848),
    green500: Color = Color(0xFF03703C),
    green600: Color = Color(0xFF03582F),
    green700: Color = Color(0xFF10462D),
    blue50: Color = Color(0xFFEFF3FE),
    blue100: Color = Color(0xFFD4E2FC),
    blue200: Color = Color(0xFFA0BFF8),
    blue300: Color = Color(0xFF5B91F5),
    blue400: Color = Color(0xFF276EF1),
    blue500: Color = Color(0xFF1E54B7),
    blue600: Color = Color(0xFF174291),
    blue700: Color = Color(0xFF102C60),
    purple50: Color = Color(0xFFF4F1FA),
    purple100: Color = Color(0xFFE3DDF2),
    purple200: Color = Color(0xFFC1B5E3),
    purple300: Color = Color(0xFF957FCE),
    purple400: Color = Color(0xFF7356BF),
    purple500: Color = Color(0xFF574191),
    purple600: Color = Color(0xFF453473),
    purple700: Color = Color(0xFF2E224C),
    magenta50: Color = Color(0xFFF9F1F7),
    magenta100: Color = Color(0xFFF2DDEB),
    magenta200: Color = Color(0xFFE2B4D3),
    magenta300: Color = Color(0xFFCE7EB3),
    magenta400: Color = Color(0xFFBF569C),
    magenta500: Color = Color(0xFF914176),
    magenta600: Color = Color(0xFF72335D),
    magenta700: Color = Color(0xFF4C223E),
    transparent: Color =  Color(0x00000000)
): JdsColors = JdsColors(
    white,
    black,
    gray50,
    gray100,
    gray200,
    gray300,
    gray400,
    gray500,
    gray600,
    gray700,
    gray800,
    gray900,
    red50,
    red100,
    red200,
    red300,
    red400,
    red500,
    red600,
    red700,
    orange50,
    orange100,
    orange200,
    orange300,
    orange400,
    orange500,
    orange600,
    orange700,
    yellow50,
    yellow100,
    yellow200,
    yellow300,
    yellow400,
    yellow500,
    yellow600,
    yellow700,
    green50,
    green100,
    green200,
    green300,
    green400,
    green500,
    green600,
    green700,
    blue50,
    blue100,
    blue200,
    blue300,
    blue400,
    blue500,
    blue600,
    blue700,
    purple50,
    purple100,
    purple200,
    purple300,
    purple400,
    purple500,
    purple600,
    purple700,
    magenta50,
    magenta100,
    magenta200,
    magenta300,
    magenta400,
    magenta500,
    magenta600,
    magenta700,
    transparent
)
fun darkHdsColors() = lightHdsColors()


internal val LocalColors = staticCompositionLocalOf { lightHdsColors() }

internal val LocalContentColor = compositionLocalOf { Color.Black }

internal val LocalContentAlpha = compositionLocalOf { 1f }