# PlaceAutoCompleteSearch

A library for android to show auto place suggestion for the searched text. Support for Android 4.0.3 (API 15) and up.

# Implementation
Only 4 steps are required to implement this into your project.

- Include the PlaceAutoCompleteFragment in your xml file.
- Resigter FetchPlacePredictionService service in your menifest file.
- Implement onPlaceSelectedListener Interface.
- Return the api key through getWebApiKey() CallBack method.

# Step 1 :
```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:map="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <fragment xmlns:tools="http://schemas.android.com/tools"
        android:id="@+id/map"
        android:name="com.google.android.gms.maps.SupportMapFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context="code.cameo.placeautocomplete.MapsActivity" />
    <fragment
        android:id="@+id/place_autocomplete_fragment"
        android:name="cameo.code.placeautocomplete.PlaceAutoCompleteFragment"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        tools:layout="@layout/fragment_search_toolbar" />
    
</RelativeLayout>
```

# Step 2 :
```xml
<service android:name="cameo.code.placeautocomplete.FetchPlacePredictionService"/>
```

# Step 3: 
```java
public class MapsActivity extends FragmentActivity implements
        PlaceAutoCompleteFragment.onPlaceSelectedListener {
    @Override
    public void onPlaceSelected(PlaceModel placeModel) { }
    @Override
    public String getWebApiKey() {}
}
```

# Step 4 :
```java
public String getWebApiKey() {
return getString(R.string.google_maps_browser_key);
}
```
This map API key is not the same API key for android application. This is an unrestricted key. You get your API key from [Here](https://console.developers.google.com/apis/credentials)

# Demo :
<img src="screenshots/sample.gif" width="50%">

#### Gradle

Step 1. Add the JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
```

Step 2. Add the dependency
```groovy
dependencies {
	compile 'com.github.codecameo:PlaceAutoCompleteSearch:1.0'
}
```

