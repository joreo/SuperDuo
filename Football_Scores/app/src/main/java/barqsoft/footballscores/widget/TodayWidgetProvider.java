package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TodayWidgetProvider extends AppWidgetProvider {

    private static final String[] SCORE_COLUMNS = {
            DatabaseContract.scores_table.LEAGUE_COL,
            DatabaseContract.scores_table.DATE_COL,
            DatabaseContract.scores_table.TIME_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.MATCH_ID,
            DatabaseContract.scores_table.MATCH_DAY

    };
    private Cursor data = null;

    static final int INDEX_SCORES_DATE_COL = 1;
    static final int INDEX_SCORES_HOME_COL = 3;
    static final int INDEX_SCORES_AWAY_COL = 4;
    static final int INDEX_SCORES_HOME_GOALS_COL = 6;
    static final int INDEX_SCORES_AWAY_GOALS_COL = 7;
    static final int INDEX_SCORES_ID = 8;
    static final int INDEX_LEAGUE_COL = 0;





    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        int artResourceId = R.drawable.ic_launcher;
        String description = "Today's Scores";

        if (data != null) {
            data.close();
        }
        

        Cursor data = context.getContentResolver().query(DatabaseContract.BASE_CONTENT_URI, null, null, null, null);
        //Log.e("is there data?", Integer.toString(data.getCount()));


        data.moveToFirst();

        String home_team = data.getString(INDEX_SCORES_HOME_COL);
        String home_goals = data.getString(INDEX_SCORES_HOME_GOALS_COL);
        String away_team = data.getString(INDEX_SCORES_AWAY_COL);
        String away_goals = data.getString(INDEX_SCORES_AWAY_GOALS_COL);
        String score = "*" + home_goals + " - " + away_goals +"*";


        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.widget_today_large;
            RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);



            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Add the data to the RemoteViews
            views.setImageViewResource(R.id.widget_icon, artResourceId);
            // Content Descriptions for RemoteViews were only added in ICS MR1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                setRemoteContentDescription(views, description);
            }
            views.setTextViewText(R.id.widget_match_score, score);
            views.setTextViewText(R.id.widget_home_team, home_team + " vs.");
            views.setTextViewText(R.id.widget_away_team, away_team);

            // Set up the collection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views);
            } else {
                setRemoteAdapterV11(context, views);
            }
            boolean useDetailActivity = context.getResources()
                    .getBoolean(R.bool.use_detail_activity);
            Intent clickIntentTemplate = useDetailActivity
                    ? new Intent(context, MainActivity.class)
                    : new Intent(context, MainActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
            views.setEmptyView(R.id.widget_list, R.id.widget_empty);


            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, TodayWidgetRemoteViewsService.class));
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        //if (myFetchService.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        //}
    }
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list,
                new Intent(context, TodayWidgetRemoteViewsService.class));
    }

}