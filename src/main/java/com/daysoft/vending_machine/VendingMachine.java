package com.daysoft.vending_machine;

import static java.util.Map.Entry;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VendingMachine {

  private MathContext mc = new MathContext(2, RoundingMode.HALF_EVEN);
  private static Logger logger = LoggerFactory.getLogger(
    MethodHandles.lookup().lookupClass().getSimpleName()
  );
  private BigDecimal balance = BigDecimal.ZERO;

  @Getter
  private Map<Item, Integer> items = new HashMap<>();

  private Map<String, Integer> coinQuantities = new HashMap<>();

  public VendingMachine(Map<String, Integer> initialBalanceInCoins) {
    this.coinQuantities = initialBalanceInCoins;
    initialBalanceInCoins.forEach((coin, q) ->
      this.balance =
        this.balance.add(
            Common.coinTypes.get(coin).multiply(BigDecimal.valueOf(q))
          )
    );
    logger.info(
      this.coinQuantities.entrySet()
        .stream()
        .map(i -> String.format("\t%s\t%d\n", i.getKey(), i.getValue()))
        .reduce(
          "\nCurrent Coin Quantities in machine: \n\tCoin, Quantity\n",
          String::concat
        )
    );
    logger.info(String.format("Balance: £%.2f\n", this.balance));
  }

  public void addItem(Item item, int quantity) {
    this.items.put(item, quantity);
    logger.info(
      String.format("Added %d units of item %s.", quantity, item.getName())
    );
  }

  public void removeItem(Item item) {
    this.items.remove(item);
  }

  public Map<String, Integer> convertToCoins(BigDecimal coins) {
    Map<String, Integer> returnCoins = new HashMap<>();

    while (coins.compareTo(BigDecimal.ZERO) > 0) {
      for (Entry<String, BigDecimal> i : Common.coinTypes.entrySet()) {
        BigDecimal value = Common.coinTypes.get(i.getKey());
        if (
          value.compareTo(coins) <= 0 &&
          this.coinQuantities.get(i.getKey()) != 0
        ) {
          coins = coins.subtract(value, mc);
          if (this.coinQuantities.get(i.getKey()) == 1) {
            logger.info(
              String.format("The stock of %s coins is depleted!", i.getKey())
            );
          }
          returnCoins.merge(i.getKey(), 1, Integer::sum);
          break;
        }
      }
    }
    //returnCoins.forEach((i, q) -> logString.append(String.format("\t%s, %d\n", i,q)));
    return returnCoins;
  }

  public Map<String, Integer> purchaseItem(
    String itemName,
    Map<String, Integer> coins
  ) {
    Item item =
      this.items.keySet()
        .stream()
        .filter(n -> n.getName().equals(itemName))
        .findFirst()
        .get();
    this.items.replace(item, this.items.get(item) - 1);

    logger.info(
      String.format(
        "Customer is attempting to purchase item: %s, price: £%.2f.",
        item.getName(),
        item.getPrice()
      )
    );

    BigDecimal amount = coins
      .entrySet()
      .stream()
      .map(i ->
        Common.coinTypes
          .get(i.getKey())
          .multiply(BigDecimal.valueOf(i.getValue()), mc)
      )
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    logger.info(String.format("Customer paid £%.2f.", amount));

    Map<String, Integer> returnChange = new HashMap<>();

    if (amount.compareTo(item.getPrice()) >= 0) {
      if (this.items.get(item) == 0) {
        this.items.remove(item);
        logger.info(
          String.format("Stock of item: %s depleted.", item.getName())
        );
      }
      BigDecimal change = amount.subtract(item.getPrice(), mc);
      this.balance = this.balance.subtract(amount, mc);
      logger.info(String.format("Returning £%.2f change.", change));
      Map<String, Integer> changeCoins = this.convertToCoins(change);
      logger.info(
        changeCoins
          .entrySet()
          .stream()
          .map(i -> String.format("\t%s\t%d\n", i.getKey(), i.getValue()))
          .reduce("\nCHANGE in coins: \n\tCoin\tQuantity\n", String::concat)
      );
      logger.info(String.format("Balance: £%.2f\n", this.balance));
      return changeCoins;
    } else {
      logger.info("Insufficient money to purchase item.");
    }
    return null;
  }
}
