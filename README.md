# silver-bars

Coding test for Credit Suisse

Assumptions and design decisions:

- Based on requirements example, Price £ is per Kg, assumed that price has no fraction, therefore long can be used.
- Based on requirements example, Quantity is in Kg and has fractions, therefore double can be used.
- Attempts to Cancel an Order not currently present will have no effect, and will not error, or return any message.
- Based on the requirements, the order summary should return both SELL and BUY orders in a single list. However, I have implemented my solution with separate Buy and Sell lists, partly because it made sorting easier, and partly because it seemed more realistic to be able to provide the summary in two separate lists as well.
- I also decided to hold onto the original Orders for completeness, rather than simply build the Map of Price to Quantity - future proofing?
- I decided to convert the original Order into a SummaryOrder which contains only the data that the UI would need according to the requirements. In this way I can return a list of SummaryOrder, which can easily be sent to the UI using JSON or the like.
- When an Order is cancelled, the TreeMap holding the relevant PriceByQuantity map entry will not be totally removed, simply zero'd out, therefore if another Order is registered with the same Price, it will increment the entry already in the map, as opposed to having to create a brand new entry.
- I have attempted to include a simplistic concurrency test around simultaneously registering and cancelling Orders.
