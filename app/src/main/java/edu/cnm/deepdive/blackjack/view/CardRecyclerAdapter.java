package edu.cnm.deepdive.blackjack.view;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.State;
import com.squareup.picasso.Picasso;
import edu.cnm.deepdive.blackjack.R;
import edu.cnm.deepdive.blackjack.model.entity.Card;
import edu.cnm.deepdive.blackjack.model.pojo.HandWithCards;
import edu.cnm.deepdive.blackjack.service.DeckOfCardsService;
import edu.cnm.deepdive.blackjack.view.CardRecyclerAdapter.CardHolder;

public class CardRecyclerAdapter extends RecyclerView.Adapter<CardHolder> {

  private final Context context;
  private final HandWithCards hand;
  private boolean complete;

  public CardRecyclerAdapter(Context context, HandWithCards hand) {
    this.context = context;
    this.hand = hand;
  }

  @NonNull
  @Override
  public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.card_list_item, parent, false);
    return new CardHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull CardHolder holder, int position) {
    holder.bind(position);
  }

  @Override
  public int getItemCount() {
    return hand.getCards().size();
  }

  public void setComplete(boolean complete) {
    this.complete = complete;
    if (complete) {
      notifyItemChanged(0);
    }
  }

  public static class OverlapDecoration extends RecyclerView.ItemDecoration {

    private final int verticalOffset;
    private final int horizontalOffset;

    public OverlapDecoration() {
      this(0, 0);
    }

    public OverlapDecoration(int verticalOffset, int horizontalOffset) {
      this.verticalOffset = verticalOffset;
      this.horizontalOffset = horizontalOffset;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
        @NonNull RecyclerView parent, @NonNull State state) {
      final int itemPosition = parent.getChildAdapterPosition(view);
      if (itemPosition == 0) {
        super.getItemOffsets(outRect, view, parent, state);
      } else {
        outRect.set(horizontalOffset, verticalOffset, 0, 0);
      }
    }
  }

  class CardHolder extends RecyclerView.ViewHolder {

    private final ImageView imageView;

    private CardHolder(@NonNull View itemView) {
      super(itemView);
      imageView = (ImageView) itemView;
    }

    private void bind(int position) {
      if (!complete && hand.isDealer() && hand.getCards().size() <= 2 && position == 0) {
        imageView.setContentDescription(context.getString(R.string.hole_card_description));
        imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.card_back));
      } else {
        Card card = hand.getCards().get(position);
        imageView.setContentDescription(
            context.getString(R.string.card_content_description, card.getRank(), card.getSuit()));
        Picasso.get().load(DeckOfCardsService.getImageUrl(card).toString()).into(imageView);
      }
    }

  }

}
