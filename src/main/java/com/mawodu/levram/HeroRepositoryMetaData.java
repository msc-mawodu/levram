package com.mawodu.levram;

public class HeroRepositoryMetaData {
    private int limit = 0;
    private int total = 0;
    private int count = 0;
    private int responseCode = 0;

    private HeroRepositoryMetaData(Builder builder) {
        this.limit = builder.limit;
        this.total = builder.total;
        this.count = builder.count;
        this.responseCode = builder.responseCode;
    }

    public int getLimit() {
        return limit;
    }

    public int getTotal() {
        return total;
    }

    public int getCount() {
        return count;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private int limit;
        private int total;
        private int count;
        private int responseCode;

        Builder() {}

        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public Builder total(int total) {
            this.total = total;
            return this;
        }

        public Builder count(int count) {
            this.count = count;
            return this;
        }

        public Builder responseCode(int responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public HeroRepositoryMetaData build() {
            return new HeroRepositoryMetaData(this);
        }


    }
}
