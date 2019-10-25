package edu.cnm.deepdive.blackjack.controller.fsm;

import edu.cnm.deepdive.blackjack.model.pojo.HandWithCards;
import java.util.EnumSet;

public enum RoundState {

  DEAL {
    @Override
    public RoundState getNextState(HandWithCards dealer, HandWithCards player,
        EnumSet<RuleVariation> variations) {
      RoundState next;
      if (dealer.isBlackjack()) {
        if (variations.contains(RuleVariation.NO_HOLE_CARD)) {
          next = player.isBlackjack() ? LOSS : PLAYER_ACTION;
        } else {
          next = LOSS;
        }
      } else if (player.isBlackjack()) {
        if (dealer.isPossibleBlackjack()) {
          next = DEALER_ACTION;
        } else {
          next = WIN;
        }
      } else if (player.getSoftValue() == 21) {
        next = DEALER_ACTION;
      } else {
        next = PLAYER_ACTION;
      }
      return next;
    }
  },
  PLAYER_ACTION {
    @Override
    public RoundState getNextState(HandWithCards dealer, HandWithCards player,
        EnumSet<RuleVariation> variations) {
      RoundState next;
      if (player.getSoftValue() > 21) {
        next = LOSS;
      } else if (player.getSoftValue() == 21 || player.isStaying()) {
        if (dealerCanHit(dealer, variations)) {
          next = DEALER_ACTION;
        } else if (player.getSoftValue() > dealer.getSoftValue()) {
          next = WIN;
        } else if (player.getSoftValue() < dealer.getSoftValue()) {
          next = LOSS;
        } else {
          next = PUSH;
        }
      } else {
        next = PLAYER_ACTION;
      }
      return next;
    }
  },
  DEALER_ACTION {
    @Override
    public RoundState getNextState(HandWithCards dealer, HandWithCards player,
        EnumSet<RuleVariation> variations) {
      RoundState next;
      if (dealer.isBlackjack()) {
        next = LOSS;
      } else if (dealer.getHardValue() > 21) {
        next = WIN;
      } else if (dealerCanHit(dealer, variations)) {
        next = DEALER_ACTION;
      } else if (dealer.getSoftValue() < player.getSoftValue()) {
        next = WIN;
      } else if (dealer.getSoftValue() > player.getSoftValue()) {
        next = LOSS;
      } else {
        next = PUSH;
      }
      return next;
    }
  },
  WIN,
  LOSS,
  PUSH;

  public static RoundState getInitialState() {
    return DEAL;
  }

  public static boolean dealerCanHit(HandWithCards dealer, EnumSet<RuleVariation> variations) {
    return dealer.isDealer()
        && (dealer.getSoftValue() < 17
            || (dealer.isSoft()
                && dealer.getSoftValue() == 17
                && !variations.contains(RuleVariation.STAND_ON_SOFT_17)));
  }

  public RoundState getNextState(HandWithCards dealer, HandWithCards player,
      EnumSet<RuleVariation> variations) {
    return this;
  }

  public boolean isTerminal() {
    boolean terminal;
    switch (this) {
      case WIN:
      case LOSS:
      case PUSH:
        terminal = true;
        break;
      default:
        terminal = false;
        break;
    }
    return terminal;
  }

  public enum RuleVariation {
    NO_HOLE_CARD,
    STAND_ON_SOFT_17
  }

}
