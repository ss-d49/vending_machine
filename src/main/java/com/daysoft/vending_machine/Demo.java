package com.daysoft.vending_machine;

import static java.util.Map.Entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Demo {

  public static void main(String[] args) {
    Map<Integer, Item> purchasableItems = new LinkedHashMap<>() {
      {
        put(1, new Item("Tea", 0.90d));
        put(2, new Item("Coffee", 1.10d));
        put(3, new Item("Hot Chocolate", 1.50d));
      }
    };

    Scanner scn = new Scanner(System.in);
    System.out.print("Create Vending Machine? (y/n)");
    String response = scn.nextLine();
    if (response.equalsIgnoreCase("y")) {
      Map<String, Integer> coinQuantities = new LinkedHashMap<>();
      System.out.println("Specify quantity of each coin for float:\n");

      System.out.print("Specify same quantity for all coins? (y/n)");

      response = scn.nextLine();
      if (response.equalsIgnoreCase("y")) {
        System.out.print("  enter quantity: ");
        int quantity = Integer.parseInt(scn.nextLine());
        for (String coinType : Common.coinTypes.keySet()) {
          coinQuantities.put(coinType, quantity);
        }
        // coinQuantities = Common.coinTypes.keySet().stream().collect(Collectors.toMap(c -> c, q -> quantity, (k1, k2) -> k1,LinkedHashMap::new));
      } else if (response.equalsIgnoreCase("n")) {
        for (String coinType : Common.coinTypes.keySet()) {
          System.out.print(String.format("  %s: ", coinType));
          coinQuantities.put(coinType, Integer.parseInt(scn.nextLine()));
        }
        //coinQuantities.forEach((x, y) -> System.out.println(x +" "+ y));
      }
      VendingMachine hotDrinksMachine = new VendingMachine(coinQuantities);

      System.out.println("Select purchasable items to add to machine:");

      while (true) {
        System.out.println("ID\tName:\tPrice:");
        purchasableItems.forEach((num, itm) ->
          System.out.println(
            String.format("%d\t%s\t£%.02f", num, itm.getName(), itm.getPrice())
          )
        );
        System.out.print("\nEnter item number: ");
        int itemChoice = Integer.parseInt(scn.nextLine());
        System.out.print("Enter item quantity: ");
        int itemQuantity = Integer.parseInt(scn.nextLine());
        hotDrinksMachine.addItem(
          purchasableItems.get(itemChoice),
          itemQuantity
        );
        System.out.print("\nAdd another item? (y/n): ");
        response = scn.nextLine();
        if (response.equalsIgnoreCase("y")) {
          continue;
        } else if (response.equalsIgnoreCase("n")) {
          break;
        }
      }

      while (true) {
        System.out.print("Purchase an item? (y/n): ");
        response = scn.nextLine();
        if (response.equalsIgnoreCase("y")) {
          System.out.println("\nSelect item to purchase:");
          System.out.println("ID\tName:\tPrice:");
          purchasableItems
            .entrySet()
            .stream()
            .filter(itm ->
              hotDrinksMachine.getItems().containsKey(itm.getValue())
            )
            .forEach(itm ->
              System.out.println(
                String.format(
                  "%d\t%s\t£%.02f",
                  itm.getKey(),
                  itm.getValue().getName(),
                  itm.getValue().getPrice()
                )
              )
            );
          System.out.print("\nEnter item number: ");
          int itemChoice = Integer.parseInt(scn.nextLine());

          if (purchasableItems.containsKey(itemChoice)) {
            Map<String, Integer> payment = new HashMap<>();
            System.out.println(
              "Specify quantity of each coin to pay for item:"
            );

            for (String coinType : Common.coinTypes.keySet()) {
              System.out.print(String.format("  %s: ", coinType));
              payment.put(coinType, Integer.parseInt(scn.nextLine()));
            }
            Map<String, Integer> change = hotDrinksMachine.purchaseItem(
              purchasableItems.get(itemChoice).getName(),
              payment
            );
          } else {
            System.out.print(
              String.format("%d is not a valid option.", itemChoice)
            );
          }
        } else if (response.equalsIgnoreCase("n")) {
          break;
        }
      }
    } else if (response.equalsIgnoreCase("n")) {
      System.exit(0);
    }
  }
}
