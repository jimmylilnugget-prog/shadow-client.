# Shadow Client

A modular utility client for **Minecraft Java Edition 1.21.11** built on **Fabric**.
Original identity and UI (inspired by the layout style of ClickGUI-based clients, no third-party assets or code).

- Draggable, collapsible ClickGUI windows (one per category) with animated hover / toggle / expand
- HUD editor with drag, edge/centre snapping and per-element toggles
- Themes: **Dark**, **Red**, **Purple** (colours cross-fade when switched)
- JSON config (modules, keybinds, settings, theme, HUD layout, window layout)
- 18 modules across 6 categories, 7 HUD elements

> **Fair play:** several modules (KillAura, Fly, Speed, ...) are cheats. Use them in singleplayer,
> on your own server, or on servers that explicitly allow them. Most public servers will ban you.

## Requirements

| Tool | Version |
|------|---------|
| JDK | 21 (Temurin / Microsoft / Adoptium) |
| Minecraft | 1.21.11 |
| Fabric Loader | >= 0.18.1 |
| Fabric API | 0.141.x for 1.21.11 |
| Gradle | 9.x (wrapper config included) |

## Build

```bash
# One-time: generate the wrapper scripts + jar (the jar is a binary, so it isn't shipped here)
gradle wrapper --gradle-version 9.2.0       # or copy gradlew*, gradle/wrapper/gradle-wrapper.jar
                                            # from the official Fabric example mod

./gradlew build          # Windows: gradlew.bat build
```

The jar is written to `build/libs/shadow-client-1.0.0.jar`.

## Run from source (dev environment)

```bash
./gradlew runClient
```

Or import the folder into IntelliJ IDEA (File > Open > `build.gradle`) and run the generated
**Minecraft Client** configuration.

## Install in the launcher

1. Install Fabric Loader for 1.21.11 (https://fabricmc.net/use/installer/).
2. Put **fabric-api-*.jar** and **shadow-client-1.0.0.jar** in `.minecraft/mods/`.
3. Launch the Fabric 1.21.11 profile.

## Controls

| Action | Input |
|--------|-------|
| Open / close ClickGUI | **Right Shift** (rebindable: ClickGUI module in *Misc*) |
| Toggle module | Left click |
| Show settings | Right click a module |
| Bind key | Middle click a module, press a key (Backspace/Delete clears, Esc cancels) |
| Collapse window | Right click its header |
| Move window | Drag its header |
| Change theme | Bottom bar: Dark / Red / Purple |
| HUD editor | Bottom bar: **HUD Editor** (drag elements, right click to toggle) |
| Save / Load config | Bottom bar (also auto-saves on closing the GUI and on exit) |

Config file: `.minecraft/config/shadowclient/default.json`

## Modules

| Category | Modules |
|----------|---------|
| Combat | KillAura, TriggerBot, AutoTotem |
| Movement | Sprint, Fly, Speed, Step, AutoWalk |
| Player | AutoRespawn, AutoTool, Sneak |
| Render | Fullbright, ESP, HUD |
| World | AutoMine, AutoUse |
| Misc | ClickGUI, Panic, AntiAFK, Notifications |

HUD elements: Watermark, FPS, CPS, Coordinates, Keystrokes, Armor, Target Info.

## Project layout

```
com.shadowclient
 ├─ ShadowClient            entrypoint (events, HUD registration)
 ├─ core/                   ModuleManager, CpsTracker, TargetTracker
 ├─ module/                 Module, Category
 │   ├─ setting/            Boolean / Number / Mode settings
 │   └─ impl/<category>/    concrete modules
 ├─ hud/                    HudManager, HudWidget
 │   └─ widgets/            Watermark, FPS, CPS, Coords, Keystrokes, Armor, TargetInfo
 ├─ gui/                    ClickGuiScreen, HudEditorScreen
 │   └─ components/         CategoryWindow, ModuleButton, SettingRow, BarButton, WindowState
 ├─ theme/                  Theme, ThemeManager
 ├─ config/                 ConfigManager (Gson)
 └─ util/                   Animation, ColorUtil, RenderUtil, KeyNames
```

## Adding a module

```java
public final class MyModule extends Module {
    private final NumberSetting amount = add(new NumberSetting("Amount", 1, 0, 10, 1));

    public MyModule() { super("MyModule", "Does a thing.", Category.MISC); }

    @Override public void onTick() { /* runs every tick while enabled */ }
}
```
Then add `register(new MyModule());` in `ModuleManager`. The GUI, keybinds and config pick it up automatically.

## Troubleshooting

* **Dependency not found** - Yarn builds / Fabric API versions move; copy the current 1.21.11 values from
  https://fabricmc.net/develop/ into `gradle.properties`.
* **Compile error on `Click`, `KeyInput`, `DrawContext` or a Yarn name** - 1.21.9-1.21.11 reshuffled input and
  GUI classes; the usages are isolated to `gui/` and `util/RenderUtil`, so fixes are one-liners.
* **Stale caches** - delete `.gradle/` and `build/`, then re-import.
