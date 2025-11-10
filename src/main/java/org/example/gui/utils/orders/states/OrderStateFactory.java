package org.example.gui.utils.orders.states;

/**
 * factory to create the correct state object
 * from a database string.
 */
public class OrderStateFactory {

    /**
     * returns a state object based on the status string from the db.
     * return a concrete orderstate object
     */
    public static OrderState getState(String orderStatus) {
        if (orderStatus == null) {
            return new PendingState(); // default
        }

        // switch maps db enum values to the state classes
        switch (orderStatus.toLowerCase().trim()) {
            case "accepted":
                return new AcceptedState();
            case "in_progress":
                return new InProgressState();
            case "ready_for_delivery":
                return new ReadyForDeliveryState();
            case "out_for_delivery":
                return new OutForDeliveryState();
            case "completed":
                return new CompletedState();
//            case "cancelled":
//                return new CancelledState();
            case "pending":
            default:
                return new PendingState();
        }
    }
}