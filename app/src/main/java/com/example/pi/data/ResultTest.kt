package com.example.pi.data

data class ResultTest(
    var testId: Int,
    var date: String,
    var countStages: Int,
    var dimension: Int,
    var rating: Float,
) {
    companion object {
        const val COUNT_FIELDS = 5

        public fun decodeArrayFromString(info: String): ArrayList<ResultTest>? {
            val infoArray = info.split("|")
            if (infoArray.size == 0)
                return null
            val countResults = (infoArray.size - 1) / COUNT_FIELDS
            if (infoArray[0].toInt() != countResults)
                return null
            var resultsArray = ArrayList<ResultTest>()
            for (i in 0..countResults - 1) {
                var a = 0
                if (i == countResults - 1)
                    a = 1
                var result = ResultTest(infoArray[i * COUNT_FIELDS + 1].toInt(), infoArray[i * COUNT_FIELDS + 2], infoArray[i * COUNT_FIELDS + 3].toInt(),
                    infoArray[i * COUNT_FIELDS + 4].toInt(), stringToFloat(infoArray[i * COUNT_FIELDS + 5]))
                resultsArray.add(result)
            }
            return resultsArray
        }
        fun stringToFloat(value: String) : Float {
            var result: String = value
            if (value.indexOf(",") >= 0) {
                val array = value.split(",")
                result = array[0]
                if (array[1].length > 1)
                    result += '.' + array[1][0].toString() + array[1][1].toString()
                else
                    result += '.' + array[1][0].toString()
            }
            if (value.indexOf(".") >= 0) {
                val array = value.split(".")
                result = array[0]
                if (array[1].length > 1)
                    result += '.' + array[1][0].toString() + array[1][1].toString()
                else
                    result += '.' + array[1][0].toString()
            }
            return result.toFloat()
        }
    }
}
