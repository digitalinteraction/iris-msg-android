package uk.ac.ncl.openlab.irismsg.common

import java.util.*

/**
 * Provides a method to format a time as '5y 7d 31m'
 */
class DateUtils {
    companion object {
    
        private const val MILLI_IN_SECOND: Long = 1000
        private const val MILLI_IN_MINUTE: Long = MILLI_IN_SECOND * 60
        private const val MILLI_IN_HOUR: Long =   MILLI_IN_MINUTE * 60
        private const val MILLI_IN_DAY: Long =    MILLI_IN_HOUR * 24
        private const val MILLI_IN_WEEK: Long =   MILLI_IN_DAY * 7
        private val MILLI_IN_YEAR: Long =         Math.round(MILLI_IN_WEEK * 365.25)
        
        // Add a unit to the string if it is non-zero
        private fun addUnit (
            time: Long,
            unit: Long,
            comps: MutableList<String>,
            letter: String,
            ignoreUnit: Long = Long.MAX_VALUE
        ) {
            val subComp = ((time % ignoreUnit) / unit)
            if (subComp > 0) comps.add("$subComp$letter")
        }
        
        fun timeSince (date: Date, format: Boolean = false, largestOnly: Boolean = false) : String {
            val target = date.time
            val now = Calendar.getInstance().time.time
            
            val diff = Math.abs(now - target)
            
            val comps = mutableListOf<String>()
            
            
            addUnit(diff, MILLI_IN_YEAR, comps, "y")
            addUnit(diff, MILLI_IN_WEEK, comps, "w", MILLI_IN_YEAR)
            addUnit(diff, MILLI_IN_DAY, comps, "d", MILLI_IN_WEEK)
            addUnit(diff, MILLI_IN_HOUR, comps, "h", MILLI_IN_DAY)
            addUnit(diff, MILLI_IN_MINUTE, comps, "m", MILLI_IN_HOUR)
            addUnit(diff, MILLI_IN_SECOND, comps, "s", MILLI_IN_MINUTE)
            
            if (largestOnly && comps.size > 0) {
                comps.retainAll(listOf(comps.first()))
            }
            
            val value = comps.joinToString(" ")
            
            return if (format) {
                if (now < target) "in $value" else "$value ago"
            } else value
        }
        
    }
}