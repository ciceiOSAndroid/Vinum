package com.m600.alumnos.cice.vinum.calculatorActivity.tools

import com.airbnb.lottie.LottieAnimationView

class CalculatorGlassAnimationController(val changeListener: CurrentFrameChange) {

    interface CurrentFrameChange{
        fun send()
    }

    //Todas las animaciones de las copas son en realidad una "unica" instancia, para que todas realicen lo mismo
    //Esto puede ser ineficiente cuando solo se esta moviendo 1 dentro del circulo sin cambiar de copa (poco frecuente)
    //Pero es necesario para que al hacer scroll todas las demas copas tengan el mismo movimiento
    companion object {
        var currentFrame: Int = 0
        var currentMaxFrame: Int = 10
        var currentMinFrame: Int = 0
        var currentSpeed: Float = 1f
        var lastState: StateEnum = StateEnum.IDlE

        val MAX_RIGHT_FRAME = 3
        val MAX_LEFT_FRAME = 7
        val MAX_GLOBAL_FRAME = 11
        val MIN_GLOBAL_FRAME = 0

        fun syncWithFrame(lottieAnimationView: LottieAnimationView){
            lottieAnimationView.pauseAnimation()
            val frame = if (currentSpeed > 0f) currentMaxFrame else currentMinFrame
            lottieAnimationView.frame = frame
            lottieAnimationView.setMinAndMaxFrame(currentMinFrame, currentMaxFrame)
            lottieAnimationView.speed = currentSpeed
            lottieAnimationView.playAnimation()
        }
    }

    //Aqui se anima la copa en funcion de la direccion del scroll
    fun animateGlass(direction : DirectionEnum) {

        when (direction){
            DirectionEnum.RIGHT ->{
                if (lastState == StateEnum.SCROLLING_RIGHT) return

                when (lastState) {
                    StateEnum.IDlE -> {
                        currentMinFrame = MIN_GLOBAL_FRAME
                        currentMaxFrame = MAX_RIGHT_FRAME
                        currentSpeed = 1f
                    }
                    else -> {
                        currentMinFrame = MAX_RIGHT_FRAME
                        currentMaxFrame = MAX_LEFT_FRAME
                        currentSpeed = -1f
                    }
                }
                lastState = StateEnum.SCROLLING_RIGHT
            }
            DirectionEnum.LEFT -> {
                if (lastState == StateEnum.SCROLLING_LEFT) return
                when (lastState) {
                    StateEnum.IDlE -> {
                        currentMinFrame = MAX_LEFT_FRAME
                        currentMaxFrame = MAX_GLOBAL_FRAME
                        currentSpeed = -1f
                    }
                    else -> {
                        currentMinFrame = MAX_RIGHT_FRAME
                        currentMaxFrame = MAX_LEFT_FRAME
                        currentSpeed = 1f
                    }
                }
                lastState = StateEnum.SCROLLING_LEFT
            }
        }
        changeListener.send()
    }

    //Aqui la copa se anima de nuevo al estado de reposo
    fun idleAnimationGlass(resumingAnimation: Boolean){
        if (lastState == StateEnum.IDlE && !resumingAnimation) return
        when (lastState){
            StateEnum.SCROLLING_LEFT -> {
                currentMinFrame = MIN_GLOBAL_FRAME
                currentMaxFrame = MAX_LEFT_FRAME
                currentSpeed = -1f
            }
            else -> {
                currentMinFrame = MAX_RIGHT_FRAME
                currentMaxFrame = MAX_GLOBAL_FRAME
                currentSpeed = 1f
            }
        }
        lastState = StateEnum.IDlE
        currentFrame = 0
        changeListener.send()
    }

    enum class StateEnum{
        IDlE, SCROLLING_LEFT, SCROLLING_RIGHT
    }

    enum class DirectionEnum{
        RIGHT, LEFT
    }
}