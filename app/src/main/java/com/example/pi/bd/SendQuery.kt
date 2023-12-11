package com.example.pi.bd

import com.example.pi.data.ResultTest
import com.example.pi.data.TestInformation
import com.example.pi.data.TestTakerInformation
import java.util.concurrent.CountDownLatch

class SendQuery {
    companion object {
        //interface for work with BD
        const val WriteAccount = "WriteAccount"
        const val WriteResults = "WriteResults"
        const val ReadAccountsFromQuery = "ReadAccountsFromQuery"
        const val ReadAreas = "ReadAreas"
        const val ReadAccountInfo = "ReadAccountInfo"
        const val ReadTestsResults = "ReadTestsResults"
        const val ReadElResults = "ReadElResults"
        const val ReadLastAnonymous = "ReadLastAnonymous"
        const val CheckAccountData = "CheckAccountData"
        const val UpdateAccountData = "UpdateAccountData"

        fun getAreas() : List<String> {
            val countDownLatch = CountDownLatch(1)
            val sendToServer = SendToServer(countDownLatch, ReadAreas + "::")
            countDownLatch.await()
            return sendToServer.answer.split('|')
        }
        fun addUser(testTakerInfo: TestTakerInformation) : Int {
            if (testTakerInfo.login == "") {
                val countDownLatch = CountDownLatch(1)
                val check = TestTakerInformation.encodeToString(testTakerInfo)
                val sendToServer = SendToServer(countDownLatch, ReadLastAnonymous + "::")
                countDownLatch.await()
                val number = sendToServer.answer.toInt()
                testTakerInfo.login = "Anonymous" + (number + 1).toString()
            }
            val countDownLatch = CountDownLatch(1)
            val check = TestTakerInformation.encodeToString(testTakerInfo)
            val sendToServer = SendToServer(countDownLatch, WriteAccount + "::" + TestTakerInformation.encodeToString(testTakerInfo))
            countDownLatch.await()
            val hm = sendToServer.answer
            return Integer.parseInt(sendToServer.answer)
        }
        fun checkAccountData(login: String, password: String) : Int {
            val countDownLatch = CountDownLatch(1)
            val sendToServer = SendToServer(countDownLatch, CheckAccountData + "::" + login + '|' + password)
            countDownLatch.await()
            if (sendToServer.answer == "")
                return -1;
            return sendToServer.answer.toInt()
        }

        fun saveResults(mID: String?, testInfo: TestInformation) : Int{
            if (mID == null)
                return -7
            val countDownLatch = CountDownLatch(1)
            val sendToServer = SendToServer(countDownLatch, WriteResults + "::" + mID + '|' + TestInformation.encodeToString(testInfo))
            countDownLatch.await()
            return sendToServer.answer.toInt()
        }

        fun getUserInfoFromID(mID: String?) : TestTakerInformation? {
            if (mID == null)
                return null
            val countDownLatch = CountDownLatch(1)
            val sendToServer = SendToServer(countDownLatch, ReadAccountInfo + "::" + mID)
            countDownLatch.await()
            val info = sendToServer.answer
            if (info.split("|").size <= 1)
                return null
            return TestTakerInformation.decodeFromString(info)
        }

        fun getResults(mID: String?) : ArrayList<ResultTest>?{
            if (mID == null)
                return null
            val countDownLatch = CountDownLatch(1)
            val sendToServer = SendToServer(countDownLatch, ReadTestsResults + "::" + mID)
            countDownLatch.await()
            val a = sendToServer.answer
            if (a == "")
                return null
            return ResultTest.decodeArrayFromString(sendToServer.answer)
        }

        fun getTimesAndMistakes(testID: Int) : TestInformation? {
            val countDownLatch = CountDownLatch(1)
            val sendToServer = SendToServer(countDownLatch, ReadElResults + "::" + testID)
            countDownLatch.await()
            return TestInformation.decodeArrayTaMFromString(sendToServer.answer)
        }

        fun UpdateAccountInfo(mID: String?, userInfo: TestTakerInformation?) : Int? {
            if (mID == null || userInfo == null)
                return null
            val countDownLatch = CountDownLatch(1)
            val sendToServer = SendToServer(countDownLatch, UpdateAccountData + "::" + mID + '|' + TestTakerInformation.encodeToString(userInfo))
            countDownLatch.await()
            return sendToServer.answer.toInt()
        }
    }
}