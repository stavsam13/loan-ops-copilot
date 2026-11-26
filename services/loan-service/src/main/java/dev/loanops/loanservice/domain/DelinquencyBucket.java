package dev.loanops.loanservice.domain;

/**
 * Delinquency classification by days past due.
 *
 * <p>These thresholds mirror section 4 of the (synthetic) internal credit policy that is indexed
 * for retrieval, so the agent can explain a classification by citing the policy clause alongside
 * the computed number. If the policy changes, this is the one place to change the code.
 */
public enum DelinquencyBucket {
    CURRENT(0, 0),
    DPD_1_30(1, 30),
    DPD_31_60(31, 60),
    DPD_61_90(61, 90),
    DEFAULT_90_PLUS(91, Long.MAX_VALUE);

    private final long minDays;
    private final long maxDays;

    DelinquencyBucket(long minDays, long maxDays) {
        this.minDays = minDays;
        this.maxDays = maxDays;
    }

    public static DelinquencyBucket fromDaysPastDue(long daysPastDue) {
        if (daysPastDue < 0) {
            throw new IllegalArgumentException("daysPastDue must be >= 0, was " + daysPastDue);
        }
        for (var bucket : values()) {
            if (daysPastDue >= bucket.minDays && daysPastDue <= bucket.maxDays) {
                return bucket;
            }
        }
        throw new IllegalStateException("No bucket for " + daysPastDue);
    }

    public boolean isOverdue() {
        return this != CURRENT;
    }
}
