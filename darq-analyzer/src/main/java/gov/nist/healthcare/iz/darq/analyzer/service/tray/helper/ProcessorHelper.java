package gov.nist.healthcare.iz.darq.analyzer.service.tray.helper;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ProcessorHelper {
    protected Predicate<Tray> guard;
    protected Consumer<Tray> finalize;

    public ProcessorHelper(Predicate<Tray> guard, Consumer<Tray> finalize) {
        this.guard = guard;
        this.finalize = finalize;
    }
}
