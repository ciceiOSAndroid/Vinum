package com.m600.alumnos.cice.vinum.calculatorActivity

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.LinearSnapHelper
import com.airbnb.lottie.LottieAnimationView
import com.m600.alumnos.cice.vinum.R
import com.m600.alumnos.cice.vinum.calculatorActivity.tools.*
import kotlinx.android.synthetic.main.activity_calculator.*
import java.math.RoundingMode
import java.text.DecimalFormat

class CalculatorActivity : AppCompatActivity(),
    CalculatorGlassSnapOnScrollListener.OnSnapPositionChangeListener,
    CalculatorGlassAnimationController.CurrentFrameChange,
    CalculatorPesoSnapOnScrollListener.OnSnapPositionChangeListener{

    private val centilitrosCopa = 100
    private val graduacionVino = 12
    private val densidadAlcohol = 0.8

    private val maxBottleAnimationFrames = 70
    private val defaultBottleJumpAnimation = 10

    private var recyclerAdapter: CalculatorGlassRecyclerAdapter? = null
    private var recyclerPesoAdapter: CalculatorPesoRecyclerAdapter? = null
    private var linearLayout: LinearLayoutManager? = null
    private var pesoLinearLayout: LinearLayoutManager? = null
    private var smoothScroller: LinearSmoothScroller? = null

    private var glassSnapPosition = 0
    private var lastBottleQuantity = 0
    private var bottleCounter = 0
    private var lastBottleCounter = 0

    private var lightTypeface: Typeface? = null
    private var regularTypeFace: Typeface? = null

    enum class Genre{
        MAN, WOMAN
    }

    enum class RBAnimationState{
        NONE, SELECT_WOMAN, SELECT_MAN
    }

    private var selectGenre : Genre? = null
    private var peso: Int = 40
    private var lastAlcoholCounter = 0.0

    private var calculatorGlassAnimationController: CalculatorGlassAnimationController? = null

    private var botleAnimationAnimating = false
    private var currentGlassAnimationAnimating = false
    private var currentRBState = RBAnimationState.NONE

    private var alertCarController: Alert? = null
    private var alertAmbulanceController: Alert? = null
    private var alertWastedController: Alert? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        lightTypeface = ResourcesCompat.getFont(this, R.font.roboto_light)
        regularTypeFace = ResourcesCompat.getFont(this, R.font.roboto)

        bottleLottieView.enableMergePathsForKitKatAndAbove(true)
        alertCarController = Alert()
        alertAmbulanceController = Alert()
        alertWastedController = Alert()

        calculatorGlassAnimationController = CalculatorGlassAnimationController(this)
        linearLayout = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        pesoLinearLayout = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerAdapter = CalculatorGlassRecyclerAdapter(this)
        recyclerPesoAdapter = CalculatorPesoRecyclerAdapter(this)
        smoothScroller = object : LinearSmoothScroller(this){
            override fun getVerticalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_START
            }
        }

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(calculatorGlassRV)
        val pesoSnapHelper = LinearSnapHelper()
        pesoSnapHelper.attachToRecyclerView(calculatorPesoRV)

        calculatorGlassRV.layoutManager = linearLayout!!
        calculatorGlassRV.adapter = recyclerAdapter!!
        calculatorPesoRV.layoutManager = pesoLinearLayout!!
        calculatorPesoRV.adapter = recyclerPesoAdapter!!

        val snapOnScrollListener = CalculatorGlassSnapOnScrollListener(
            snapHelper,
            linearLayout!!,
            CalculatorGlassSnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
            this)
        val snapOnPesoScrollListener = CalculatorPesoSnapOnScrollListener(
            pesoSnapHelper,
            pesoLinearLayout!!,
            CalculatorPesoSnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
            this)

        calculatorPesoRV.addOnScrollListener(snapOnPesoScrollListener)
        calculatorGlassRV.addOnScrollListener(snapOnScrollListener)

        calculatorManHolder.setOnClickListener {
            selectGenre(Genre.MAN)
        }
        calculatorWomanHolder.setOnClickListener {
            selectGenre(Genre.WOMAN)
        }

        calculatorUpArrow.setOnClickListener {
            changePesoRVNotScrolling(false)
        }
        calculatorDownArrow.setOnClickListener {
            changePesoRVNotScrolling(true)
        }

    }

    // Activo el calculo de alcohol si el peso cambia
    override fun onSnapPesoPositionChange(position: Int) {
        peso = position + 40
        calculateAlcohol()
    }

    //Detecta cuando cambia el numero de copas tomadas
    override fun onSnapPositionChange(position: Int) {
        glassSnapPosition = position
        bottleCounter = position / 7
        calculatorBottleCount.text = bottleCounter.toString()
        calculatorGlassCount.text = glassSnapPosition.toString()
        unfillBottle(position)
    }

    //Detecta la direccion y el estado del scroll del recyclerView de las copas
    override fun sendScrollBehaviour(scrolling: Boolean, right: Boolean) {
        when(scrolling){
            true -> {
                val direction =
                    when(right){
                        true -> CalculatorGlassAnimationController.DirectionEnum.RIGHT
                        false -> CalculatorGlassAnimationController.DirectionEnum.LEFT
                    }
                calculatorGlassAnimationController!!.animateGlass(direction)
            }
            false -> {
                calculatorGlassAnimationController!!.idleAnimationGlass(false)
            }
        }
    }

    //Envia un aviso al recyclerview para que actualice sus animaciones, este metodo se activa al cambiar la animacion de las copas
    override fun send() {
        recyclerAdapter!!.updateAnimations()
    }

    //Gestiona la animacion de la botella de vino
    fun unfillBottle(position: Int) {
        bottleLottieView.clearAnimation()
        val bottleQuantity = position % 7
        val maxFrame: Int
        val minFrame: Int
        val speed: Float

        if (bottleQuantity > lastBottleQuantity) {
            maxFrame = (bottleQuantity) * defaultBottleJumpAnimation
            minFrame = (lastBottleQuantity) * defaultBottleJumpAnimation
            speed = 1.0f
        } else {
            maxFrame = lastBottleQuantity * defaultBottleJumpAnimation
            minFrame = (bottleQuantity) * defaultBottleJumpAnimation
            speed = -1.0f
        }

        lastBottleQuantity = bottleQuantity
        lastBottleCounter = bottleCounter


        bottleLottieView.speed = speed
        bottleLottieView.setMinAndMaxFrame(minFrame, maxFrame)
        bottleLottieView.playAnimation()

        calculateAlcohol()
    }

    //Gestiona la seleccion de sexo
    fun selectGenre(genre: Genre){
        if (currentRBState == RBAnimationState.NONE)
            if (genre == selectGenre) return
        when(genre){
            Genre.MAN -> {
                selectGenre?.let {
                    calculatorWomanRB.speed = -1.0f
                    calculatorWomanLabel.setTypeface(lightTypeface)
                    calculatorWomanRB.playAnimation()
                }
                calculatorManRB.speed = 1.0f
                calculatorManRB.playAnimation()
                calculatorManLabel.setTypeface(regularTypeFace)
                selectGenre = Genre.MAN
            }
            Genre.WOMAN ->{
                selectGenre?.let {
                    calculatorManRB.speed = -1.0f
                    calculatorManLabel.setTypeface(lightTypeface)
                    calculatorManRB.playAnimation()
                }
                calculatorWomanRB.speed = 1.0f
                calculatorWomanRB.playAnimation()
                calculatorWomanLabel.setTypeface(regularTypeFace)
                selectGenre = Genre.WOMAN
            }
        }
        calculateAlcohol()

    }

    //Permite cambiar el peso evitando el scroll del recyclerview, en caso de que se quiera hacerlo con clicks en los botones
    fun changePesoRVNotScrolling(up: Boolean){
        val actualPosition = peso - 40
        when (up){
            true -> {
                if (actualPosition > 99) return
                smoothScroller!!.targetPosition = actualPosition + 1
                calculatorPesoRV.layoutManager!!.startSmoothScroll(smoothScroller)
                peso = actualPosition + 41
            }
            false -> {
                if (actualPosition < 1) return
                smoothScroller!!.targetPosition = actualPosition - 1
                calculatorPesoRV.layoutManager!!.startSmoothScroll(smoothScroller)
                peso = actualPosition + 39
            }
        }
    }

    //Calcula el alcohol en aire
    fun calculateAlcohol(){
        if (selectGenre == null){
            val snackbar = Snackbar.make(CalculatorRoot,"Selecciona un genero",Snackbar.LENGTH_LONG)
            snackbar.setAction(getString(android.R.string.ok)){}
            snackbar.show()
            return
        }
        val gradosAlcoholPuro = glassSnapPosition * centilitrosCopa * graduacionVino * densidadAlcohol
        val constanteGenero = when(selectGenre){
            Genre.MAN ->{
                0.68
            }
            Genre.WOMAN ->{
                0.55
            }
            else -> null
        }
        val gradoAlcoholemiaSangre = gradosAlcoholPuro / ( peso * constanteGenero!!)
        var gradoAlcoholemiaAire = gradoAlcoholemiaSangre / 200
        val df= DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        gradoAlcoholemiaAire = df.format(gradoAlcoholemiaAire).toDouble()
        calculatorAlcoholCounter.text = String.format("%1\$,.2f mg/l",gradoAlcoholemiaAire)

        //La teoria tras esta comprovacion es asegurar que los iconos de alerta aparezcan y desaparezcan en orden, en
        //la practica ocurre tan deprisa que es casi imperceptible
        if (lastAlcoholCounter < gradoAlcoholemiaAire){
            manageAlerts(gradoAlcoholemiaAire, calculatorCarAlertView, alertCarController!!, 2, Pair(0.25, 0.0))
            manageAlerts(gradoAlcoholemiaAire, calculatorAmbulanceAlertView, alertAmbulanceController!!, 1, Pair(1.0, null))
            manageAlerts(gradoAlcoholemiaAire, calculatorWastedAnimationView, alertWastedController!!, 2, Pair(2.0, 1.5))
        } else {
            manageAlerts(gradoAlcoholemiaAire, calculatorWastedAnimationView, alertWastedController!!, 2, Pair(2.0, 1.5))
            manageAlerts(gradoAlcoholemiaAire, calculatorAmbulanceAlertView, alertAmbulanceController!!, 1, Pair(1.0, null))
            manageAlerts(gradoAlcoholemiaAire, calculatorCarAlertView, alertCarController!!, 2, Pair(0.25, 0.0))
        }

        lastAlcoholCounter = gradoAlcoholemiaAire

    }

    //Gestiona las animaciones de los 3 iconos de alerta
    private fun manageAlerts(nivelAlcolemia: Double, lottieView: LottieAnimationView, alertController: Alert, numberOfStates: Int, separator: Pair<Double, Double?>){
        var currentMaxFrame = 0
        var currentMinFrame = 0
        var currentSpeed = 1f
        val firstSeparator = separator.first
        var secondSeparator = separator.second

        secondSeparator?.let {
            if (it != 0.00){
                secondSeparator -= 0.01
            }
        }

        if (numberOfStates < 1 || numberOfStates > 2) return

        if (numberOfStates == 1){
            when {
                nivelAlcolemia >= firstSeparator ->{
                    when(alertController.lastState){
                        alerState.HIDE -> {
                            currentMaxFrame = 12
                            currentMinFrame = 0
                            currentSpeed = 1f
                        }
                        alerState.TOTAL -> {
                            return
                        }
                        else -> return
                    }
                    alertController.lastState = alerState.TOTAL
                }
                nivelAlcolemia < firstSeparator ->{
                    when(alertController.lastState){
                        alerState.HIDE ->{
                            return
                        }
                        alerState.TOTAL ->{
                            currentMaxFrame = 12
                            currentMinFrame = 0
                            currentSpeed = -1f
                        }
                        else -> return
                    }
                    alertController.lastState = alerState.HIDE
                }
            }
        } else {
            if (secondSeparator == null) return

            when {
                nivelAlcolemia >= firstSeparator -> {
                    when(alertController.lastState){
                        alerState.HIDE -> {
                            currentMaxFrame = 19
                            currentMinFrame = 0
                            currentSpeed = 1f
                        }
                        alerState.MEDIUM -> {
                            currentMaxFrame = 19
                            currentMinFrame = 12
                            currentSpeed = 1f
                        }
                        alerState.TOTAL -> {
                            return
                        }
                    }
                    alertController.lastState = alerState.TOTAL
                }
                nivelAlcolemia < firstSeparator && nivelAlcolemia > secondSeparator ->{
                    when(alertController.lastState){
                        alerState.HIDE ->{
                            currentMaxFrame = 12
                            currentMinFrame = 0
                            currentSpeed = 1f
                        }
                        alerState.MEDIUM ->{
                            return
                        }
                        alerState.TOTAL ->{
                            currentMaxFrame = 19
                            currentMinFrame = 12
                            currentSpeed = -1f
                        }
                    }
                    alertController.lastState = alerState.MEDIUM
                }
                nivelAlcolemia <= secondSeparator ->{
                    when(alertController.lastState){
                        alerState.HIDE ->{
                            return
                        }
                        alerState.MEDIUM ->{
                            currentMaxFrame = 12
                            currentMinFrame = 0
                            currentSpeed = -1f
                        }
                        alerState.TOTAL ->{
                            currentMaxFrame = 19
                            currentMinFrame = 0
                            currentSpeed = -1f
                        }
                    }
                    alertController.lastState = alerState.HIDE
                }
            }
        }

        lottieView.setMinAndMaxFrame(currentMinFrame, currentMaxFrame)
        lottieView.speed = currentSpeed
        lottieView.playAnimation()
        alertController.lastMaxFrame = currentMaxFrame
        alertController.lastMinFrame = currentMinFrame
    }

    //Define los 3 estados en los que pueden estar los iconos de alerta
    enum class alerState{
        HIDE, MEDIUM, TOTAL
    }

    //Define las propiedades de cada uno de los tres iconos de alerta
    internal class Alert{
        var lastMaxFrame = 0
        var lastMinFrame = 0
        var lastSpeed = 1f
        var lastState = alerState.HIDE
        var wasAnimatingBeforePause = false
    }

    //Se reanudan las animaciones que estaban activas cuando se pauso la activity
    override fun onResume() {
        super.onResume()
        if (botleAnimationAnimating){
            bottleLottieView.resumeAnimation()
            botleAnimationAnimating = false
        }
        if (currentGlassAnimationAnimating){
            calculatorGlassAnimationController!!.idleAnimationGlass(true)
            currentGlassAnimationAnimating = false
        }
        if (currentRBState != RBAnimationState.NONE){
            when(currentRBState){
                RBAnimationState.SELECT_MAN -> selectGenre(Genre.MAN)
                RBAnimationState.SELECT_WOMAN -> selectGenre(Genre.WOMAN)
                else -> true
            }
            currentRBState = RBAnimationState.NONE
        }
        if (alertCarController!!.wasAnimatingBeforePause){
            calculatorCarAlertView.playAnimation()
            alertCarController!!.wasAnimatingBeforePause = false
        }
        if (alertAmbulanceController!!.wasAnimatingBeforePause){
            calculatorAmbulanceAlertView.playAnimation()
            alertAmbulanceController!!.wasAnimatingBeforePause = false
        }
        if (alertWastedController!!.wasAnimatingBeforePause){
            calculatorWastedAnimationView.playAnimation()
            alertWastedController!!.wasAnimatingBeforePause = false
        }

    }

    //Se pausan todas las animaciones activas, esto es necesario para evitar posibles errores al enviar la activity al background
    //Podrian darse errores al cerrarla completamente, pero para evitar esto se implementa onDestroy
    override fun onPause() {
        if (recyclerAdapter!!.isCurrentPositionAnimationPlaying(glassSnapPosition)){
            currentGlassAnimationAnimating = true
        }
        recyclerAdapter!!.cancelAnimations(false)
        if (calculatorWomanRB.isAnimating || calculatorManRB.isAnimating){
            when(selectGenre){
                Genre.WOMAN -> currentRBState = RBAnimationState.SELECT_WOMAN
                Genre.MAN -> currentRBState = RBAnimationState.SELECT_MAN
            }
            calculatorWomanRB.pauseAnimation()
            calculatorManRB.pauseAnimation()
        }
        if (calculatorCarAlertView.isAnimating){
            calculatorCarAlertView.pauseAnimation()
            alertCarController!!.wasAnimatingBeforePause = true
        }
        if (calculatorAmbulanceAlertView.isAnimating){
            calculatorAmbulanceAlertView.pauseAnimation()
            alertAmbulanceController!!.wasAnimatingBeforePause = true
        }
        if (calculatorWastedAnimationView.isAnimating){
            calculatorWastedAnimationView.pauseAnimation()
            alertWastedController!!.wasAnimatingBeforePause = true
        }
        if (bottleLottieView.isAnimating) {
            bottleLottieView.pauseAnimation()
            botleAnimationAnimating = true
        }
        super.onPause()
    }

    //Se cancelan(destruyen) todas las animaciones, para evitar errores una vez la activity se cierre completamente
    override fun onDestroy() {
        recyclerAdapter!!.cancelAnimations(true)
        calculatorCarAlertView!!.cancelAnimation()
        calculatorWastedAnimationView!!.cancelAnimation()
        calculatorAmbulanceAlertView!!.cancelAnimation()
        bottleLottieView!!.cancelAnimation()
        super.onDestroy()
    }
}
