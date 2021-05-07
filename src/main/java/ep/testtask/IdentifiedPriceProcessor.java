package ep.testtask;

/**
 * We really need some identifying entity in PP to distinct them, so
 * subscribe/unsubscribe methods should be called argument of class implementing the interface
 */
public interface IdentifiedPriceProcessor extends PriceProcessor{
    int getId();
}
