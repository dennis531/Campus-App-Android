package de.tum.in.tumcampusapp.component.ui.overview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.component.ui.overview.card.Card;
import de.tum.in.tumcampusapp.component.ui.overview.card.CardViewHolder;
import de.tum.in.tumcampusapp.utils.Utils;

/**
 * Card that describes how to dismiss a card
 */
public class SupportCard extends Card {

    public SupportCard(Context context) {
        super(CardManager.CARD_SUPPORT, context, "", true);
    }

    public static CardViewHolder inflateViewHolder(ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.card_support, parent, false);
        //Add links to imageviews
        view.findViewById(R.id.facebook)
            .setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(view.getContext()
                                                    .getString(R.string.facebook_link)));
                view.getContext()
                    .startActivity(browserIntent);
            });
        view.findViewById(R.id.github)
            .setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(view.getContext()
                                                    .getString(R.string.github_link)));
                view.getContext()
                    .startActivity(browserIntent);
            });
        view.findViewById(R.id.email)
            .setOnClickListener(v -> {
                Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
                mailIntent.setData(Uri.parse(view.getContext().getString(R.string.support_email_link)));
                mailIntent.putExtra(Intent.EXTRA_SUBJECT, view.getContext().getString(R.string.feedback));
                view.getContext()
                    .startActivity(mailIntent);
            });
        return new CardViewHolder(view);
    }

    @Override
    public void discard(Editor editor) {
        Utils.setSetting(getContext(), CardManager.SHOW_SUPPORT, false);
    }

    @Override
    protected boolean shouldShow(SharedPreferences p) {
        return Utils.getSettingBool(getContext(), CardManager.SHOW_SUPPORT, true);
    }

    @Override
    public Intent getIntent() {
        return null;
    }

    @Override
    public int getId() {
        return 0;
    }
}
