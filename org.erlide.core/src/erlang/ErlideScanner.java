package erlang;

import java.util.ArrayList;
import java.util.List;

import org.erlide.core.ErlangPlugin;
import org.erlide.core.erlang.ErlToken;
import org.erlide.core.erlang.ErlangCore;
import org.erlide.jinterface.backend.BackendException;
import org.erlide.jinterface.backend.util.Assert;
import org.erlide.jinterface.backend.util.Util;
import org.erlide.jinterface.util.ErlLogger;
import org.erlide.jinterface.util.ErlUtils;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangBinary;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;

public class ErlideScanner {
	private static final String ERLIDE_SCANNER = "erlide_scanner_server";

	public static void initialScan(final String module,
			final String moduleFileName, final String initialText,
			final String erlidePath, final boolean updateCaches) {
		final String stateDir = ErlangPlugin.getDefault().getStateLocation()
				.toString();
		try {
			final OtpErlangObject res = ErlangCore.getBackendManager()
					.getIdeBackend().call(ERLIDE_SCANNER, "initialScan",
							"asssso", module, moduleFileName, initialText,
							stateDir, erlidePath, updateCaches);
			ErlLogger.debug("initialScan " + module + " " + res);
		} catch (final Exception e) {
			ErlLogger.debug(e);
		}
	}

	public static void destroy(final String module) {
		try {
			ErlangCore.getBackendManager().getIdeBackend().call(ERLIDE_SCANNER,
					"destroy", "a", module);
		} catch (final Exception e) {
			ErlLogger.debug(e);
		}
	}

	@SuppressWarnings("boxing")
	public static ErlToken getTokenAt(final String module, final int offset) {
		OtpErlangObject r1 = null;
		try {
			r1 = ErlangCore.getBackendManager().getIdeBackend().call(
					ERLIDE_SCANNER, "getTokenAt", "ai", module, offset);
			// ErlLogger.debug("getTokenAt -> " + r1);
		} catch (final Exception e) {
			// e.printStackTrace();
			return null;
		}
		if (r1 == null || !(r1 instanceof OtpErlangTuple)) {
			return null;
		}
		final OtpErlangTuple t1 = (OtpErlangTuple) r1;
		if (Util.isOk(t1)) {
			final OtpErlangObject ot = t1.elementAt(1);
			if (ot instanceof OtpErlangTuple) {
				final OtpErlangTuple tt = (OtpErlangTuple) ot;
				return new ErlToken(tt);
			}
		}
		return null;
	}

	@SuppressWarnings("boxing")
	public static void replaceText(final String module, final int offset,
			final int removeLength, final String newText) {
		try {
			// ErlLogger.debug("replaceText %s %d %d <length %d>", module,
			// offset,
			// removeLength, newTextLen);
			// ErlLogger.debug("replaceText %s %d %d \"%s\"", module, offset,
			// removeLength, newText);
			final OtpErlangObject r = ErlangCore.getBackendManager()
					.getIdeBackend().call(ERLIDE_SCANNER, "replaceText",
							"aiis", module, offset, removeLength, newText);
			if (r instanceof OtpErlangTuple) {
				ErlLogger.error("GOT::" + r.toString());
			}
		} catch (final BackendException e) {
			ErlLogger.debug(e);
		}
	}

	/**
	 * @param string
	 * @param offset
	 * @return
	 * @throws BackendException
	 */
	public static List<ErlToken> lightScanString(final String string,
			final int offset) throws BackendException {
		OtpErlangObject r1 = null;
		try {
			r1 = ErlangCore.getBackendManager().getIdeBackend().call(
					"erlide_scanner", "light_scan_string", "b", string);
		} catch (final Exception e) {
			throw new BackendException("Could not parse string \"" + string
					+ "\": " + e.getMessage());
		}
		if (r1 == null) {
			return null;
		}

		if (!(r1 instanceof OtpErlangTuple)) {
			throw new BackendException("Could not parse string \"" + string
					+ "\": weird return value " + r1);
		}
		final OtpErlangTuple t1 = (OtpErlangTuple) r1;

		List<ErlToken> toks = null;
		if (!(t1.elementAt(0) instanceof OtpErlangAtom)) {
			throw new BackendException("Could not parse string \"" + string
					+ "\": funny return value" + t1);
		}
		if (Util.isOk(t1)) {
			if (t1.elementAt(1) instanceof OtpErlangList) {
				final OtpErlangList l = (OtpErlangList) t1.elementAt(1);
				if (l != null) {
					toks = new ArrayList<ErlToken>(l.arity() + 1);
					for (final OtpErlangObject o : l) {
						final OtpErlangTuple t = (OtpErlangTuple) o;
						final ErlToken tk = new ErlToken(t);
						tk.fixOffset(offset);
						toks.add(tk);
					}
					return toks;
				}
			} else if (t1.elementAt(1) instanceof OtpErlangBinary) {
				final OtpErlangBinary b = (OtpErlangBinary) t1.elementAt(1);
				final byte[] bytes = b.binaryValue();
				toks = new ArrayList<ErlToken>(bytes.length / 10);
				for (int i = 0; i < bytes.length; i += 10) {
					final ErlToken tk = new ErlToken(bytes, i);
					tk.fixOffset(offset);
					toks.add(tk);
				}
				return toks;
			}
		}
		throw new BackendException("Could not parse string \"" + string
				+ "\": " + t1.elementAt(1).toString());
	}

	public static String checkAll(final String module, final String text) {
		if (module == null) {
			return "";
		}
		try {
			final OtpErlangObject o = ErlangCore.getBackendManager()
					.getIdeBackend().call(ERLIDE_SCANNER, "check_all", "as",
							module, text);
			return o.toString();
		} catch (final BackendException e) {
			return "";
		}

	}

	@SuppressWarnings("boxing")
	public static void notifyChange(final String scannerName, final int offset,
			final int length, final String text) {
		Assert.isNotNull(scannerName);
		try {
			final OtpErlangObject msg = ErlUtils.format(
					"{change, ~a, ~i,  ~i, ~s}", scannerName, offset, length,
					text);
			ErlangCore.getBackendManager().getIdeBackend().send(
					"erlide_scanner_listener", msg);
		} catch (final Exception e) {
			ErlLogger.warn(e);
		}
	}

	public static void notifyNew(final String scannerName) {
		Assert.isNotNull(scannerName);
		try {
			final OtpErlangObject msg = ErlUtils.format("{new, ~a}",
					scannerName);
			ErlangCore.getBackendManager().getIdeBackend().send(
					"erlide_scanner_listener", msg);
		} catch (final Exception e) {
			ErlLogger.warn(e);
		}
	}

}
