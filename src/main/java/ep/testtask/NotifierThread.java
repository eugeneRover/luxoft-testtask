package ep.testtask;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Thread that notifies single predefined PriceProcessor of rate changes.<br/>
 * Keeps inner queue with unique elements (ccyPairs).<br/>
 * Important: PriceProcessor is notified of changes strictly sequentially<br/>
 */
public class NotifierThread extends Thread {

    private static final long QUEUE_WAIT_TIME_MS = 50;
    private final PriceProcessor pp;
    private final Map<String,Double> ccyMap;
    //this queue contains only unique strings (ccypairs)
    private final LinkedBlockingQueue<String> queue;
    private boolean stop = false;

    public NotifierThread(PriceProcessor pp, Map<String, Double> ccyMap) {
        this.pp = pp;
        this.ccyMap = ccyMap;

        this.queue = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        //stop the thread only if stop flag set and queue is empty
        while (!stop || !queue.isEmpty()) {
            try {
                String ccyPair = queue.poll(QUEUE_WAIT_TIME_MS, TimeUnit.MILLISECONDS);
                if (ccyPair != null) {
                    //1. Notify PP only with last rate
                    pp.onPrice(ccyPair, ccyMap.get(ccyPair));
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void ccyPairChangedEvent(String ccyPair){
        synchronized (queue){//we need this in case of simultaneous notifying from multiple threads
            if (!queue.contains(ccyPair)){
                queue.offer(ccyPair);
            }
        }
    }

    public synchronized void stopThread(){
        this.stop = true;
    }
}
