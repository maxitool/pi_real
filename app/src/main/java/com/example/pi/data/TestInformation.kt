package com.example.pi.data

data class TestInformation (
    var date: String,
    var countStages: Int,
    var dimension: Int,
    var rating: Float,
    var timeList: ArrayList<Long>,
    var errorsList: ArrayList<Int>
) {
    companion object {
        fun encodeToString(testInfo: TestInformation): String{
            var value: String = testInfo.date + '|' + testInfo.countStages.toString() + '|' +
                    testInfo.dimension.toString() + '|' + testInfo.rating.toString()
            for (i in 0..testInfo.timeList.size - 1) {
                value += '|' + testInfo.timeList.get(i).toString()
                value += '|' + testInfo.errorsList.get(i).toString()
            }
            return value
        }
        fun decodeFromString(stringTestInfo: String): TestInformation? {
            val stringList = stringTestInfo.split('|')
            if (stringList.size < 4)
                return null
            var testInfo = TestInformation(stringList[0], Integer.parseInt(stringList[1]), Integer.parseInt(stringList[2]),
                (stringList[3]).toFloat(), ArrayList(0), ArrayList(0))
            val dimension = Integer.parseInt(stringList[2])
            if (stringList.size != dimension * dimension * testInfo.countStages * 2 + 4) //two lists and first four parameters
                return null
            for (i in 0..testInfo.countStages - 1)
                for (j in 0..dimension - 1)
                    for (k in 0..dimension - 1) {
                        testInfo.timeList.add(stringList[(i * dimension * dimension + j * dimension + k) * 2 + 4].toLong())
                        testInfo.errorsList.add(Integer.parseInt(stringList[(i * dimension * dimension + j * dimension + k) * 2 + 1 + 4]))
                    }
            return testInfo
        }
        fun decodeArrayTaMFromString(info: String): TestInformation? { //TaM - Times and Mistakes
            val infoArray = info.split("|")
            if (infoArray.size / 2 * 2 != infoArray.size)
                return null
            var testInfo = TestInformation("", 0, 0, 0f, ArrayList(0), ArrayList(0))
            for (i in 0..infoArray.size / 2 - 1) {
                testInfo.timeList.add(infoArray[i * 2].toLong())
                testInfo.errorsList.add(infoArray[i * 2 + 1].toInt())
            }
            return testInfo
        }
        fun testInfoPlusResultTest(testInfo: TestInformation?, resultTest: ResultTest) : TestInformation? {
            if (testInfo == null || resultTest == null)
                return null
            var result = testInfo
            result.date = resultTest.date
            result.countStages = resultTest.countStages
            result.dimension = resultTest.dimension
            result.rating = resultTest.rating
            return result
        }
        fun decodeResultsArrayFromString(info: String) {

        }
    }
}