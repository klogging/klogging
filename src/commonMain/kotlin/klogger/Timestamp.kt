package klogger

data class Timestamp(val epochSeconds: Long, val nanos: Int) {
    override fun toString(): String {
        val ns = "000000000$nanos"
        return "$epochSeconds.${ns.substring(ns.length - 9)}"
    }
}