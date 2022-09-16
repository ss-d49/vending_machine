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
    this.items.merge(item, quantity, Integer::sum);
    logger.info(
      String.format("Added %d units of item %s.", quantity, item.getName())
    );

    logger.info(
      this.items.entrySet()
        .stream()
        .map(i -> String.format("\t%s\t%d\n", i.getKey().getName(), i.getValue()))
        .reduce(
          "\nCurrent Item stock in machine: \n\tItem, Quantity\n",
          String::concat
        )
    );
  }

  public void removeItem(Item item) {
    this.items.remove(item);
    logger.info(
      this.items.entrySet()
        .stream()
        .map(i -> String.format("\t%s\t%d\n", i.getKey().getName(), i.getValue()))
        .reduce(
          "\nCurrent Item stock in machine: \n\tItem, Quantity\n",
          String::concat
        )
    );
  }

  public Map<String, Integer> convertToCoins(BigDecimal coins) throws insufficientChangeError{
    Map<String, Integer> returnCoins = new HashMap<>();
    int counter = 0;
    Map<String, Integer> copyOfCoinQuantities = new HashMap<>();
    copyOfCoinQuantities.putAll(this.coinQuantities);
    while (coins.compareTo(BigDecimal.ZERO) > 0) {

      for (Entry<String, BigDecimal> i : Common.coinTypes.entrySet()) {
        BigDecimal value = Common.coinTypes.get(i.getKey());
        if (
          value.compareTo(coins) <= 0 &&
          copyOfCoinQuantities.get(i.getKey()) > 0
        ) {
          coins = coins.subtract(value);
          if (copyOfCoinQuantities.get(i.getKey()) == 1) {
            logger.info(
              String.format("The stock of %s coins is depleted!", i.getKey())
            );
          }
          returnCoins.merge(i.getKey(), 1, Integer::sum);
          copyOfCoinQuantities.merge(i.getKey(), -1, Integer::sum);
          break;
        }
        counter++;
        if (counter>100){
          // logger.info("Insufficient change in machine.");
          // return Map.of();
          throw new insufficientChangeError("Insufficient change in machine.");
        }
      }
    }
    this.coinQuantities = copyOfCoinQuantities;
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
          .multiply(BigDecimal.valueOf(i.getValue()))
      )
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    logger.info(String.format("Customer paid £%.2f.", amount));

    Map<String, Integer> returnChange = new HashMap<>();
    Map<String, Integer> changeCoins = Map.of();
    if (amount.compareTo(item.getPrice()) >= 0) {
      if (this.items.get(item) == 0) {
        this.items.remove(item);
        logger.info(
          String.format("Stock of item: %s depleted.", item.getName())
        );
      }
      BigDecimal change = amount.subtract(item.getPrice());

      logger.info(String.format("Returning £%.2f change.", change));

      try{
        changeCoins = this.convertToCoins(change);
        this.balance = this.balance.subtract(change);
      }
      catch(insufficientChangeError e){
        logger.info(e.getMessage());
        logger.info("Returning money");
        changeCoins = coins;
      }
    } else {
      logger.info("Insufficient money to purchase item. Returning money.");
      changeCoins = coins;
    }
    logger.info(
      changeCoins
        .entrySet()
        .stream()
        .map(i -> String.format("\t%s\t%d\n", i.getKey(), i.getValue()))
        .reduce("\nCHANGE in coins: \n\tCoin\tQuantity\n", String::concat)
    );
    logger.info(
      this.coinQuantities
        .entrySet()
        .stream()
        .map(i -> String.format("\t%s\t%d\n", i.getKey(), i.getValue()))
        .reduce("\nCoins in Machine: \n\tCoin\tQuantity\n", String::concat)
    );
    logger.info(String.format("Balance: £%.2f\n", this.balance));

    logger.info(
      this.items.entrySet()
        .stream()
        .map(i -> String.format("\t%s\t%d\n", i.getKey().getName(), i.getValue()))
        .reduce(
          "\nCurrent Item stock in machine: \n\tItem, Quantity\n",
          String::concat
        )
    );
    return changeCoins;
  }
}
