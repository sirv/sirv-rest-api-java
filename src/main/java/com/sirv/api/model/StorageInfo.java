package com.sirv.api.model;

/**
 * Storage usage information.
 */
public class StorageInfo {
    private Long plan;
    private Long burstable;
    private Long extra;
    private Long used;
    private Long files;
    private String quotaExceededDate;

    public Long getPlan() {
        return plan;
    }

    public void setPlan(Long plan) {
        this.plan = plan;
    }

    public Long getBurstable() {
        return burstable;
    }

    public void setBurstable(Long burstable) {
        this.burstable = burstable;
    }

    public Long getExtra() {
        return extra;
    }

    public void setExtra(Long extra) {
        this.extra = extra;
    }

    public Long getUsed() {
        return used;
    }

    public void setUsed(Long used) {
        this.used = used;
    }

    public Long getFiles() {
        return files;
    }

    public void setFiles(Long files) {
        this.files = files;
    }

    public String getQuotaExceededDate() {
        return quotaExceededDate;
    }

    public void setQuotaExceededDate(String quotaExceededDate) {
        this.quotaExceededDate = quotaExceededDate;
    }

    /**
     * Gets the total allowance (plan + burstable + extra).
     *
     * @return The total storage allowance in bytes
     */
    public Long getAllowance() {
        long total = 0;
        if (plan != null) total += plan;
        if (burstable != null) total += burstable;
        if (extra != null) total += extra;
        return total;
    }

    @Override
    public String toString() {
        return "StorageInfo{" +
                "plan=" + plan +
                ", used=" + used +
                ", files=" + files +
                '}';
    }
}
