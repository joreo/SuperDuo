package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TodayWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = TodayWidgetRemoteViewsService.class.getSimpleName();
    private static final String[] SCORES_COLUMNS = {
            DatabaseContract.scores_table.MATCH_ID,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.DATE_COL
    };
    // these indices must match the projection

    static final int INDEX_SCORES_ID = 0;
    static final int INDEX_SCORES_AWAY_COL = 1;
    static final int INDEX_SCORES_AWAY_GOALS_COL = 2;
    static final int INDEX_SCORES_HOME_COL = 3;
    static final int INDEX_SCORES_HOME_GOALS_COL = 4;
    static final int INDEX_SCORES_DATE_COL = 5;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
                Log.e("RVF", "onCreate");
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
//                String location = Utility.getPreferredLocation(DetailWidgetRemoteViewsService.this);
//                Uri weatherForLocationUri = WeatherContract.WeatherEntry
//                        .buildWeatherLocationWithStartDate(location, System.currentTimeMillis());
//                data = getContentResolver().query(weatherForLocationUri,
                Uri weatherForLocationUri = DatabaseContract.scores_table
                        .buildScoreWithDate();
                data = getContentResolver().query(weatherForLocationUri,
                        SCORES_COLUMNS,
                        null,
                        null,
                        DatabaseContract.scores_table.DATE_COL + " ASC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_today_large);
//                int weatherId = data.getInt(INDEX_SCORES_AWAY_GOALS_COL);
//                int weatherArtResourceId = R.drawable.ic_launcher;
//                Bitmap weatherArtImage = null;
//                if ( !Utility.usingLocalGraphics(DetailWidgetRemoteViewsService.this) ) {
//                    String weatherArtResourceUrl = Utility.getArtUrlForWeatherCondition(
//                            DetailWidgetRemoteViewsService.this, weatherId);
//                    try {
//                        weatherArtImage = Glide.with(DetailWidgetRemoteViewsService.this)
//                                .load(weatherArtResourceUrl)
//                                .asBitmap()
//                                .error(weatherArtResourceId)
//                                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
//                    } catch (InterruptedException | ExecutionException e) {
//                        Log.e(LOG_TAG, "Error retrieving large icon from " + weatherArtResourceUrl, e);
//                    }
//                }
                String home_team = data.getString(INDEX_SCORES_HOME_COL);
                Log.e("home team", home_team);
                String home_goals = data.getString(INDEX_SCORES_HOME_GOALS_COL);
                String away_team = data.getString(INDEX_SCORES_AWAY_COL);
                String away_goals = data.getString(INDEX_SCORES_AWAY_GOALS_COL);
                String score = home_goals + " - " + away_goals;

//                long dateInMillis = data.getLong(INDEX_SCORES_AWAY_COL);
////                String formattedDate = Utility.getFriendlyDayString(
////                        DetailWidgetRemoteViewsService.this, dateInMillis, false);
//
//                double minTemp = data.getDouble(INDEX_SCORES_DATE_COL);
////                String formattedMaxTemperature =
//                        Utility.formatTemperature(DetailWidgetRemoteViewsService.this, maxTemp);
//                String formattedMinTemperature =
//                        Utility.formatTemperature(DetailWidgetRemoteViewsService.this, minTemp);
//                if (weatherArtImage != null) {
//                    views.setImageViewBitmap(R.id.widget_icon, weatherArtImage);
//                } else {
//                    views.setImageViewResource(R.id.widget_icon, weatherArtResourceId);
//                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, home_team);
                }
                views.setTextViewText(R.id.widget_home_team, home_team);
                views.setTextViewText(R.id.widget_away_team, away_team);
                views.setTextViewText(R.id.widget_match_score, score);
//                views.setTextViewText(R.id.widget_low_temperature, formattedMinTemperature);

//                final Intent fillInIntent = new Intent();
//                String locationSetting =
//                        Utility.getPreferredLocation(DetailWidgetRemoteViewsService.this);
//                Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
//                        locationSetting,
//                        dateInMillis);
//                fillInIntent.setData(weatherUri);
//                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_icon, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_today_large);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_SCORES_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
