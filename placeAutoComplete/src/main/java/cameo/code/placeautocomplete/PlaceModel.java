package cameo.code.placeautocomplete;

import java.io.Serializable;

/**
 * Created by Md. Sifat-Ul Haque on 12/2/2016.
 */

public class PlaceModel implements Serializable {
    String mMainPlace, mSecondaryPlace, mPlaceId, mReference, mPlaceFullName;
    double mLat, mLng;

    public String getMainPlace() {
        return mMainPlace;
    }

    public void setMainPlace(String mMainPlace) {
        this.mMainPlace = mMainPlace;
    }

    public String getSecondaryPlace() {
        return mSecondaryPlace;
    }

    public void setSecondaryPlace(String mSecondaryPlace) {
        this.mSecondaryPlace = mSecondaryPlace;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    public void setPlaceId(String mPlaceId) {
        this.mPlaceId = mPlaceId;
    }

    public String getReference() {
        return mReference;
    }

    public void setReference(String mReference) {
        this.mReference = mReference;
    }

    public String getPlaceFullName() {
        return mPlaceFullName;
    }

    public void setPlaceFullName(String mPlaceFullName) {
        this.mPlaceFullName = mPlaceFullName;
    }

    public double getLng() {
        return mLng;
    }

    public void setLng(double mLng) {
        this.mLng = mLng;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double mLat) {
        this.mLat = mLat;
    }
}
