package bar;

import entities_states.Waiter_State;
import semaphore.Semaphore;
import java.io.IOException;
import stubs.GeneralRepoStub;

/**
 * Defines the bar that constitutes the service provided
 * @author Diogo Jorge
 */
public class Bar {

    private final Semaphore access;
    private char situation;
    private boolean presentBill = false;

    private final Semaphore waiterInTheBar;
    private final Semaphore waitingForStudentsToFinish;
    private GeneralRepoStub generalRepoStub;

    /**
     * 
     * @param StudentSize Student number
     * @param generalRepoStub GeneralRepo Stub
     */
    public Bar(int StudentSize, GeneralRepoStub generalRepoStub) {
        this.waiterInTheBar = new Semaphore();
        this.waitingForStudentsToFinish = new Semaphore();
        this.access = new Semaphore();
        access.up();
        this.generalRepoStub = generalRepoStub;
    }

    /**
     * Waiter in the bar waiting for wake up actions
     *
     * @return Waiter next situation
     * @throws IOException if the waiter state can't be written to the logger file
     */
    public char lookAround() throws IOException {
        access.down();
//        System.out.println("Bar         Waiter      Looks around: " + situation);
        generalRepoStub.updateWaiterState(Waiter_State.ATS);
        access.up();
        if (presentBill == true) {
        }
        waiterInTheBar.down();
        return situation;
    }

    /**
     * Changes waiter situation
     *
     * @param newsituation Waiter new situation
     */
    public void changeSituation(char newsituation) {
        situation = newsituation;
//        System.out.println("Bar         Waiter      Changing situation to: " + newsituation);
    }

    /**
     * First student to arrive calls the waiter to describe order
     */
    public void callTheWaiter() {
        access.down();
//        System.out.println("Bar         Student     Call the waiter");
        changeSituation('o');
        waiterInTheBar.up();
        access.up();

    }

    /**
     * Releases waiter from the bar, used by the chef
     */
    public void waiterInTheBarUp() {
        waiterInTheBar.up();
    }

    /**
     * Waiter returns to the bar
     *
     * @throws IOException if the waiter state can't be written to the logger file 
     */
    public void returnToTheBar() throws IOException {
        access.down();
//        System.out.println("Bar         Waiter      Return to the bar");
        generalRepoStub.updateWaiterState(Waiter_State.ATS);
        access.up();
    }

    /**
     * Waiter prepares the bill
     *
     * @throws IOException if the waiter state can't be written to the logger file
     */
    public void prepareTheBill() throws IOException {
        generalRepoStub.updateWaiterState(Waiter_State.PTB);
//        System.out.println("Bar         Waiter      Prepare the bill");
        presentBill = true;
    }

    /**
     * Last student to finish course signals the waiter to bring the next one
     */
    public void SignalTheWaiter() {
        access.down();
//        System.out.println("Bar         Student     Signal the waiter");
        changeSituation('c');
        waitingForStudentsToFinish.up();
        access.up();
    }

    /**
     * Last student to arrive pays the waiter
     */
    public void PayTheWaiter() {
        access.down();
//        System.out.println("Bar         Student     Pay the waiter");
        changeSituation('p');
        waiterInTheBar.up();
        access.up();
    }

    /**
     * Waiting for students to finish course, used by the chef
     */
    public void waitingForStudentsToFinishDown() {
        waitingForStudentsToFinish.down();
    }

    /**
     * Student x alerts the waiter he is entering
     */
    public void alertWaiterEntering() {
        access.down();
//        System.out.println("Bar         Student     Alert the waiter entering");
        changeSituation('n');
        waiterInTheBar.up();
        access.up();
    }

    /**
     * Chef calls the waiter to serve
     */
    void CallTheWaitertoServe() {
        access.down();
        changeSituation('c');
        access.up();
    }

    /**
     * Student x alerts the waiter he is leaving
     */
    void alertTheWaiterExit() {
        access.down();
        changeSituation('g');
        waiterInTheBar.up();
        access.up();
    }

    /**
     * Waiter finishes and leaves
     */
    void waiterGoHome() {
        access.down();
        changeSituation('e');
        waiterInTheBar.up();
//        System.out.println("Bar         Waiter      Go home");
        access.up();
    }

}
