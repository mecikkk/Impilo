package com.met.impilo.data.workouts.generator

enum class WorkoutType(val nameRef : String, val titleRef : Int, val days : Int) {

    THREE_DAY_FAT_LOSS("3_days_fat_loss", com.met.impilo.R.string.three_days_fat_loss, 3),
    THREE_DAY_SPLIT("3_days_split", com.met.impilo.R.string.three_days_split,3),
    THREE_DAY_FBW("3_days_fbw", com.met.impilo.R.string.fbw,2),
    FOUR_DAY_PUSH_PULL("4_days_push_pull", com.met.impilo.R.string.four_day_push_pull, 4),
    FOUR_DAY_UP_DOWN("4_days_up_down", com.met.impilo.R.string.four_day_up_down, 4),
    FIVE_DAY_SPLIT("5_days_split", com.met.impilo.R.string.five_day_up_down,5)

}