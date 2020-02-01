package com.met.impilo.data.workouts

enum class Muscles (val nameRef : Int, val bodySide: BodySide) {
    PECTORALIS_MAJOR(com.met.impilo.R.string.pec_major, BodySide.FRONT),
    PECTORALIS_MINOR(com.met.impilo.R.string.pec_minor, BodySide.FRONT),
    FOREARMS(com.met.impilo.R.string.forearms, BodySide.FRONT),
    ABDOMINALS(com.met.impilo.R.string.abdominals, BodySide.FRONT),
    EXTERNAL_OBLIQUE(com.met.impilo.R.string.external_oblique, BodySide.FRONT),
    LATISSIMUS_DORSI(com.met.impilo.R.string.lats, BodySide.BACK),
    TRAPEZIUS(com.met.impilo.R.string.trapezius, BodySide.BACK),
    TERES_MAJOR(com.met.impilo.R.string.teres_major, BodySide.BACK),
    TERES_MINOR(com.met.impilo.R.string.teres_minor, BodySide.BACK),
    DELTOID_ANTERIOR(com.met.impilo.R.string.deltoid_anterior, BodySide.FRONT),
    DELTOID_MIDDLE(com.met.impilo.R.string.deltoid_middle, BodySide.FRONT),
    DELTOID_POSTERIOR(com.met.impilo.R.string.deltoid_posterior, BodySide.BACK),
    ERACTOR_SPINAE(com.met.impilo.R.string.eractor_spinae, BodySide.BACK),
    GLUTES(com.met.impilo.R.string.glutes, BodySide.BACK),
    BICEPS(com.met.impilo.R.string.biceps, BodySide.FRONT),
    SERRATUS_ANTERIOR(com.met.impilo.R.string.serratus_anterior, BodySide.FRONT),
    ADDUCTOR_MAGNUS(com.met.impilo.R.string.adductor_magnus, BodySide.BACK),
    ADDUCTOR_LONGUS(com.met.impilo.R.string.adductor_longus, BodySide.FRONT),
    QUADRICEPS(com.met.impilo.R.string.quadriceps, BodySide.FRONT),
    CALVES(com.met.impilo.R.string.calves, BodySide.BACK),
    BICEPS_FEMORIS(com.met.impilo.R.string.biceps_femoris, BodySide.BACK),
    TENSOR_FASCIAE_LATAE(com.met.impilo.R.string.tensor_fasciae_latae, BodySide.BACK),
    SEMITENDINOSUS(com.met.impilo.R.string.semitendinosus, BodySide.BACK),
    TRICEPS(com.met.impilo.R.string.triceps, BodySide.BACK),
    ALL_BODY(com.met.impilo.R.string.all_body, BodySide.FRONT),
    THIGHS(com.met.impilo.R.string.thighs, BodySide.FRONT),
    BACK(com.met.impilo.R.string.back_muscles, BodySide.BACK),
    ABS(com.met.impilo.R.string.abs, BodySide.FRONT)

}