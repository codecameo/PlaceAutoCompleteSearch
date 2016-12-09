package cameo.code.placeautocomplete;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by Md. Sifat-Ul Haque on 12/9/2016.
 */
public class PlaceAutoCompleteSearchBarFragment extends BasePlaceAutoCompleteFragment implements View.OnClickListener, View.OnTouchListener {

    private ImageView mIvBack, mIvClear;
    private RelativeLayout mSearchBox;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_box, container, false);

        initializeView(view);

        //setDefaultView();

        initListeners();

        return view;
    }

    @Override
    protected void initListeners() {
        super.initListeners();
        mIvBack.setOnClickListener(this);
        mIvClear.setOnClickListener(this);
        mSearchBox.setOnClickListener(this);
        mEtSearchText.setOnTouchListener(this);
    }

    private void initializeView(View view) {
        mIvBack = (ImageView) view.findViewById(R.id.iv_back);
        mIvClear = (ImageView) view.findViewById(R.id.iv_clear_search_box);
        mEtSearchText = (EditText) view.findViewById(R.id.et_search_text);
        mRvPlaceList = (RecyclerView) view.findViewById(R.id.rv_place_list);
        mSearchBox = (RelativeLayout) view.findViewById(R.id.rl_search_box);
    }

    @Override
    public void onTextChanged(CharSequence query, int start, int before, int count) {
        super.onTextChanged(query, start, before, count);

        if (mQueryText.length() > 0) {
            mIvClear.setVisibility(View.VISIBLE);
        } else {
            mIvClear.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        Log.d("Click","Clicked");

        if (id == R.id.iv_back) {
            resetSearchBox();
            performBackAction();
        }
        else if(id == R.id.iv_clear_search_box){
            resetSearchBox();
        }
        else if (id == R.id.rl_search_box){
            //enableSearchField();
        }
    }

    private void enableSearchField() {
        mIvBack.setImageResource(R.drawable.ic_arrow_back);
    }

    private void resetSearchBox() {
        mEtSearchText.setText("");
    }

    private void performBackAction() {
        mIvBack.setImageResource(R.drawable.ic_search);
        mEtSearchText.clearFocus();
        Utils.hideKeyboardFromDialog(getActivity(),mEtSearchText);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Log.d("Click","Touched");

        if (event.getAction() == MotionEvent.ACTION_UP){
            if (!mEtSearchText.isFocused())
                enableSearchField();
        }
        return false;
    }
}
