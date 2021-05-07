package ep.testtask;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class DelayPP implements IdentifiedPriceProcessor{
    private final int id;
    private final long delay;
    private final List<Pair<String, Double>> accumulator = new ArrayList<>();

    public DelayPP(int id, long delay) {
        this.id = id;
        this.delay = delay;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public List<Pair<String, Double>> getAccumulator() {
        return accumulator;
    }

    @Override
    public void onPrice(String ccyPair, double rate) {
//        System.out.println("**************************************");
//        System.out.println(String.format("PP(id=%s) accbefore: size=%s",id,accumulator.size()));
        accumulator.add(new Pair<>(ccyPair,rate));
//        System.out.println(String.format("PP(id=%s) accafter: size=%s",id,accumulator.size()));
//
//        System.out.println(String.format("PP(id=%s) onPrice: ccyPair=%s, rate=%s",id,ccyPair,rate));

        if (delay>0) {
            try {
                Thread.sleep(this.delay);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void subscribe(PriceProcessor priceProcessor) {
        // not implemented
    }

    @Override
    public void unsubscribe(PriceProcessor priceProcessor) {
        // not implemented
    }
}
