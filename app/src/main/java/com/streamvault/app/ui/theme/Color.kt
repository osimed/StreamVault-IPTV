package com.streamvault.app.ui.theme

import androidx.compose.ui.graphics.Color

// ── Brand Primary — Deep Indigo-Violet ────────────────────────────
val Primary          = Color(0xFF6C63FF)
val PrimaryLight     = Color(0xFF9D97FF)   // hover/pressed states
val PrimaryVariant   = PrimaryLight        // legacy alias
val PrimaryDark      = Color(0xFF4A42DB)
val PrimaryGlow      = Color(0x336C63FF)   // 20% opacity glow ring

// ── Backgrounds — Layered depth system ────────────────────────────
val BackgroundDeep   = Color(0xFF050508)   // Deepest — behind everything
val Background       = Color(0xFF0D0D12)   // Slight blue tint
val Surface          = Color(0xFF16161F)   // Card surfaces
val SurfaceElevated  = Color(0xFF1E1E2A)   // Modals, overlays, sidebar
val SurfaceHighlight = Color(0xFF282838)   // Selected/hovered
val SurfaceVariant   = SurfaceElevated

// ── Text — 4-tier hierarchy ───────────────────────────────────────
val TextPrimary      = Color(0xFFF0F0F5)   // Titles, high emphasis
val TextSecondary    = Color(0xFFB8B8C8)   // Descriptions, metadata
val TextTertiary     = Color(0xFF7A7A8E)   // Timestamps, counts
val TextDisabled     = Color(0xFF4A4A58)   // Disabled states

// Legacy aliases kept for compatibility
val OnBackground     = TextPrimary
val OnSurface        = TextSecondary
val OnSurfaceVariant = TextSecondary
val OnSurfaceDim     = TextTertiary

// ── Accents ────────────────────────────────────────────────────────
val AccentRed        = Color(0xFFFF4B6A)   // Live indicator, errors
val AccentGreen      = Color(0xFF2DD881)   // Success, online
val AccentAmber      = Color(0xFFFFA742)   // Warnings, badges
val AccentCyan       = Color(0xFF00D4FF)   // EPG progress, info

// Legacy aliases
val OnPrimary        = Color(0xFFFFFFFF)
val Secondary        = Color(0xFF03DAC6)
val ErrorColor       = AccentRed
val SuccessColor     = AccentGreen
val LiveIndicator    = AccentRed
val WarningColor     = AccentAmber

// ── Gradient Overlays ─────────────────────────────────────────────
val GradientOverlayTop    = Color(0xCC000005)   // 80% opacity
val GradientOverlayBottom = Color(0xE6000005)   // 90% opacity

// ── Card & Focus ──────────────────────────────────────────────────
val FocusBorder             = Primary
val CardBackground          = Surface
val ProgressBar             = Primary
val ProgressBarBackground   = Color(0xFF333344)
