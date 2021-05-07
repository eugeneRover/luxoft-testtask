package ep.testtask;

import java.util.*;

/**
 * Implemented on {@link PriceProcessor} requirements
 */
public class PriceThrottler extends IdentifiedPriceProcessor {

    private static final int SUBSCRIBERS_LIMIT = 200;

    private final Map<Integer, NotifierThread> subscriberNotifiers;//subscribers with their IDs
    private final Map<String,Double> ccyMap;//current rate values

    public PriceThrottler(int id) {
        super(id);
        this.subscriberNotifiers = Collections.synchronizedMap(new HashMap<>());
        this.ccyMap =  Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public void onPrice(String ccyPair, double rate) {

        //1. store current rate for the ccyPair
        ccyMap.put(ccyPair,rate);

        //2. enqueue notifications for all subscribed listeners
        subscriberNotifiers.values().forEach(t-> t.ccyPairChangedEvent(ccyPair));
    }

    @Override
    public void subscribe(PriceProcessor priceProcessor) {
        if (subscriberNotifiers.size()>SUBSCRIBERS_LIMIT){
            throw new RuntimeException("Subscriber limit (200) exceeded");
        }

        if (priceProcessor instanceof IdentifiedPriceProcessor){
            int ppid = ((IdentifiedPriceProcessor) priceProcessor).getId();
            subscriberNotifiers.computeIfAbsent(ppid,
                k->{
                    NotifierThread t = new NotifierThread(priceProcessor, ccyMap);
                    t.start();
                    return t;
                });
        }
        else{
            throw new RuntimeException("PriceProcessor should be instanceof IdentifiedPriceProcessor");
        }
    }

    @Override
    public void unsubscribe(PriceProcessor priceProcessor) {
        if (priceProcessor instanceof IdentifiedPriceProcessor){
            int ppid = ((IdentifiedPriceProcessor) priceProcessor).getId();
            NotifierThread t = subscriberNotifiers.remove(ppid);
            if (t != null){
                t.stopThread();//this only set inner stop flag. Notifier thread itself won't stop until
                // its inner queue completely processed
            }
        }
        else{
            throw new RuntimeException("PriceProcessor should be instanceof IdentifiedPriceProcessor");
        }
    }


}
