package ep.testtask;

import javafx.util.Pair;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestSuite {


    /**
     * Distribute updates to its listeners which are added through subscribe() and removed through unsubscribe()
     * @throws InterruptedException
     */
    @Test
    public void testSubscribeUnsubscribe() throws InterruptedException {
        PriceThrottler pt = new PriceThrottler(0);

        DelayPP d1 = new DelayPP(1,0);
        DelayPP d2 = new DelayPP(2,0);

        pt.onPrice("ZZZ", 12.34);//none

        pt.subscribe(d1);
        pt.onPrice("USDJPY", 1.25);//d1

        pt.subscribe(d2);
        pt.onPrice("CNYRUB", 154.67);//d1 d2

        pt.unsubscribe(d1);
        pt.onPrice("CHFDM", 6.27);//d2

        pt.unsubscribe(d2);
        pt.onPrice("BLABLA", -18);//none

        Thread.sleep(1000);

        assertEquals(2, d1.getAccumulator().size());
        assertEquals(new Pair("USDJPY", 1.25), d1.getAccumulator().get(0));
        assertEquals(new Pair("CNYRUB", 154.67), d1.getAccumulator().get(1));

        assertEquals(2, d2.getAccumulator().size());
        assertEquals(new Pair("CNYRUB", 154.67), d2.getAccumulator().get(0));
        assertEquals(new Pair("CHFDM", 6.27), d2.getAccumulator().get(1));
    }

    /**
     *  ONLY LAST PRICE for each ccyPair matters for subscribers. I.e. if a slow subscriber is not coping
     *  with updates for EURUSD - it is only important to deliver the latest rate
     *
     * @throws InterruptedException
     */
    @Test
    public void testOnlyLastPrice() throws InterruptedException {
        PriceThrottler pt = new PriceThrottler(0);

        DelayPP d1 = new DelayPP(1,0);//fast guy
        DelayPP d2 = new DelayPP(2,2000);//slowpoke

        pt.subscribe(d1);
        pt.subscribe(d2);

        pt.onPrice("USDJPY", 1.25);//d1 d2

        Thread.sleep(5);//we need the pause for fast PP to complete handling
        pt.onPrice("EURUSD", 201.0);//d1 d2(skipped)
        Thread.sleep(5);
        pt.onPrice("EURUSD", 202.0);//d1 d2(skipped)
        Thread.sleep(5);
        pt.onPrice("EURUSD", 203.0);//d1 d2


        pt.unsubscribe(d1);
        pt.unsubscribe(d2);

        Thread.sleep(4500);

        assertEquals(4, d1.getAccumulator().size());
        assertEquals(new Pair("USDJPY", 1.25), d1.getAccumulator().get(0));
        assertEquals(new Pair("EURUSD", 201.0), d1.getAccumulator().get(1));
        assertEquals(new Pair("EURUSD", 202.0), d1.getAccumulator().get(2));
        assertEquals(new Pair("EURUSD", 203.0), d1.getAccumulator().get(3));

        assertEquals(2, d2.getAccumulator().size());
        assertEquals(new Pair("USDJPY", 1.25), d2.getAccumulator().get(0));
        assertEquals(new Pair("EURUSD", 203.0), d2.getAccumulator().get(1));
    }
}
