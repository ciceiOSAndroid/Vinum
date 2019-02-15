package com.m600.alumnos.cice.vinum.calculatorActivity

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.LinearSnapHelper
import android.util.Log
import android.widget.Toast
import com.m600.alumnos.cice.vinum.R
import com.m600.alumnos.cice.vinum.calculatorActivity.tools.*
import kotlinx.android.synthetic.main.activity_calculator.*

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

    private var calculatorGlassAnimationController: CalculatorGlassAnimationController? = null

    private var botleAnimationAnimating = false
    private var currentRBState = RBAnimationState.NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        lightTypeface = ResourcesCompat.getFont(this, R.font.roboto_light)
        regularTypeFace = ResourcesCompat.getFont(this, R.font.roboto)

        bottleLottieView.enableMergePathsForKitKatAndAbove(true)

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

    override fun onSnapPesoPositionChange(position: Int) {
        peso = position + 40
        calculateAlcohol()
    }

    override fun onSnapPositionChange(position: Int) {
        glassSnapPosition = position
        bottleCounter = position / 7
        calculatorBottleCount.text = bottleCounter.toString()
        calculatorGlassCount.text = glassSnapPosition.toString()
        unfillBottle(position)
    }

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
                calculatorGlassAnimationController!!.idleAnimationGlass()
            }
        }
    }

    override fun send() {
        recyclerAdapter!!.updateAnimations()
    }

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
        val gradoAlcoholemiaAire = gradoAlcoholemiaSangre / 200
        calculatorAlcoholCounter.text = String.format("%1\$,.2f mg/l",gradoAlcoholemiaAire)

    }

    override fun onResume() {
        super.onResume()
        if (botleAnimationAnimating){
            bottleLottieView.resumeAnimation()
            botleAnimationAnimating = false
        }
        if (currentRBState != RBAnimationState.NONE){
            when(currentRBState){
                RBAnimationState.SELECT_MAN -> selectGenre(Genre.MAN)
                RBAnimationState.SELECT_WOMAN -> selectGenre(Genre.WOMAN)
                else -> true
            }
            currentRBState = RBAnimationState.NONE
        }

    }

    override fun onPause() {
        recyclerAdapter!!.cancelAnimations()
        if (calculatorWomanRB.isAnimating || calculatorManRB.isAnimating){
            when(selectGenre){
                Genre.WOMAN -> currentRBState = RBAnimationState.SELECT_WOMAN
                Genre.MAN -> currentRBState = RBAnimationState.SELECT_MAN
            }
            calculatorWomanRB.pauseAnimation()
            calculatorManRB.pauseAnimation()
        }
        if (bottleLottieView.isAnimating) {
            bottleLottieView.pauseAnimation()
            botleAnimationAnimating = true
        }
        super.onPause()
    }
}
