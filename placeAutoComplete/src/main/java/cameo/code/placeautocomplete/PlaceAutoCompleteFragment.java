package cameo.code.placeautocomplete;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Md. Sifat-Ul Haque on 12/1/2016.
 */
public class PlaceAutoCompleteFragment extends Fragment implements
        View.OnClickListener,
        TextWatcher,
        AutoCompletePlaceAdapter.onPlaceSelectedListener {

    private ImageView mIvBack, mIvSearch;
    private TextView mTvTitle;
    private EditText mEtSearchText;
    private boolean isShowingSearchField;
    private Animation mSlideIn;
    private Animation mSlideUp;

    public static final String TAG_API_KEY = "ApiKey";
    private AutoCompletePlaceReceiver mAutoCompletePlaceReceiver;
    private ArrayList<PlaceModel> mPlaces;
    private RecyclerView mRvPlaceList;
    private AutoCompletePlaceAdapter mPlaceAdapter;
    private String mQueryText;
    private onPlaceSelectedListener mOnPlaceSelectedListener;
    private PlaceModel mSelectedPlaceModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_box, container, false);

        initializeView(view);

        initListeners();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSlideIn = AnimationUtils.loadAnimation(getActivity(), R.anim.transition_slide_in);
        mSlideIn.setDuration(100);
        mSlideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.transition_slide_up);
    }

    private void initListeners() {
        mIvBack.setOnClickListener(this);
        mIvSearch.setOnClickListener(this);
        mEtSearchText.addTextChangedListener(this);

        mSlideIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mTvTitle.setVisibility(View.GONE);
                mEtSearchText.setVisibility(View.VISIBLE);
                mEtSearchText.requestFocus();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isShowingSearchField = true;
                mIvSearch.setImageResource(R.drawable.ic_cross);
                mIvSearch.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mSlideUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
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

    private void initializeView(View view) {
        mIvBack = (ImageView) view.findViewById(R.id.iv_back);
        mIvSearch = (ImageView) view.findViewById(R.id.iv_search_action);
        mEtSearchText = (EditText) view.findViewById(R.id.et_search_text);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mRvPlaceList = (RecyclerView) view.findViewById(R.id.rv_place_list);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.iv_back) {
            backActionPerform();
        } else if (id == R.id.iv_search_action) {
            toggleSearchInputField();
            showSearchInputField();
        }


    }

    private void backActionPerform() {
        if (isShowingSearchField) {
            isShowingSearchField = false;
            mIvSearch.setImageResource(R.drawable.ic_search);
            mTvTitle.setVisibility(View.VISIBLE);
            mEtSearchText.setVisibility(View.GONE);
            mEtSearchText.setText("");
            mIvSearch.setVisibility(View.VISIBLE);
            Utils.hideKeyboardFromDialog(getActivity(), mEtSearchText);
        } else {
            getActivity().onBackPressed();
        }
    }

    private void showSearchInputField() {
        mEtSearchText.clearAnimation();

        if (isShowingSearchField) {
            mEtSearchText.setText("");
        } else {
            mEtSearchText.startAnimation(mSlideIn);
        }
    }

    private void toggleSearchInputField() {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence query, int start, int before, int count) {
        String searchText = query.toString();

        if (searchText.length() > 0) {
            mIvSearch.setVisibility(View.VISIBLE);
        }else if (isShowingSearchField){
            mIvSearch.setVisibility(View.INVISIBLE);
        }

        searchPlace(searchText);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void searchPlace(String query) {

        mQueryText = query;

        if (TextUtils.isEmpty(mQueryText)) {
            hideList();
        } else {
            fetchAutoCompletePlaces();
        }
    }

    private void hideList() {
        mRvPlaceList.clearAnimation();
        mRvPlaceList.setVisibility(View.GONE);
    }

    @Override
    public void onPlaceSelected(PlaceModel placeModel) {
        mSelectedPlaceModel = placeModel;
        updateSearchText(placeModel);
        hideList();
        fetchPlaceDetail(placeModel);
    }

    private void fetchAutoCompletePlaces() {
        FetchPlacePredictionService.startAutoCompletePlaces(getActivity(), mQueryText, mOnPlaceSelectedListener.getWebApiKey(), mAutoCompletePlaceReceiver);
    }

    private void fetchPlaceDetail(PlaceModel placeModel) {
        FetchPlacePredictionService.startFetchPlaceDetail(getActivity(), placeModel.getPlaceId(), mOnPlaceSelectedListener.getWebApiKey(), mAutoCompletePlaceReceiver);
    }

    private void updateSearchText(PlaceModel placeModel) {
        mEtSearchText.removeTextChangedListener(this);
        mEtSearchText.setText(placeModel.getMainPlace());
        mEtSearchText.addTextChangedListener(this);
        Utils.hideKeyboardFromDialog(getActivity(), mEtSearchText);
        mEtSearchText.setSelection(placeModel.getMainPlace().length());
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
