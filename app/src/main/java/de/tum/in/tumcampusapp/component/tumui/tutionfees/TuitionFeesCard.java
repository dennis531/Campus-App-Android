package de.tum.in.tumcampusapp.component.tumui.tutionfees;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.component.tumui.tutionfees.model.Tuition;
import de.tum.in.tumcampusapp.component.ui.overview.CardManager;
import de.tum.in.tumcampusapp.component.ui.overview.card.CardViewHolder;
import de.tum.in.tumcampusapp.component.ui.overview.card.NotificationAwareCard;
import de.tum.in.tumcampusapp.utils.DateUtils;
import de.tum.in.tumcampusapp.utils.Utils;

/**
 * Card that shows information about your fees that have to be paid or have been paid
 */
public class TuitionFeesCard extends NotificationAwareCard {

    private static final String LAST_FEE_FRIST = "fee_frist";
    private static final String LAST_FEE_SOLL = "fee_soll";
    private Tuition mTuition;

    public TuitionFeesCard(Context context) {
        super(CardManager.CARD_TUITION_FEE, context, "card_tuition_fee");
    }

    public static CardViewHolder inflateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.card_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public String getTitle() {
        return getContext().getString(R.string.tuition_fees);
    }

    @Override
    public void updateViewHolder(RecyclerView.ViewHolder viewHolder) {
        super.updateViewHolder(viewHolder);
        CardViewHolder cardsViewHolder = (CardViewHolder) viewHolder;
        List<View> addedViews = cardsViewHolder.getAddedViews();

        setMCard(viewHolder.itemView);
        setMLinearLayout(getMCard().findViewById(R.id.card_view));
        setMTitleView(getMCard().findViewById(R.id.card_title));
        getMTitleView().setText(getTitle());

        //Remove additional views
        for (View view : addedViews) {
            getMLinearLayout().removeView(view);
        }

        if ("0".equals(mTuition.getSoll())) {
            addedViews.add(addTextView(String.format(getContext().getString(R.string.reregister_success), mTuition.getSemesterBez())));
        } else {
            Date d = DateUtils.getDate(mTuition.getFrist());
            String date = DateFormat.getDateInstance()
                                    .format(d);
            addedViews.add(addTextView(String.format(getContext().getString(R.string.reregister_todo), date)));

            String balanceStr = mTuition.getSoll();
            try {
                Double balance = NumberFormat.getInstance(Locale.GERMAN)
                                             .parse(mTuition.getSoll())
                                             .doubleValue();

                balanceStr = String.format(Locale.GERMAN, "Value of a: %.2f", balance);
            } catch (ParseException ignore) {
            }
            addedViews.add(addTextView(viewHolder.itemView.getContext()
                                                          .getString(R.string.amount_dots) + ' ' + balanceStr + '€'));
        }
    }

    @Override
    public void discard(Editor editor) {
        editor.putString(LAST_FEE_FRIST, mTuition.getFrist());
        editor.putString(LAST_FEE_SOLL, mTuition.getSoll());
    }

    @Override
    protected boolean shouldShow(SharedPreferences prefs) {
        String prevFrist = prefs.getString(LAST_FEE_FRIST, "");
        String prevSoll = prefs.getString(LAST_FEE_SOLL, mTuition.getSoll());

        // If app gets started for the first time and fee is already paid don't annoy user
        // by showing him that he has been re-registered successfully
        return !(prevFrist.isEmpty() && "0".equals(mTuition.getSoll())) &&
               (prevFrist.compareTo(mTuition.getFrist()) < 0 || prevSoll.compareTo(mTuition.getSoll()) > 0);
    }

    @Override
    protected Notification fillNotification(NotificationCompat.Builder notificationBuilder) {
        if ("0".equals(mTuition.getSoll())) {
            notificationBuilder.setContentText(String.format(getContext().getString(R.string.reregister_success), mTuition.getSemesterBez()));
        } else {
            notificationBuilder.setContentText(mTuition.getSoll() + "€\n" +
                                               String.format(getContext().getString(R.string.reregister_todo), mTuition.getFrist()));
        }
        notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        notificationBuilder.setLargeIcon(Utils.getLargeIcon(getContext(), R.drawable.ic_money));
        Bitmap bm = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.wear_tuition_fee);
        notificationBuilder.extend(new NotificationCompat.WearableExtender().setBackground(bm));
        return notificationBuilder.build();
    }

    @Override
    public Intent getIntent() {
        return new Intent(getContext(), TuitionFeesActivity.class);
    }

    @Override
    public int getId() {
        return 0;
    }

    public void setTuition(Tuition tuition) {
        mTuition = tuition;
    }

    @Override
    public RemoteViews getRemoteViews(Context context) {
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.cards_widget_card);
        remoteViews.setTextViewText(R.id.widgetCardTextView, this.getTitle());
        remoteViews.setImageViewResource(R.id.widgetCardImageView, R.drawable.ic_money);
        return remoteViews;
    }
}
