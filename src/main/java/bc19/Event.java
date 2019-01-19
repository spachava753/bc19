package bc19;

import java.util.Date;

public abstract class Event {

    protected String name;
    protected long timestamp;

    protected Event() {
        this.name = Utils.DEFAULT_EVENT_NAME;
        timestamp = System.currentTimeMillis();
    }

    protected Event(final String name) {
        this.name = name;
        timestamp = System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Event");
        sb.append("{name='").append(name).append('\'');
        sb.append(", timestamp=").append(new Date(timestamp));
        sb.append('}');
        return sb.toString();
    }

}
