package com.example.pi;

import com.example.pi.data.TestTakerInformation;

//old!! partially using

public class Transformations {
    public static boolean IntegerToBoolean(int number) {if (number == 0) return false; return true;}
    public static int BooleanToInteger(boolean number) {if (number) return 1; return 0;}
    public static String[] TestTakerInfoToArray(TestTakerInformation testTakerInfo) {
        String[] array = new String[5];
        array[0] = Integer.toString(testTakerInfo.age);
        array[1] = Integer.toString(testTakerInfo.area);
        array[2] = Integer.toString(testTakerInfo.gender);
        array[3] = Boolean.toString(testTakerInfo.isSchoolchild);
        array[4] = Boolean.toString(testTakerInfo.isStudent);
        return array;
    }
    public static TestTakerInformation ArrayToTestTakerInfo(String[] array) {
        TestTakerInformation testTakerInfo = new TestTakerInformation();
        if (array.length != 5)
            return testTakerInfo;
        testTakerInfo.age = Integer.parseInt(array[0]);
        testTakerInfo.area = Integer.parseInt(array[1]);
        testTakerInfo.gender = Integer.parseInt(array[2]);
        testTakerInfo.isSchoolchild = Boolean.parseBoolean(array[3]);
        testTakerInfo.isStudent = Boolean.parseBoolean(array[4]);
        return testTakerInfo;
    }
}
