package coresearch.cvurl.io.util;


public enum FeatureFlag {
    ENABLED {
        @Override
        public boolean getValue() {
            return true;
        }

        @Override
        public void let(Runnable runnable) {
            runnable.run();
        }
    },
    DISABLED {
        @Override
        public boolean getValue() {
            return false;
        }

        @Override
        public void let(Runnable runnable) {
            //do nothing when flag is disabled
        }
    };

    public abstract boolean getValue();

    public abstract void let(Runnable runnable);

    public static FeatureFlag of(boolean enabled) {
        return enabled ? FeatureFlag.ENABLED : FeatureFlag.DISABLED;
    }
}