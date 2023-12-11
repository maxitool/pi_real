package com.example.pi.data;


public class TestTakerInformation {
    public String login = "";
    public String password = "";
    public int age = 0;
    public int area = 0;
    public int gender = 0;
    public boolean isSchoolchild = false;
    public boolean isStudent = false;

    public static String encodeToString(TestTakerInformation testTakerInfo) {
        String result = "";
        if (testTakerInfo.login != "")
            result += testTakerInfo.login + '|' + testTakerInfo.password + '|';
        result += Integer.toString(testTakerInfo.age) + '|' + Integer.toString(testTakerInfo.area) + '|' + Integer.toString(testTakerInfo.gender) + '|';
        if (testTakerInfo.isSchoolchild)
            result += "1|";
        else
            result += "0|";
        if (testTakerInfo.isStudent)
            result += "1";
        else
            result += "0";
        return result;
    }
    public static TestTakerInformation decodeFromString(String testTakerInfo) {
        TestTakerInformation result = new TestTakerInformation();
        String[] array = testTakerInfo.split("\\|", 0);
        if (array.length != 7)
            return result;
        result.login = array[0];
        result.password = array[1];
        result.age = Integer.parseInt(array[2]);
        result.area = Integer.parseInt(array[3]);
        result.gender = Integer.parseInt(array[4]);
        if (Integer.parseInt(array[5]) >= 1)
            result.isSchoolchild = true;
        else
            result.isSchoolchild = false;
        if (Integer.parseInt(array[6]) >= 1)
            result.isStudent = true;
        else
            result.isStudent = false;
        return result;
    }
}
