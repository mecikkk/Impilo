package com.met.impilo.data

import com.met.impilo.data.workouts.BodySide
import com.met.impilo.utils.Const

enum class MusclesSet(val muscles: List<String>, val nameRef: Int, val bodySide: BodySide) {
    PECTORALIS_MAJOR(listOf(Const.PECTORALIS_MAJOR_LEFT, Const.PECTORALIS_MAJOR_RIGHT), com.met.impilo.R.string.pec_major, BodySide.FRONT),
    PECTORALIS_MINOR(listOf(), com.met.impilo.R.string.pec_minor, BodySide.FRONT),

    CHEST(listOf(Const.PECTORALIS_MAJOR_LEFT, Const.PECTORALIS_MAJOR_RIGHT), com.met.impilo.R.string.chest, BodySide.FRONT),

    BICEPS(listOf(Const.BICEPS_LONG_LEFT, Const.BICEPS_LONG_RIGHT, Const.BICEPS_SHORT_LEFT, Const.BICEPS_SHORT_RIGHT), com.met.impilo.R.string.biceps,
        BodySide.FRONT),
    TRICEPS(listOf(Const.TRICEPS_LATERAL_LEFT, Const.TRICEPS_LATERAL_RIGHT, Const.TRICEPS_LONG_LEFT, Const.TRICEPS_LONG_RIGHT, Const.TRICEPS_MEDIAL_LEFT,
        Const.TRICEPS_MEDIAL_RIGHT), com.met.impilo.R.string.triceps, BodySide.BACK),

    FOREARMS(listOf(Const.FOREARM_EXTERNAL_LEFT, Const.FOREARM_EXTERNAL_RIGHT, Const.FOREARM_INTERNAL_LEFT, Const.FOREARM_INTERNAL_RIGHT),
        com.met.impilo.R.string.forearms, BodySide.FRONT),

    FOREARM_BACK(listOf(Const.FOREARM_BRACHIORADIALIS_LEFT, Const.FOREARM_BRACHIORADIALIS_RIGHT, Const.FOREARM_EXTENSIR_LEFT, Const.FOREARM_EXTENSIR_RIGHT,
        Const.FOREARM_FLEXOR_LEFT, Const.FOREARM_FLEXOR_RIGHT, Const.FOREARM_REXOR_LEFT, Const.FOREARM_REXOR_RIGHT), com.met.impilo.R.string.forearms,
        BodySide.BACK),

    ABS(listOf("abs1", "abs2", "abs3", "abs4", "abs5", "abs6", "abs7", "abs8", Const.EXTERNAL_OBLIQUE_LEFT1, Const.EXTERNAL_OBLIQUE_LEFT2, Const.EXTERNAL_OBLIQUE_LEFT3,
        Const.EXTERNAL_OBLIQUE_LEFT4, Const.EXTERNAL_OBLIQUE_RIGHT1, Const.EXTERNAL_OBLIQUE_RIGHT2, Const.EXTERNAL_OBLIQUE_RIGHT3,
        Const.EXTERNAL_OBLIQUE_RIGHT4), com.met.impilo.R.string.abs, BodySide.FRONT),

    ABDOMINALS(listOf("abs1", "abs2", "abs3", "abs4", "abs5", "abs6", "abs7", "abs8"), com.met.impilo.R.string.abdominals, BodySide.FRONT),

    EXTERNAL_OBLIQUE(listOf(Const.EXTERNAL_OBLIQUE_LEFT1, Const.EXTERNAL_OBLIQUE_LEFT2, Const.EXTERNAL_OBLIQUE_LEFT3, Const.EXTERNAL_OBLIQUE_LEFT4,
        Const.EXTERNAL_OBLIQUE_RIGHT1, Const.EXTERNAL_OBLIQUE_RIGHT2, Const.EXTERNAL_OBLIQUE_RIGHT3, Const.EXTERNAL_OBLIQUE_RIGHT4),
        com.met.impilo.R.string.external_oblique, BodySide.FRONT),

    SHOULDERS_FRONT(listOf(Const.SHOULDER_ANTERIOR_LEFT, Const.SHOULDER_ANTERIOR_RIGHT, Const.SHOULDER_MIDDLE_LEFT, Const.SHOULDER_MIDDLE_RIGHT),
        com.met.impilo.R.string.shoulders, BodySide.FRONT),
    SHOULDERS_BACK(listOf(Const.SHOULDER_POSTERIOR_LEFT, Const.SHOULDER_POSTERIOR_RIGHT), com.met.impilo.R.string.shoulders, BodySide.BACK), DELTOID_ANTERIOR(
        listOf(Const.SHOULDER_ANTERIOR_LEFT, Const.SHOULDER_ANTERIOR_RIGHT), com.met.impilo.R.string.deltoid_anterior, BodySide.FRONT),
    DELTOID_MIDDLE(listOf(Const.SHOULDER_MIDDLE_LEFT, Const.SHOULDER_MIDDLE_RIGHT), com.met.impilo.R.string.deltoid_middle, BodySide.FRONT), DELTOID_POSTERIOR(
        listOf(Const.SHOULDER_POSTERIOR_LEFT, Const.SHOULDER_POSTERIOR_RIGHT), com.met.impilo.R.string.deltoid_posterior, BodySide.BACK),

    TRAPEZIUS(listOf(Const.TRAPEZIUS_LEFT, Const.TRAPEZIUS_RIGHT), com.met.impilo.R.string.trapezius, BodySide.BACK),

    LEGS(listOf("adductor_longus_left", "quadriceps1", "quadriceps2", "quadriceps3", "adductor_longus_right", "quadriceps4", "quadriceps5", "quadriceps6",
        "adductor_magnus_left", "adductor_magnus_right", "semitendinosus_left", "semitendinosus_right", "tensor_fasciae_latae_left", "tensor_fasciae_latae_right",
        "biceps_femoris_left", "biceps_femoris_right","calves1", "calves2", "calves3", "calves4", "calves5", "calves6", "calves7", "calves8"),
        com.met.impilo.R.string.legs, BodySide.FRONT),

    THIGHS(listOf("adductor_longus_left", "quadriceps1", "quadriceps2", "quadriceps3", "adductor_longus_right", "quadriceps4", "quadriceps5", "quadriceps6"),
        com.met.impilo.R.string.thighs, BodySide.FRONT),
    THIGHS_BACK(listOf("adductor_magnus_left", "adductor_magnus_right", "semitendinosus_left", "semitendinosus_right", "tensor_fasciae_latae_left", "tensor_fasciae_latae_right",
        "biceps_femoris_left", "biceps_femoris_right"), com.met.impilo.R.string.thighs, BodySide.BACK),

    ADDUCTOR_LONGUS(listOf("adductor_longus_left", "adductor_longus_right"), com.met.impilo.R.string.adductor_longus, BodySide.FRONT),
    ADDUCTOR_MAGNUS(listOf("adductor_magnus_left", "adductor_magnus_right"), com.met.impilo.R.string.adductor_magnus, BodySide.BACK),

    QUADRICEPS(listOf("quadriceps1", "quadriceps2", "quadriceps3", "quadriceps4", "quadriceps5", "quadriceps6"), com.met.impilo.R.string.quadriceps, BodySide.FRONT),
    BICEPS_FEMORIS(listOf("biceps_femoris_left", "biceps_femoris_right", "semitendinosus_left", "semitendinosus_right"), com.met.impilo.R.string.biceps_femoris, BodySide.BACK),

    SEMITENDINOSUS(listOf("semitendinosus_left", "semitendinosus_right"), com.met.impilo.R.string.semitendinosus, BodySide.BACK),

    TENSOR_FASCIAE_LATAE(listOf("tensor_fasciae_latae_left", "tensor_fasciae_latae_right"), com.met.impilo.R.string.tensor_fasciae_latae, BodySide.BACK),

    CALVES_FRONT(listOf("calves1", "calves2", "calves3", "calves4", "calves5", "calves6"), com.met.impilo.R.string.calves, BodySide.FRONT),
    CALVES(listOf("calves1", "calves2", "calves3", "calves4", "calves5", "calves6", "calves7", "calves8"), com.met.impilo.R.string.calves, BodySide.BACK),

    GLUTES(listOf(Const.GLUTE_LEFT, Const.GLUTE_RIGHT), com.met.impilo.R.string.glutes, BodySide.BACK),

    TERES(listOf(Const.TERES_MAJOR_LEFT, Const.TERES_MAJOR_RIGHT, Const.TERES_MINOR_LEFT, Const.TERES_MINOR_RIGHT), com.met.impilo.R.string.teres_major,
        BodySide.BACK),
    TERES_MAJOR(listOf(Const.TERES_MAJOR_LEFT, Const.TERES_MAJOR_RIGHT), com.met.impilo.R.string.teres_major, BodySide.BACK), TERES_MINOR(
        listOf(Const.TERES_MINOR_LEFT, Const.TERES_MINOR_RIGHT), com.met.impilo.R.string.teres_minor, BodySide.BACK),

    ERACTOR_SPINAE(listOf(Const.ERACTOR_SPINAE_LEFT, Const.ERACTOR_SPINAE_RIGHT), com.met.impilo.R.string.eractor_spinae, BodySide.BACK),
    LATISSIMUS_DORSI(listOf(Const.LATISSIMUS_DORSI_LEFT, Const.LATISSIMUS_DORSI_RIGHT), com.met.impilo.R.string.lats, BodySide.BACK),
    SERRATUS_ANTERIOR(
        listOf("serratus_anterior_left1", "serratus_anterior_left2", "serratus_anterior_left3", "serratus_anterior_right1", "serratus_anterior_right2", "serratus_anterior_right3"),
        com.met.impilo.R.string.serratus_anterior, BodySide.FRONT),

    BACK(listOf(Const.TERES_MAJOR_LEFT, Const.TERES_MAJOR_RIGHT, Const.TERES_MINOR_LEFT, Const.TERES_MINOR_RIGHT, Const.ERACTOR_SPINAE_LEFT,
        Const.ERACTOR_SPINAE_RIGHT, Const.LATISSIMUS_DORSI_LEFT, Const.LATISSIMUS_DORSI_RIGHT), com.met.impilo.R.string.back_muscles, BodySide.BACK),

    ALL_BODY(listOf(Const.PECTORALIS_MAJOR_LEFT, Const.PECTORALIS_MAJOR_RIGHT, Const.BICEPS_LONG_LEFT, Const.BICEPS_LONG_RIGHT, Const.BICEPS_SHORT_LEFT,
        Const.BICEPS_SHORT_RIGHT, Const.FOREARM_EXTERNAL_LEFT, Const.FOREARM_EXTERNAL_RIGHT, Const.FOREARM_INTERNAL_LEFT, Const.FOREARM_INTERNAL_RIGHT, "abs1",
        "abs2", "abs3", "abs4", "abs5", "abs6", "abs7", "abs8", Const.EXTERNAL_OBLIQUE_LEFT1, Const.EXTERNAL_OBLIQUE_LEFT2, Const.EXTERNAL_OBLIQUE_LEFT3,
        Const.EXTERNAL_OBLIQUE_LEFT4, Const.EXTERNAL_OBLIQUE_RIGHT1, Const.EXTERNAL_OBLIQUE_RIGHT2, Const.EXTERNAL_OBLIQUE_RIGHT3,
        Const.EXTERNAL_OBLIQUE_RIGHT4, Const.SHOULDER_ANTERIOR_LEFT, Const.SHOULDER_ANTERIOR_RIGHT, Const.SHOULDER_MIDDLE_LEFT, Const.SHOULDER_MIDDLE_RIGHT,
        "adductor_longus_left", "quadriceps1", "quadriceps2", "quadriceps3", "adductor_longus_right", "quadriceps4", "quadriceps5", "quadriceps6", "calves1", "calves2", "calves3",
        "calves4", "calves5", "calves6", Const.LATISSIMUS_DORSI_LEFT, Const.LATISSIMUS_DORSI_RIGHT, "serratus_anterior_left1", "serratus_anterior_left2",
        "serratus_anterior_left3", "serratus_anterior_right1", "serratus_anterior_right2", "serratus_anterior_right3"), com.met.impilo.R.string.all_body, BodySide.FRONT),

    UP(listOf(Const.PECTORALIS_MAJOR_LEFT, Const.PECTORALIS_MAJOR_RIGHT, Const.TERES_MAJOR_LEFT, Const.TERES_MAJOR_RIGHT, Const.TERES_MINOR_LEFT, Const.TERES_MINOR_RIGHT, Const.ERACTOR_SPINAE_LEFT,
        Const.ERACTOR_SPINAE_RIGHT, Const.LATISSIMUS_DORSI_LEFT, Const.LATISSIMUS_DORSI_RIGHT, Const.SHOULDER_ANTERIOR_LEFT, Const.SHOULDER_ANTERIOR_RIGHT, Const.SHOULDER_MIDDLE_LEFT, Const.SHOULDER_MIDDLE_RIGHT,
        Const.BICEPS_LONG_LEFT, Const.BICEPS_LONG_RIGHT, Const.BICEPS_SHORT_LEFT, Const.BICEPS_SHORT_RIGHT, Const.TRICEPS_LATERAL_LEFT, Const.TRICEPS_LATERAL_RIGHT, Const.TRICEPS_LONG_LEFT, Const.TRICEPS_LONG_RIGHT, Const.TRICEPS_MEDIAL_LEFT,
        Const.TRICEPS_MEDIAL_RIGHT), com.met.impilo.R.string.up_training_day, BodySide.FRONT),
    DOWN(listOf("adductor_longus_left", "quadriceps1", "quadriceps2", "quadriceps3", "adductor_longus_right", "quadriceps4", "quadriceps5", "quadriceps6",
        "adductor_magnus_left", "adductor_magnus_right", "semitendinosus_left", "semitendinosus_right", "tensor_fasciae_latae_left", "tensor_fasciae_latae_right",
        "biceps_femoris_left", "biceps_femoris_right","calves1", "calves2", "calves3", "calves4", "calves5", "calves6", "calves7", "calves8",Const.GLUTE_LEFT, Const.GLUTE_RIGHT), com.met.impilo.R.string.down_training_day, BodySide.FRONT),

    A(listOf("adductor_longus_left", "quadriceps1", "quadriceps2", "quadriceps3", "adductor_longus_right", "quadriceps4", "quadriceps5", "quadriceps6",
        Const.LATISSIMUS_DORSI_LEFT, Const.LATISSIMUS_DORSI_RIGHT,
        Const.PECTORALIS_MAJOR_LEFT, Const.PECTORALIS_MAJOR_RIGHT,
        Const.SHOULDER_ANTERIOR_LEFT, Const.SHOULDER_ANTERIOR_RIGHT, Const.SHOULDER_MIDDLE_LEFT, Const.SHOULDER_MIDDLE_RIGHT,
        Const.TRICEPS_LATERAL_LEFT, Const.TRICEPS_LATERAL_RIGHT, Const.TRICEPS_LONG_LEFT, Const.TRICEPS_LONG_RIGHT, Const.TRICEPS_MEDIAL_LEFT,
        Const.TRICEPS_MEDIAL_RIGHT, Const.BICEPS_LONG_LEFT, Const.BICEPS_LONG_RIGHT, Const.BICEPS_SHORT_LEFT, Const.BICEPS_SHORT_RIGHT,
        "abs1", "abs2", "abs3", "abs4", "abs5", "abs6", "abs7", "abs8", Const.EXTERNAL_OBLIQUE_LEFT1, Const.EXTERNAL_OBLIQUE_LEFT2, Const.EXTERNAL_OBLIQUE_LEFT3,
        Const.EXTERNAL_OBLIQUE_LEFT4, Const.EXTERNAL_OBLIQUE_RIGHT1, Const.EXTERNAL_OBLIQUE_RIGHT2, Const.EXTERNAL_OBLIQUE_RIGHT3,
        Const.EXTERNAL_OBLIQUE_RIGHT4), com.met.impilo.R.string.a_training_day, BodySide.FRONT),

    B(listOf("biceps_femoris_left", "biceps_femoris_right", "semitendinosus_left", "semitendinosus_right",Const.ERACTOR_SPINAE_LEFT, Const.ERACTOR_SPINAE_RIGHT,
        Const.PECTORALIS_MAJOR_LEFT, Const.PECTORALIS_MAJOR_RIGHT,Const.SHOULDER_POSTERIOR_LEFT, Const.SHOULDER_POSTERIOR_RIGHT,
        Const.SHOULDER_ANTERIOR_LEFT, Const.SHOULDER_ANTERIOR_RIGHT, Const.TRICEPS_LATERAL_LEFT, Const.TRICEPS_LATERAL_RIGHT, Const.TRICEPS_LONG_LEFT, Const.TRICEPS_LONG_RIGHT, Const.TRICEPS_MEDIAL_LEFT,
        Const.TRICEPS_MEDIAL_RIGHT, Const.BICEPS_LONG_LEFT, Const.BICEPS_LONG_RIGHT, Const.BICEPS_SHORT_LEFT, Const.BICEPS_SHORT_RIGHT,
        "abs1", "abs2", "abs3", "abs4", "abs5", "abs6", "abs7", "abs8", Const.EXTERNAL_OBLIQUE_LEFT1, Const.EXTERNAL_OBLIQUE_LEFT2, Const.EXTERNAL_OBLIQUE_LEFT3,
        Const.EXTERNAL_OBLIQUE_LEFT4, Const.EXTERNAL_OBLIQUE_RIGHT1, Const.EXTERNAL_OBLIQUE_RIGHT2, Const.EXTERNAL_OBLIQUE_RIGHT3,
        Const.EXTERNAL_OBLIQUE_RIGHT4), com.met.impilo.R.string.b_training_day, BodySide.FRONT),

    PUSH(listOf("adductor_longus_left", "quadriceps1", "quadriceps2", "quadriceps3", "adductor_longus_right", "quadriceps4", "quadriceps5", "quadriceps6",
        Const.PECTORALIS_MAJOR_LEFT, Const.PECTORALIS_MAJOR_RIGHT), com.met.impilo.R.string.push_training_day, BodySide.FRONT),
    PULL(listOf(), com.met.impilo.R.string.pull_training_day, BodySide.FRONT),
    BURN(listOf(), com.met.impilo.R.string.burning_training_day, BodySide.FRONT),



}