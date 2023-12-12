package com.example.pi.calculations

class Calculates {
    companion object {
        const val MAX_RATING = 5
        const val MIN_RATING = 1
        const val FASTEST_TIME = 1.2f
        const val SLOWEST_TIME = 2.2f
        const val PRICE_OF_MISTAKE = 0.1f
        const val MILLISECONDS = 1000

        fun calculateRating(countStages: Int?, listTimes: ArrayList<Long>?, listMistakes: ArrayList<Int>?) : Float {
            if (countStages == null || listTimes == null || listMistakes == null)
                return 0f
            val sizeTable = listTimes.size / countStages
            val er = calculateER(countStages, listTimes)
            if (er >= SLOWEST_TIME * sizeTable)
                return MIN_RATING.toFloat()
            var countErrors = 0
            for (i in 0..listMistakes.size - 1)
                countErrors += listMistakes[i]
            if (er <= FASTEST_TIME * sizeTable)
                return MAX_RATING.toFloat() - countErrors * PRICE_OF_MISTAKE
            var rating = MAX_RATING - (er - FASTEST_TIME * sizeTable) *
                    (MAX_RATING - MIN_RATING) / ((SLOWEST_TIME - FASTEST_TIME) * sizeTable + 1) -
                    countErrors * PRICE_OF_MISTAKE;
            if (rating <= MIN_RATING)
                return MIN_RATING.toFloat()
            return rating
        }
        fun calculateRating(ER: Int?, listMistakes: ArrayList<Int>?) : Float {
            if (ER == null || listMistakes == null)
                return 0f
            var countErrors = 0
            if (ER >= SLOWEST_TIME)
                return MIN_RATING.toFloat()
            for (i in 0..listMistakes.size - 1)
                countErrors += listMistakes[i]
            if (ER <= FASTEST_TIME)
                return MAX_RATING.toFloat() - countErrors * PRICE_OF_MISTAKE
            var rating = MAX_RATING - (ER - MIN_RATING) *
                    (MAX_RATING - MIN_RATING) / (SLOWEST_TIME - FASTEST_TIME + 1) -
                    countErrors * PRICE_OF_MISTAKE;
            if (rating <= MIN_RATING)
                return MIN_RATING.toFloat()
            return rating
        }
        fun calculateER(countStages: Int?, listTimes: ArrayList<Long>?) : Float {
            if (countStages == null || listTimes == null)
                return 0f
            var time = 0f
            var count = 0
            for (i in 0..countStages - 1) {
                for (j in 0..(listTimes.size / countStages - 1))
                    time += listTimes[i * listTimes.size / countStages + j]
                count++
            }
            return time / MILLISECONDS / count
        }
        fun calculateVR(ER: Float?, sizeTable: Int?, listTimes: ArrayList<Long>?) : Float {
            if (ER == null || sizeTable == null || listTimes == null)
                return 0f
            if (sizeTable * sizeTable > listTimes.size)
                return 0f
            var time = 0f
            for (i in 0..sizeTable * sizeTable- 1)
                time += listTimes[i]
            return time / MILLISECONDS / ER
        }
        fun calculatePU(ER: Float?, countStages: Int?, listTimes: ArrayList<Long>?) : Float {
            if (ER == null || countStages == null || listTimes == null)
                return 0f
            var currentStage = 0
            if (countStages == 2)
                currentStage = 1
            if (countStages > 2)
                currentStage = countStages - 2
            val sizeTable = listTimes.size / countStages
            var time = 0f
            for (i in currentStage * sizeTable..(currentStage * sizeTable + sizeTable - 1))
                time += listTimes[i]
            return time / MILLISECONDS / ER
        }
    }
}