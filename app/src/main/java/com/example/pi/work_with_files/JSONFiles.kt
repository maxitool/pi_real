package com.example.pi.work_with_files

import android.content.Context
import android.util.JsonWriter
import android.util.Log
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class JSONFiles {
    companion object{

        fun createJSONFile(context: Context, fileName: String) {
            try {
                val outputStreamWriter = OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
                outputStreamWriter.write("""{"login":"", "password":""}""")
                outputStreamWriter.close()
            }
            catch (e: IOException) {
                Log.e("Exception", "File write failed: " + e.toString())
            }
        }

        fun readFromFile(context: Context, fileName: String): String {
            var ret: String = ""
            lateinit var inputStream: InputStream
            try {
                inputStream = context.openFileInput(fileName)
                if ( inputStream != null ) {
                    val inputStreamReader = InputStreamReader(inputStream)
                    var bufferedReader = BufferedReader(inputStreamReader)
                    val stringBuilder = StringBuilder()
                    var receiveString: String? = bufferedReader.readLine()

                    while ( receiveString != null ) {
                        stringBuilder.append(receiveString)
                        receiveString = bufferedReader.readLine()
                    }

                    ret = stringBuilder.toString();
                }
            }
            catch (e: FileNotFoundException) {
                Log.e("login activity", "File not found: " + e.toString());
            } catch (e: IOException) {
                Log.e("login activity", "Can not read file: " + e.toString());
            }
            finally {
                try {
                    inputStream.close();
                } catch (e: IOException) {
                    e.printStackTrace();
                }
            }
            return ret;
        }

        fun deleteFile(context: Context, fileName: String) {
            context.deleteFile(fileName)
        }

        fun WriteIntoJSON(context: Context, fileName: String, login: String, password: String) {
            try {
                val jsonWrite = JsonWriter(OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE)))
                jsonWrite.beginObject()
                jsonWrite.name("login").value(login)
                jsonWrite.name("password").value(password)
                jsonWrite.endObject()
                jsonWrite.close()
            }
            catch (e :IOException) {
                e.printStackTrace()
            }
        }
    }
}