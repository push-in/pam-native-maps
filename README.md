# PAM Native Maps

Native Google Maps on Android and MapKit on iOS behind one immutable PHP API. It supports camera control, map styles, up to 5,000 markers, user location, gestures, and typed map, marker, camera, and error events.

```bash
composer require pushinbr/pam-native-maps
pam mobile sync
```

```php
use Pam\Native\Maps\{Coordinate, MapMarker, MapView};

MapView::make(new Coordinate(-23.5505, -46.6333), 14)
    ->markers([new MapMarker('office', new Coordinate(-23.5505, -46.6333), 'Office')])
    ->showUserLocation();
```

Android apps must provide `com.google.android.geo.API_KEY` in their application manifest. Request location permission at runtime before enabling user location. The package never stores or transmits API keys.
