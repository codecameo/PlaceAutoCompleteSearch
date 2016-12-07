package cameo.code.placeautocomplete;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Md. Sifat-Ul Haque on 12/3/2016.
 */
public class AutoCompletePlaceAdapter extends RecyclerView.Adapter<AutoCompletePlaceAdapter.PlaceViewHolder> {

    private ArrayList<PlaceModel> mPlaces = new ArrayList<>();
    private onPlaceSelectedListener mOnPlaceSelectedListener;

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_place_list, parent, false);
        return new AutoCompletePlaceAdapter.PlaceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        holder.updateInfo(position);
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    public void updatePlaceInfo(ArrayList<PlaceModel> places) {
        mPlaces = places;
        notifyDataSetChanged();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTvMainPlace, mTvSecondaryPlace;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            mTvMainPlace = (TextView) itemView.findViewById(R.id.tv_main_place);
            mTvSecondaryPlace = (TextView) itemView.findViewById(R.id.tv_secondary_place);
            itemView.setOnClickListener(this);
        }

        public void updateInfo(int position) {
            mTvMainPlace.setText(mPlaces.get(position).getMainPlace());
            mTvSecondaryPlace.setText(mPlaces.get(position).getSecondaryPlace());
        }

        @Override
        public void onClick(View v) {
            mOnPlaceSelectedListener.onPlaceSelected(mPlaces.get(getAdapterPosition()));
        }
    }


    public void setOnPlaceSelectedListener(onPlaceSelectedListener mOnPlaceSelectedListener) {
        this.mOnPlaceSelectedListener = mOnPlaceSelectedListener;
    }

    public interface onPlaceSelectedListener {
        void onPlaceSelected(PlaceModel placeModel);
    }
}
