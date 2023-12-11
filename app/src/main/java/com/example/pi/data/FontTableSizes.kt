package com.example.pi.data

class FontTableSizes {
    companion object{
        fun getFontSize(dimensionSize: Int): Float {
            when(dimensionSize) {
                3 -> return 75f
                4 -> return 60f
                5 -> return 49f
                6 -> return 35f
                7 -> return 28f
                8 -> return 25f
                9 -> return 20f
                10 -> return 13f
                else -> return 0f
            }
        }
    }
}