<!-- pam:product-page:start -->
<div align="center">

# PAM Native Maps

**Declarative maps backed by real platform map views.**

Control cameras, markers, overlays, and interactions through bounded PHP state while panning and rendering remain native.

[![Latest version](https://img.shields.io/packagist/v/pushinbr/pam-native-maps?style=flat-square&label=stable)](https://packagist.org/packages/pushinbr/pam-native-maps)
[![CI](https://img.shields.io/github/actions/workflow/status/push-in/pam-native-maps/ci.yml?branch=main&style=flat-square&label=CI)](https://github.com/push-in/pam-native-maps/actions)
![PHP](https://img.shields.io/badge/PHP-8.5-777BB4?style=flat-square&logo=php&logoColor=white)
![Android](https://img.shields.io/badge/Android-API%2026%2B-3DDC84?style=flat-square&logo=android&logoColor=white)
![iOS](https://img.shields.io/badge/iOS-15%2B-000000?style=flat-square&logo=apple&logoColor=white)

**[Documentation](https://push-in.github.io/pam-docs/native/overview/) · [Quick start](#quick-start) · [What you can build](#what-you-can-build) · [PAM ecosystem](https://push-in.github.io/pam-docs/ecosystem/) · [Issues](https://github.com/push-in/pam-native-maps/issues)**

</div>

---

## Why PAM Native Maps

Control cameras, markers, overlays, and interactions through bounded PHP state while panning and rendering remain native. The public API is strictly typed for PHP 8.5; expensive or frame-sensitive work stays in Rust or the platform SDK instead of crossing the application boundary every frame.

| | |
| --- | --- |
| **Best for** | A focused capability you can add to any PAM Native application |
| **Native path** | Google Maps · MapKit |
| **Application model** | Composer package + generated native integration |
| **Design rule** | Independent module; no feed, vertical, or application template bundled |

## What you can build

- Store locators and delivery tracking
- Travel, mobility, and property discovery
- Geospatial dashboards with interactive overlays

## Quick start

Already have a PAM Native project? Add only this capability:

```bash
pam composer require pushinbr/pam-native-maps
pam doctor --fix
```

New to PAM? Follow the **[five-minute PAM Native setup](https://push-in.github.io/pam-docs/native/overview/)** once, then return here. Your application stays a normal Composer project with a committed lockfile.
<!-- pam:product-page:end -->

## See it in action

Native Google Maps on Android and MapKit on iOS behind one immutable PHP API. It supports camera control, map styles, up to 5,000 markers, user location, gestures, and typed map, marker, camera, and error events.

```bash
pam add maps
pam doctor
```

```php
use Pam\Native\Maps\{Coordinate, MapMarker, MapView};

MapView::make(new Coordinate(-23.5505, -46.6333), 14)
    ->markers([new MapMarker('office', new Coordinate(-23.5505, -46.6333), 'Office')])
    ->showUserLocation();
```

Android apps must provide `com.google.android.geo.API_KEY` in their application manifest. Request location permission at runtime before enabling user location. The package never stores or transmits API keys.

## What installation does

`pam add maps` resolves the official compatible package, performs a non-mutating Composer preflight, updates the normal `composer.json` and `composer.lock`, refreshes generated native integration when required, and leaves the project ready for `pam doctor` validation.

Use `pam packages` to inspect availability and `pam remove maps` to uninstall the capability safely. Direct Composer commands are an advanced interoperability path; PAM is the supported application workflow.

## API guide

| API | Responsibility |
| --- | --- |
| `MapView` | Render a declarative native map and receive typed events. |
| `Coordinate` | Represent validated latitude and longitude. |
| `MapMarker` | Describe stable marker identity and presentation. |
| `MapStyle` | Select the platform-normalized map style. |
| `MapEventKind` | Handle camera, marker, and error events. |

All coded states, kinds, and variants are sequential integer-backed enums. Use enum cases in application code; do not depend on raw wire numbers.

## Production checklist

- Keep marker identifiers stable so reconciliation can update instead of recreate.
- Request location permission before enabling user location.
- Restrict and rotate the Android Maps API key using application and API restrictions.
- Run `pam doctor`, `pam test`, and a signed release build on every supported platform.
- Exercise denial, cancellation, backgrounding, process restart, and offline behavior before release.

## Troubleshooting

- **Android map is blank:** verify the manifest API key, billing, and key restrictions.
- **User location is absent:** confirm runtime permission before enabling it.
- **Large marker updates stutter:** preserve IDs and batch state changes.
- **Native integration is stale:** run `pam doctor --fix`, rebuild the native host, and inspect the first reported diagnostic.

## Compatibility and support

This package targets PAM Native `0.8.x`, Android API 26+, and iOS 15+ unless a platform-specific section above states a stricter requirement. Platform SDKs, credentials, entitlements, physical hardware, and store configuration remain application responsibilities.

- [PAM documentation](https://push-in.github.io/pam-docs/introduction/)
- [PAM Native overview](https://push-in.github.io/pam-docs/native/overview/)
- [Plugin and native capability model](https://push-in.github.io/pam-docs/native/plugins/)
- [Report an issue](https://github.com/push-in/pam-native-maps/issues)

Security vulnerabilities should be reported through the repository security policy or GitHub private vulnerability reporting, not a public issue.
