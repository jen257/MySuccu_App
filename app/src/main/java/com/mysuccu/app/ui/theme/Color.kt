package com.mysuccu.app.ui.theme
import androidx.compose.ui.graphics.Color

// MySuccu 终极视觉色卡库 (Figma 级高定原色)

// --- 基础灰阶 (全局通用) ---
val BackgroundLight = Color(0xFFFFFFFF) // 企划书指定：背景色纯白 #FFFFFF
val SurfaceLight = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF1D1D1F)     // 苹果级深空灰
val TextSecondary = Color(0xFF86868B)   // 优雅的高级灰
val DividerGray = Color(0xFFE5E5EA)     // 极细分割线

// --- 💀 墓碑模式专属色 ---
val TombstoneGray = Color(0xFFD1D1D6)
val TombstoneText = Color(0xFF9E9E9E)

// =============================================================
// 核心 9 组高级定制主题色 (Pro 会员/个性化功能核心)
// =============================================================

// 第 1 组：深林秘境风
val Theme1Primary = Color(0xFF2E513B)
val Theme1Container = Color(0xFFFDDCC9)
val Theme1Accent1 = Color(0xFFF2E5AE)
val Theme1Accent2 = Color(0xFFB67148)
val Theme1Status1 = Color(0xFF7B9ED6)
val Theme1Status2 = Color(0xFF8A895B)

// 第 2 组：莫兰迪薄荷
val Theme2Primary = Color(0xFFC6D8DA)
val Theme2Container = Color(0xFF647E7D)
val Theme2Accent1 = Color(0xFFE9BA84)
val Theme2Accent2 = Color(0xFF688C8A)
val Theme2Status1 = Color(0xFFC2AE4D)
val Theme2Status2 = Color(0xFFFDEFE4)

// 第 3 组：暖阳奶油风
val Theme3Primary = Color(0xFFFFF2BE)
val Theme3Container = Color(0xFFFCC648)
val Theme3Accent1 = Color(0xFF91C6CC)
val Theme3Accent2 = Color(0xFF688C8A)
val Theme3Status1 = Color(0xFF694899)
val Theme3Status2 = Color(0xFFDCE8F4)

// 第 4 组：冰玉冷淡风
val Theme4Primary = Color(0xAAAAC9B9)
val Theme4Container = Color(0xFF305065)
val Theme4Accent1 = Color(0xFFE1EABB)
val Theme4Accent2 = Color(0xFFA7C1E2)
val Theme4Status1 = Color(0xFFB1AE77)
val Theme4Status2 = Color(0xFF5E929F)

// 第 5 组：甜心泡泡糖
val Theme5Primary = Color(0xFFC7CEFC)
val Theme5Container = Color(0xFFFFB6D5)
val Theme5Accent1 = Color(0xFFFFFFFF)
val Theme5Accent2 = Color(0xFFF9E8CE)
val Theme5Status1 = Color(0xFF3A6FD7)
val Theme5Status2 = Color(0xFFF356A7)

// 第 6 组：晚霞胭脂风
val Theme6Primary = Color(0xFFF27794)
val Theme6Container = Color(0xFFF7C8DC)
val Theme6Accent1 = Color(0xFFFBA37F)
val Theme6Accent2 = Color(0xFFC7CEFC)
val Theme6Status1 = Color(0xFF6A98F1)
val Theme6Status2 = Color(0xFFF9E8CE)

// 第 7 组：复古红陶风
val Theme7Primary = Color(0xFFE2CD8C)
val Theme7Container = Color(0xFFFDDCC9)
val Theme7Accent1 = Color(0xFFF7AC59)
val Theme7Accent2 = Color(0xFFD1A6AF)
val Theme7Status1 = Color(0xFFF27794)
val Theme7Status2 = Color(0xFFB77E49)

// 第 8 组：官方默认款 (Official Standard)
val ThemeOfficialPrimary = Color(0xFF1A3D59)   // 深海蓝
val ThemeOfficialContainer = Color(0xFFD2DC97) // 芽绿
val ThemeOfficialAccent1 = Color(0xFFFFFFFF)
val ThemeOfficialAccent2 = Color(0xFF688C8A)
val ThemeOfficialStatus1 = Color(0xFFF0B5BB)   // 状态色粉
val ThemeOfficialStatus2 = Color(0xFFEBD5EC)   // 状态色紫

// 第 9 组：极客深海风
val Theme9Primary = Color(0xFFDBE8F8)
val Theme9Container = Color(0xFF6192CD)
val Theme9Accent1 = Color(0xFFFEFBF2)
val Theme9Accent2 = Color(0xFF6192CD)
val Theme9Status1 = Color(0xFFF6C2AC)
val Theme9Status2 = Color(0xFF09274B)

// --- 功能性映射 (默认指向官方第 8 组) ---
val StatusHealthy = ThemeOfficialContainer
val StatusWarning = ThemeOfficialStatus2
val StatusDanger = ThemeOfficialStatus1