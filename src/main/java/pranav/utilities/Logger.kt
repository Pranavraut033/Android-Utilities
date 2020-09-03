package pranav.utilities

import android.util.Log

/**
 * Created on 27-01-19 at 13:04 by Pranav Raut.
 * For ProVeteran
 */
class Logger(
    var verbose: Boolean = DEBUG,
    var debug: Boolean = VERBOSE,
    var tag: String = "Logger"
) {

    constructor(enable: Boolean, tag: String) : this(enable, enable, tag)

    constructor(tag: String) : this(true, tag = tag)

    init {
        debug = verbose || debug
    }

    fun d(vararg data: Any?) {
        d(*data, tag = tag, b = debug, i = 1)
    }

    fun i(vararg data: Any?) {
        i(*data, tag = tag, b = verbose, i = 1)
    }

    fun v(vararg data: Any?) {
        v(*data, tag = tag, b = verbose, i = 1)
    }

    fun w(vararg data: Any?, e: Exception? = null) {
        w(*data, e = e, tag = tag, b = debug, i = 1)
    }

    fun w(e: Exception? = null, vararg data: Any?) {
        w(*data, e = e, tag = tag, b = debug, i = 1)
    }

    fun e(vararg data: Any?, e: Exception? = null) {
        e(*data, e = e, tag = tag, b = debug, i = 1)
    }

    fun e(e: Exception? = null, vararg data: Any?) {
        e(*data, e = e, tag = tag, b = debug, i = 1)
    }

    companion object {
        var DEBUG: Boolean = true
        var VERBOSE: Boolean = true
            set(value) {
                DEBUG = value || DEBUG
                field = value
            }
        private var TAG: String = "Logger"

        fun d(vararg data: Any?, tag: String = TAG, b: Boolean = DEBUG, i: Int = 0) {
            if (b && DEBUG) Log.d(tag, "\n" + createMsg(data, i))
        }

        fun i(vararg data: Any?, tag: String = TAG, b: Boolean = DEBUG, i: Int = 0) {
            if (b && DEBUG) Log.i(tag, "\n" + createMsg(data, i))
        }

        fun v(vararg data: Any?, tag: String = TAG, b: Boolean = VERBOSE, i: Int = 0) {
            if (b && VERBOSE) Log.v(tag, "\n" + createMsg(data, i))
            else Log.d(tag, "VERBOSE DISABLED")
        }

        fun w(
            vararg data: Any?, e: Exception? = null, tag: String = TAG, b: Boolean = DEBUG,
            i: Int = 0
        ) {
            if (b && DEBUG) Log.w(tag, "\n" + createMsg(data, i), e)
        }

        fun e(
            vararg data: Any?, e: Exception? = null, tag: String = TAG, b: Boolean = DEBUG,
            i: Int = 0
        ) {
            if (b && DEBUG) Log.w(tag, "\n" + createMsg(data, i), e)
        }

        private fun createMsg(data: Array<out Any?>, i: Int): String? =
            "\n" + getCaller(i) + "\n\t=> ${data.joinToString()}"

        private fun getCaller(i: Int): String {
            val stackTrace = Thread.currentThread().stackTrace

            return "[${stackTrace[i + 7].lineNumber}]${stackTrace[i + 7].methodName} -> " +
                    "[${stackTrace[i + 6].lineNumber}]${stackTrace[i + 6].methodName}->"
        }
    }
}
