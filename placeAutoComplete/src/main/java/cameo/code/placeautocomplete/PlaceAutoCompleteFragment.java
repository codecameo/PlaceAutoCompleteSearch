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
public class PlaceAutoCompleteFragment extends BasePlaceAutoCompleteFragment implements
        View.OnClickListener,
        TextWatcher,
        AutoCompletePlaceAdapter.onPlaceSelectedListener {

    private ImageView mIvBack, mIvSearch;
    private TextView mTvTitle;
    private boolean isShowingSearchField;
    private Animation mSlideIn;

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

    @Override
    public void onTextChanged(CharSequence query, int start, int before, int count) {
        super.onTextChanged(query,start,before,count);

        if (mQueryText.length() > 0) {
            mIvSearch.setVisibility(View.VISIBLE);
        }else if (isShowingSearchField){
            mIvSearch.setVisibility(View.INVISIBLE);
        }
    }
}
