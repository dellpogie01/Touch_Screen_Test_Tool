# Touchscreen Test Tool - Android Application Plan

A native Android application built with Kotlin, Jetpack Compose, and Material 3 to inspect touch coverage, identify unresponsive screen areas, and test simultaneous multitouch input.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following user preferences have been confirmed during the interactive planning phase:
> - **Grid Density**: Medium grid density (balanced resolution for precise touch path tracking).
> - **Color Scheme**: Electric blue (`#00E5FF`) and lime green (`#00E676`) accent palette on a dark navy (`#0B0F19`) canvas.

---

## 1. Overview & Core Concept

- **What It Does**: Provides robust diagnostic tools for testing touch responsiveness across the display, tracking multi-finger simultaneous touch limits, and calculating grid coverage percentage.
- **Target Audience**: Android device owners, repair technicians, and developers testing hardware responsiveness.
- **Key Value**: Offline-first, lightweight, zero permissions required (no internet, no camera, no location), privacy-respecting, and mathematically rigorous touch event processing.

---

## 2. User Experience & Visual Design

- **Visual Identity**:
  - *Theme*: Premium dark utility aesthetic.
  - *Colors*: Dark navy background (`#0B0F19`), surface cards (`#151B2B`), electric blue primary accents (`#00E5FF`), and lime green active touch indicators (`#00E676`).
  - *Typography & Polish*: Clean sans-serif M3 typography with accessible sizing, rounded card shapes, and smooth spring animations.
- **Key User Flows**:
  1. **Splash Screen**: 3-second animated launch showing fingertip icon, app title, tagline, and subtle loading indicator before seamlessly transitioning to Home.
  2. **Home Dashboard**: Clean overview of app capabilities with cards for "Touch Coverage Test", "Multitouch Test", "How to Test", "Settings", and "About".
  3. **Touch Coverage Test**: Full-screen grid canvas where dragging fingers colors cells (Untested: dark gray, Touched: lime green, Active touch: electric blue). Displays live coverage percentage, touched cell count, elapsed time, and controls (Reset, density, trails toggle, grid toggle).
  4. **Multitouch Test**: Full-screen canvas tracking active pointers with colored circular markers, coordinates, simultaneous touch count, and peak observed touch count.
  5. **Settings**: Configurable preferences saved via DataStore (Theme mode, default grid density, touch trail visibility, coordinate display, keep screen awake).
  6. **How to Test / About**: Clear instructional guide detailing testing best practices, screen protector/moisture caveats, and explicit disclaimers that software tests cannot repair hardware or capture events unobserved by Android.

---

## 3. Key Product Decisions & Trade-Offs

- **Touch Event Capture**: 
  - *Chosen Approach*: Use Jetpack Compose pointer input APIs combined with custom Android View touch interop where needed to guarantee accurate multi-pointer tracking and historical sample interpolation.
  - *Why*: Ensures smooth paths without gap tearing and precise coordinate mapping across window insets.
- **State Management**:
  - *Chosen Approach*: ViewModel with `StateFlow` and Jetpack DataStore for local preferences.
  - *Why*: Preserves test progress across configuration changes (rotation) and maintains clean reactive state separation.

---

## 4. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────┐
│                       MainActivity                          │
└──────────────┬──────────────────────────────┬───────────────┘
               │                              │
               ▼                              ▼
┌──────────────────────────────┐    ┌─────────────────────────┐
│     Navigation Compose       │    │   MainViewModel & State │
└──────────────┬───────────────┘    └──────────┬──────────────┘
               │                               │
       ┌───────┴────────┬──────────────┬───────┴────────┐
       ▼                ▼              ▼                ▼
┌──────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│ Splash Screen│ │ Home Screen │ │Coverage Test│ │Multitouch   │
└──────────────┘ └─────────────┘ └─────────────┘ └─────────────┘
                                                       │
                                                       ▼
                                                ┌─────────────┐
                                                │ DataStore   │
                                                └─────────────┘
```

- **Data Models**:
  - `GridCellState`: Tracks Untested, Touched, and Active states for each cell in the coverage grid.
  - `PointerInfo`: Tracks active touch pointers with stable ID, X/Y coordinates, and color assignment.
  - `AppSettings`: DataStore backed user preferences.
- **Edge-to-Edge & Insets**: Implement full-screen immersive mode during active tests while respecting cutout and navigation system bar bounds.
