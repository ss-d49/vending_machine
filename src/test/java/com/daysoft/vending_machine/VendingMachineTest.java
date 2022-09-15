package com.daysoft.vending_machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.LinkedHashMap;
import static java.util.Map.entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VendingMachineTest {
  private Map<Integer, Item> purchasableItems = new LinkedHashMap<>() {
    {
      put(1, new Item("Tea", 0.90d));
      put(2, new Item("Coffee", 1.10d));
      put(3, new Item("Hot Chocolate", 1.50d));
    }
  };
  private Map<String, Integer> coinQuantities = new LinkedHashMap<>();
  private VendingMachine hotDrinksMachine = null;

  @Test
  @DisplayName("Change from £1 for 90p item is 10p.")
  public void testChangeFrom1for90is10(){
    this.coinQuantities = new LinkedHashMap<>();
    for (String coinType : Common.coinTypes.keySet()) {
      this.coinQuantities.put(coinType, 10);
    }

    this.hotDrinksMachine = new VendingMachine(coinQuantities);

    this.hotDrinksMachine.addItem(
      this.purchasableItems.get(1),
      10
    );

    Map<String, Integer> change = this.hotDrinksMachine.purchaseItem(
      this.purchasableItems.get(1).getName(),
      new HashMap<String, Integer>(){{
        put("£2", 0);
        put("£1", 1);
        put("50p", 0);
        put("20p", 0);
        put("10p", 0);
        put("5p", 0);
        put("2p", 0);
        put("1p", 0);
      }}
    );
      assertEquals( Map.ofEntries(entry("10p", 1)), change, "Change should be 1 x 10p." );
  }

  @Test
  @DisplayName("Change from £2 for £1.10 item is 1 x 50p, 2 x 20p.")
  public void testChangeFrom2for110is1x502x20(){
    this.coinQuantities = new LinkedHashMap<>();
    for (String coinType : Common.coinTypes.keySet()) {
      this.coinQuantities.put(coinType, 10);
    }

    this.hotDrinksMachine = new VendingMachine(coinQuantities);

    this.hotDrinksMachine.addItem(
      this.purchasableItems.get(2),
      5
    );

    Map<String, Integer> change = this.hotDrinksMachine.purchaseItem(
      this.purchasableItems.get(2).getName(),
      new HashMap<String, Integer>(){{
        put("£2", 1);
        put("£1", 0);
        put("50p", 0);
        put("20p", 0);
        put("10p", 0);
        put("5p", 0);
        put("2p", 0);
        put("1p", 0);
      }}
    );
      assertEquals( Map.ofEntries(entry("50p", 1),entry("20p", 2)), change, "Change should be 1 x 50p, 2 x 20p." );
  }

  @Test
  @DisplayName("Change from £1 for 90p item is 2 x 5p if depleted 10p stock.")
  public void testChangeFrom1for90is2x5IfDepeleted10(){
    this.coinQuantities = new LinkedHashMap<>();
    for (String coinType : Common.coinTypes.keySet()) {
      if(coinType.equals("10p")){
        this.coinQuantities.put(coinType, 0);
      }
      else{
        this.coinQuantities.put(coinType, 10);
      }
    }

    this.hotDrinksMachine = new VendingMachine(coinQuantities);

    this.hotDrinksMachine.addItem(
      this.purchasableItems.get(1),
      5
    );

    Map<String, Integer> change = this.hotDrinksMachine.purchaseItem(
      this.purchasableItems.get(1).getName(),
      new HashMap<String, Integer>(){{
        put("£2", 0);
        put("£1", 1);
        put("50p", 0);
        put("20p", 0);
        put("10p", 0);
        put("5p", 0);
        put("2p", 0);
        put("1p", 0);
      }}
    );
      assertEquals( Map.ofEntries(entry("5p", 2)), change, "Change should be 2 x 5p." );
  }
}
