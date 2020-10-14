package com.game.fastandsmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CardsAdapter extends ArrayAdapter<Card> {
    private final ArrayList<Card> cards;
    private final LayoutInflater layoutInflater;

    public CardsAdapter(Context context, ArrayList<Card> cards) {
        super(context, -1);
        this.cards = cards;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getPosition(@Nullable Card item) {
        return cards.indexOf(item);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view == null) {
            view = layoutInflater.inflate(R.layout.card_layout, parent, false);
        }

        Card card = cards.get(position);
        //((ImageView) view.findViewById(R.id.card_image)).setImageResource(card.imageId);
        ((TextView) view.findViewById(R.id.cardText)).setText(card.mQuestion);

        return view;
    }

    @Override public Card getItem(int position) {
        return cards.get(position);
    }

    @Override public int getCount() {
        return cards.size();
    }
}