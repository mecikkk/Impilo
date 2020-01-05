package com.met.impilo.data

import com.met.impilo.utils.Constants

enum class MusclesSet(val muscles : List<String>) {
    CHEST(listOf(Constants.CHEST_LEFT, Constants.CHEST_RIGHT)),
    BICEPS(listOf(Constants.BICEPS_LONG_LEFT, Constants.BICEPS_LONG_RIGHT, Constants.BICEPS_SHORT_LEFT, Constants.BICEPS_SHORT_RIGHT)),
    TRICEPS(listOf(Constants.TRICEPS_LATERAL_LEFT, Constants.TRICEPS_LATERAL_RIGHT, Constants.TRICEPS_LONG_LEFT, Constants.TRICEPS_LONG_RIGHT, Constants.TRICEPS_MEDIAL_LEFT, Constants.TRICEPS_MEDIAL_RIGHT)),
    FOREARM_FRONT(listOf(Constants.FOREARM_EXTERNAL_LEFT, Constants.FOREARM_EXTERNAL_RIGHT, Constants.FOREARM_INTERNAL_LEFT, Constants.FOREARM_INTERNAL_RIGHT)),
    FOREARM_BACK(listOf(Constants.FOREARM_BRACHIORADIALIS_LEFT, Constants.FOREARM_BRACHIORADIALIS_RIGHT, Constants.FOREARM_EXTENSIR_LEFT, Constants.FOREARM_EXTENSIR_RIGHT,
        Constants.FOREARM_FLEXOR_LEFT, Constants.FOREARM_FLEXOR_RIGHT, Constants.FOREARM_REXOR_LEFT, Constants.FOREARM_REXOR_RIGHT)),
    ABS(listOf("abs1", "abs2", "abs3", "abs4", "abs5", "abs6", "abs7", "abs8")),
    EXTERNAL_OBLIQUE(listOf(Constants.EXTERNAL_OBLIQUE_LEFT, Constants.EXTERNAL_OBLIQUE_RIGHT)),
    SHOULDERS_FRONT(listOf(Constants.SHOULDER_ANTERIOR_LEFT, Constants.SHOULDER_ANTERIOR_RIGHT, Constants.SHOULDER_MIDDLE_LEFT, Constants.SHOULDER_MIDDLE_RIGHT)),
    SHOULDERS_BACK(listOf(Constants.SHOULDER_POSTERIOR_LEFT, Constants.SHOULDER_POSTERIOR_RIGHT)),
    TRAPEZIUS(listOf(Constants.TRAPEZIUS_LEFT, Constants.TRAPEZIUS_RIGHT)),
    THIGHS(listOf("thighs1", "thighs2", "thighs3", "thighs4", "thighs5", "thighs6", "thighs7", "thighs8")),
    CALVES_FRONT(listOf("calves1", "calves2", "calves3", "calves4", "calves5", "calves6")),
    CALVES_BACK(listOf("calves1", "calves2", "calves3", "calves4", "calves5", "calves6", "calves7", "calves8")),
    GLUTES(listOf(Constants.GLUTE_LEFT, Constants.GLUTE_RIGHT)),
    TERES(listOf(Constants.TERES_MAJOR_LEFT, Constants.TERES_MAJOR_RIGHT, Constants.TERES_MINOR_LEFT, Constants.TERES_MINOR_RIGHT)),
    ERACTOR_SPINAE(listOf(Constants.ERACTOR_SPINAE_LEFT, Constants.ERACTOR_SPINAE_RIGHT)),
    LATISSIMUS_DORSI(listOf(Constants.LATISSIMUS_DORSI_LEFT, Constants.LATISSIMUS_DORSI_RIGHT)),
    SERRATUS_ANTERIOR(listOf("serratus_anterior1", "serratus_anterior2", "serratus_anterior3", "serratus_anterior4","serratus_anterior5", "serratus_anterior6", "serratus_anterior7", "serratus_anterior8"))
}