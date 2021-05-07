package ep.testtask;

import java.util.Objects;

/**
 * We really need some identifying entity in PP to distinct them, so
 * subscribe/unsubscribe methods should be called argument of class implementing the interface
 */
public abstract class IdentifiedPriceProcessor implements PriceProcessor{
    private final int id;

    protected IdentifiedPriceProcessor(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IdentifiedPriceProcessor that = (IdentifiedPriceProcessor) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getId() {
        return id;
    }
}
