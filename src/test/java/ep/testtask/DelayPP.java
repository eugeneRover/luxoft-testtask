package ep.testtask;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class DelayPP extends IdentifiedPriceProcessor{
    private final long delay;
    private final List<Pair<String, Double>> accumulator = new ArrayList<>();

    public DelayPP(int id, long delay) {
        super(id);
        this.delay = delay;
    }

    public List<Pair<String, Double>> getAccumulator() {
        return accumulator;
    }

    @Override
    public void onPrice(String ccyPair, double rate) {
        accumulator.add(new Pair<>(ccyPair,rate));

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
