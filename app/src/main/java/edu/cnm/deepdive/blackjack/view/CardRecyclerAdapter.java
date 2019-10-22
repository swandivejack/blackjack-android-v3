package edu.cnm.deepdive.blackjack.view;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.State;
import com.squareup.picasso.Picasso;
import edu.cnm.deepdive.blackjack.R;
import edu.cnm.deepdive.blackjack.model.entity.Card;
import edu.cnm.deepdive.blackjack.service.DeckOfCardsService;
import edu.cnm.deepdive.blackjack.view.CardRecyclerAdapter.CardHolder;
import java.util.List;

public class CardRecyclerAdapter extends RecyclerView.Adapter<CardHolder> {

  private final Context context;
  private final List<Card> cards;

  public CardRecyclerAdapter(Context context, List<Card> cards) {
    this.context = context;
    this.cards = cards;
  }

  @NonNull
  @Override
  public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.card_list_item, parent, false);
    return new CardHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull CardHolder holder, int position) {
    holder.bind(cards.get(position));
  }

  @Override
  public int getItemCount() {
    return cards.size();
  }

  class CardHolder extends RecyclerView.ViewHolder {

    private final ImageView imageView;

    private CardHolder(@NonNull View itemView) {
      super(itemView);
      imageView = (ImageView) itemView;
    }

    private void bind(Card card) {
      imageView.setContentDescription(
          context.getString(R.string.card_content_description, card.getRank(), card.getSuit()));
      Picasso.get().load(DeckOfCardsService.getImageUrl(card).toString()).into(imageView);
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

}
