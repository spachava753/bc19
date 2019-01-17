package bc19;

public final class Log {

	public final static int V = 0;
	public final static int D = 1;
	public final static int I = 2;
	public final static int W = 3;
	public final static int E = 4;

	private static BCAbstractRobot robot;

	private Log() {}

	public interface Printer {
		public void print(int level, String tag, String msg);
	}

	private static class SystemOutPrinter implements Printer {
		private final static String[] LEVELS = new String[]{"V", "D", "I", "W", "E"};
		public void print(int level, String tag, String msg) {
			getRobot().log(LEVELS[level] + "/" + tag + ": " + msg);
		}
	}

	public final static SystemOutPrinter SYSTEM = new SystemOutPrinter();

	private static String[] mUseTags = new String[]{"tag", "TAG"};
	private static int mMinLevel = V;

	public static BCAbstractRobot getRobot() {
		return robot;
	}

	public static void setRobot(BCAbstractRobot robot) {
		Log.robot = robot;
	}

	public static synchronized Log useTags(String[] tags) {
		mUseTags = tags;
		return null;
	}

	public static synchronized Log level(int minLevel) {
		mMinLevel = minLevel;
		return null;
	}

	public static synchronized Log v(Object msg, Object... args) {
		log(V, msg, args);
		return null;
	}
	public static synchronized Log d(Object msg, Object... args) {
		log(D, msg, args);
		return null;
	}
	public static synchronized Log i(Object msg, Object... args) {
		log(I, msg, args);
		return null;
	}
	public static synchronized Log w(Object msg, Object... args) {
		log(W, msg, args);
		return null;
	}
	public static synchronized Log e(Object msg, Object... args) {
		log(E, msg, args);
		return null;
	}

	private static void log(int level, Object msg, Object... args) {
		if (level < mMinLevel) {
			return;
		}

		SYSTEM.print(level, mUseTags.toString(), String.valueOf(msg));
	}
}
