# vending_machine

Running the application requires compilation:

open a terminal in the root of this project and run "mvn clean package"


then run "java -jar target/vending_machine_demo-1.0.jar"

You should now be faced with several questions in order to demonstrate the vending machine.

In order to ensure that the number of each coin in the machine's float is known, I have designed it so that rather than specifying the float as a monetary figure, you must instead specifiy the quantity of each type of coin that will be inserted into thhe machine.

As the machine is designed for the UK market as per the brief, the coins are as follows: £2, £1, 50p, 20p, 10p, 5p, 2p, 1p.

If given more time I would have liked to make the machine internationally compatible by allowing the user to create a vending machine with any currency.

So far, coins can be added only once on creation of the machine, although items can be added, removed or purchased.

Obviously given more time, I would implement all of the additional methods for replenshing the coins in the machine or withdrawing coins.

As far as possilbe the machine will give back accurate change in coins. If there are not enough of a particular value of coin, it will use the next value down.
