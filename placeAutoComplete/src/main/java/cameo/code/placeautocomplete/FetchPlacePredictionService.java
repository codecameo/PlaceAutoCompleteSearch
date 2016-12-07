package cameo.code.placeautocomplete;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static cameo.code.placeautocomplete.PlaceAutoCompleteFragment.TAG_API_KEY;

/**
 * Created by Md. Sifat-Ul Haque on 12/2/2016.
 */

public class FetchPlacePredictionService extends IntentService {

    private static final long MAX_REQUEST_TIME = 60;
    public static final String AUTO_COMPLETE_PLACE_RECIEVER = "autoCompletePlaceReceiver";
    public static final String KEY_AUTO_COMPLETE_PLACES = "autoCompletePlaces";
    public static final String ACTION_FETCH_AUTO_COMPLETE_PLACE = "fetchAutoCompletePlace";
    public static final String ACTION_FETCH_PLACE_DETAIL = "fetchPlaceDetail";
    public static final int FAILURE_RESULT = 1001;
    public static final int SUCCESS_RESULT = 1002;
    public static final String KEY_ACTION = "keyAction";
    public static final String KEY_LAT = "keyLatitude";
    public static final String KEY_LNG = "keyLongitude";

    private ArrayList<PlaceModel> mPlaces;

    public ResultReceiver mReceiver;

    private final String JSON_TAG_PREDICTION = "predictions";
    private final String JSON_TAG_FORMAT = "structured_formatting";
    private final String JSON_TAG_MAIN_TEXT = "main_text";
    private final String JSON_TAG_SECONDARY_TEXT = "secondary_text";
    private final String JSON_TAG_PLACE_ID = "place_id";
    private final String JSON_TAG_REFERENCE = "reference";
    private final String JSON_TAG_DESCRIPTION = "description";

    private final String JSON_TAG_RESULT = "result";
    private final String JSON_TAG_GEOMETRY = "geometry";
    private final String JSON_TAG_LOCATION = "location";
    private final String JSON_TAG_LAT = "lat";
    private final String JSON_TAG_LNG = "lng";


    private final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/";
    private final String TYPE_AUTOCOMPLETE = "autocomplete/";
    private final String TYPE_PLACE_DETAIL = "details/";
    private final String OUTPUT_TYPE = "json";
    private final String PARAMS_PLACE_ID = "placeid";
    private final String PARAMS_INPUT = "input";

    public static final String TAG_QUERY_PLACE = "PlaceTag";
    private OkHttpClient httpClient;
    private String mAction;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public FetchPlacePredictionService() {
        super("FetchPlacePredictionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        httpClient = new OkHttpClient.Builder()
                .connectTimeout(MAX_REQUEST_TIME, TimeUnit.SECONDS)
                .readTimeout(MAX_REQUEST_TIME, TimeUnit.SECONDS)
                .writeTimeout(MAX_REQUEST_TIME, TimeUnit.SECONDS)
                .build();

        mReceiver = intent.getParcelableExtra(AUTO_COMPLETE_PLACE_RECIEVER);

        String API_KEY = intent.getStringExtra(TAG_API_KEY);
        String input = intent.getStringExtra(TAG_QUERY_PLACE);

        Log.d("text", input + " " + API_KEY);

        if (input == null && API_KEY == null) {
            throw new NullPointerException("Search text or API Key cannot be null.");
        }

        mAction = intent.getAction();

        if (mAction.equals(ACTION_FETCH_AUTO_COMPLETE_PLACE)) {
            fetchAutoCompleteText(API_KEY, input);
        } else if (mAction.equals(ACTION_FETCH_PLACE_DETAIL)) {
            fetchPlaceDetail(API_KEY, input);
        }


    }

    private void fetchPlaceDetail(String api_key, String input) {

        Response response = callPlaceApi(getApi(api_key, input, TYPE_PLACE_DETAIL, PARAMS_PLACE_ID));
        if (response != null) {
            parsePlaceDetail(response);
        } else {
            deliverPlaceDetailResultToReceiver(FAILURE_RESULT);
        }
    }

    private void parsePlaceDetail(Response response) {

        if (response.code() == 200) {

            try {
                String result = response.body().string();
                JSONObject response_params = new JSONObject(result);
                double lat = response_params.getJSONObject(JSON_TAG_RESULT).getJSONObject(JSON_TAG_GEOMETRY).getJSONObject(JSON_TAG_LOCATION).getDouble(JSON_TAG_LAT);
                double lng = response_params.getJSONObject(JSON_TAG_RESULT).getJSONObject(JSON_TAG_GEOMETRY).getJSONObject(JSON_TAG_LOCATION).getDouble(JSON_TAG_LNG);

                deliverPlaceDetailResultToReceiver(SUCCESS_RESULT, lat, lng);

            } catch (JSONException e) {
                e.printStackTrace();
                deliverPlaceDetailResultToReceiver(FAILURE_RESULT);
            } catch (IOException e) {
                e.printStackTrace();
                deliverPlaceDetailResultToReceiver(FAILURE_RESULT);
            }

        }


    }

    private void fetchAutoCompleteText(String api_key, String input) {

        Response response = callPlaceApi(getApi(api_key, input, TYPE_AUTOCOMPLETE, PARAMS_INPUT));

        if (response != null) {
            parsePlace(response);
        } else {
            deliverResultToReceiver(FAILURE_RESULT);
        }
    }

    private void parsePlace(Response response) {

        PlaceModel place;

        if (response.code() == 200) {
            try {
                String result = response.body().string();
                JSONObject response_params = new JSONObject(result);

                if (response_params.has(JSON_TAG_PREDICTION)) {
                    JSONArray jsonArray = response_params.getJSONArray(JSON_TAG_PREDICTION);
                    int size = jsonArray.length();
                    mPlaces = new ArrayList<>();

                    for (int i = 0; i < size; i++) {
                        place = new PlaceModel();
                        place.setPlaceId(jsonArray.getJSONObject(i).getString(JSON_TAG_PLACE_ID));
                        place.setReference(jsonArray.getJSONObject(i).getString(JSON_TAG_REFERENCE));
                        place.setPlaceFullName(jsonArray.getJSONObject(i).getString(JSON_TAG_DESCRIPTION));
                        place.setMainPlace(jsonArray.getJSONObject(i).getJSONObject(JSON_TAG_FORMAT).getString(JSON_TAG_MAIN_TEXT));
                        place.setSecondaryPlace(jsonArray.getJSONObject(i).getJSONObject(JSON_TAG_FORMAT).getString(JSON_TAG_SECONDARY_TEXT));
                        mPlaces.add(place);
                    }
                    deliverResultToReceiver(SUCCESS_RESULT);
                } else {
                    deliverResultToReceiver(FAILURE_RESULT);
                }


            } catch (JSONException e) {
                e.printStackTrace();
                deliverResultToReceiver(FAILURE_RESULT);
            } catch (IOException e) {
                e.printStackTrace();
                deliverResultToReceiver(FAILURE_RESULT);
            }
        } else {
            Log.d("Response", "" + response.message());
        }
    }

    private void deliverResultToReceiver(int resultCode) {
        //Log.i(LOG_TAG_SERVICE,"Deliver -> "+address);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ACTION, ACTION_FETCH_AUTO_COMPLETE_PLACE);
        bundle.putSerializable(KEY_AUTO_COMPLETE_PLACES, mPlaces);
        mReceiver.send(resultCode, bundle);
    }


    private void deliverPlaceDetailResultToReceiver(int resultCode) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ACTION, ACTION_FETCH_PLACE_DETAIL);
        mReceiver.send(resultCode, bundle);
    }


    private void deliverPlaceDetailResultToReceiver(int resultCode, double lat, double lng) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ACTION, ACTION_FETCH_PLACE_DETAIL);
        bundle.putDouble(KEY_LAT, lat);
        bundle.putDouble(KEY_LNG, lng);
        mReceiver.send(resultCode, bundle);
    }

    @NonNull
    private String getApi(String API_KEY, String input, String type, String params) {
        StringBuilder url = new StringBuilder(PLACES_API_BASE + type + OUTPUT_TYPE);
        url.append("?key=" + API_KEY);

        try {
            url.append("&" + params + "=" + URLEncoder.encode(input, "utf8"));
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this, "Error in generating api link", Toast.LENGTH_SHORT).show();
        }
        Log.d("Response", "URL " + String.valueOf(url));
        return String.valueOf(url);
    }

    private Response callPlaceApi(String url) {
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            return response;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void startService(Context context, String query, String apiKey, ResultReceiver autoCompletePlaceReceiver, String action) {
        Intent intent = new Intent(context, FetchPlacePredictionService.class);
        intent.setAction(action);
        intent.putExtra(FetchPlacePredictionService.TAG_QUERY_PLACE, query);
        intent.putExtra(TAG_API_KEY, apiKey);
        intent.putExtra(FetchPlacePredictionService.AUTO_COMPLETE_PLACE_RECIEVER, autoCompletePlaceReceiver);
        context.startService(intent);
    }

    public static void startAutoCompletePlaces(Context context, String queryText, String apiKey, ResultReceiver autoCompletePlaceReceiver) {
        startService(context, queryText, apiKey, autoCompletePlaceReceiver, ACTION_FETCH_AUTO_COMPLETE_PLACE);
    }

    public static void startFetchPlaceDetail(Context context, String placeId, String apiKey, ResultReceiver autoCompletePlaceReceiver) {
        startService(context, placeId, apiKey, autoCompletePlaceReceiver, ACTION_FETCH_PLACE_DETAIL);
    }
}
