package cameo.code.placeautocomplete;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Md. Sifat-Ul Haque on 12/8/2016.
 */

public class BasePlaceAutoCompleteFragment extends Fragment implements AutoCompletePlaceAdapter.onPlaceSelectedListener, TextWatcher {

    protected onPlaceSelectedListener mOnPlaceSelectedListener;
    public static final String TAG_API_KEY = "ApiKey";
    protected AutoCompletePlaceReceiver mAutoCompletePlaceReceiver;
    protected ArrayList<PlaceModel> mPlaces;
    protected RecyclerView mRvPlaceList;
    protected AutoCompletePlaceAdapter mPlaceAdapter;
    protected String mQueryText;
    protected PlaceModel mSelectedPlaceModel;

    private Animation mSlideUp;

    protected EditText mEtSearchText;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSlideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.transition_slide_up);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAutoCompletePlaceReceiver = new AutoCompletePlaceReceiver(new Handler());
        setAdapter();
    }

    private void setAdapter() {
        mPlaceAdapter = new AutoCompletePlaceAdapter();
        mPlaceAdapter.setOnPlaceSelectedListener(this);
        mRvPlaceList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvPlaceList.setAdapter(mPlaceAdapter);
    }

    @Override
    public void onPlaceSelected(PlaceModel placeModel) {
        mSelectedPlaceModel = placeModel;
        updateSearchText(placeModel);
        hideList();
        fetchPlaceDetail(placeModel);
    }

    protected void hideList() {
        mRvPlaceList.clearAnimation();
        mRvPlaceList.setVisibility(View.GONE);
    }

    private void updateSearchText(PlaceModel placeModel) {
        mEtSearchText.removeTextChangedListener(this);
        mEtSearchText.setText(placeModel.getMainPlace());
        mEtSearchText.addTextChangedListener(this);
        Utils.hideKeyboardFromDialog(getActivity(), mEtSearchText);
        mEtSearchText.setSelection(placeModel.getMainPlace().length());
    }

    protected void fetchAutoCompletePlaces() {
        FetchPlacePredictionService.startAutoCompletePlaces(getActivity(), mQueryText, mOnPlaceSelectedListener.getWebApiKey(), mAutoCompletePlaceReceiver);
    }

    private void fetchPlaceDetail(PlaceModel placeModel) {
        FetchPlacePredictionService.startFetchPlaceDetail(getActivity(), placeModel.getPlaceId(), mOnPlaceSelectedListener.getWebApiKey(), mAutoCompletePlaceReceiver);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence query, int start, int before, int count) {
        String searchText = query.toString();
        searchPlace(searchText);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    protected void searchPlace(String query) {

        mQueryText = query;

        if (TextUtils.isEmpty(mQueryText)) {
            hideList();
        } else {
            fetchAutoCompletePlaces();
        }
    }


    /*****
     * Get Selected address from service
     ****/
    class AutoCompletePlaceReceiver extends ResultReceiver {

        AutoCompletePlaceReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultData.getString(FetchPlacePredictionService.KEY_ACTION).equals(FetchPlacePredictionService.ACTION_FETCH_AUTO_COMPLETE_PLACE)) {

                if (resultCode == FetchPlacePredictionService.SUCCESS_RESULT) {
                    updateAutoCompleteList(resultData);
                } else {
                    mPlaces = new ArrayList<>();
                    mPlaceAdapter.updatePlaceInfo(mPlaces);
                }
            } else {
                if (resultCode == FetchPlacePredictionService.SUCCESS_RESULT) {
                    mSelectedPlaceModel.setLat(resultData.getDouble(FetchPlacePredictionService.KEY_LAT));
                    mSelectedPlaceModel.setLng(resultData.getDouble(FetchPlacePredictionService.KEY_LNG));
                    mOnPlaceSelectedListener.onPlaceSelected(mSelectedPlaceModel);
                } else {
                    Toast.makeText(getActivity(), "Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void updateAutoCompleteList(Bundle resultData) {
        showPlaceList();
        mPlaces = (ArrayList<PlaceModel>) resultData.getSerializable(FetchPlacePredictionService.KEY_AUTO_COMPLETE_PLACES);
        mPlaceAdapter.updatePlaceInfo(mPlaces);
    }

    private void showPlaceList() {
        if (mRvPlaceList.getVisibility() == View.GONE && !TextUtils.isEmpty(mQueryText)) {
            mRvPlaceList.setVisibility(View.VISIBLE);
            mRvPlaceList.clearAnimation();
            mRvPlaceList.startAnimation(mSlideUp);
        }
    }


    public void setOnPlaceSelectedListener(onPlaceSelectedListener placeSelectedListener) {
        mOnPlaceSelectedListener = placeSelectedListener;
    }

    public interface onPlaceSelectedListener {
        void onPlaceSelected(PlaceModel placeModel);
        String getWebApiKey();
    }
}
