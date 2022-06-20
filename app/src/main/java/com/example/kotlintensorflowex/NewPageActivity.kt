package com.example.kotlintensorflowex

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import java.lang.Math.*
import kotlin.reflect.typeOf

class Complex(val re: Double, val im: Double) {
    operator infix fun plus(x: Complex) = Complex(re + x.re, im + x.im)
    operator infix fun minus(x: Complex) = Complex(re - x.re, im - x.im)
    operator infix fun times(x: Double) = Complex(re * x, im * x)
    operator infix fun times(x: Complex) = Complex(re * x.re - im * x.im, re * x.im + im * x.re)
    operator infix fun div(x: Double) = Complex(re / x, im / x)
    val exp: Complex by lazy { Complex(cos(im), sin(im)) * (cosh(re) + sinh(re)) }

    override fun toString() = when {
        b == "0.000" -> a
        a == "0.000" -> b + 'i'
        im > 0 -> a + " + " + b + 'i'
        else -> a + " - " + b + 'i'
    }

    private val a = "%1.3f".format(re)
    private val b = "%1.3f".format(abs(im))
}

object FFT {
    fun fft(a: Array<Complex>) = _fft(a, Complex(0.0, 2.0), 1.0)
    fun rfft(a: Array<Complex>) = _fft(a, Complex(0.0, -2.0), 2.0)

    private fun _fft(a: Array<Complex>, direction: Complex, scalar: Double): Array<Complex> =
        if (a.size == 1)
            a
        else {
            val n = a.size
            require(n % 2 == 0, { "The Cooley-Tukey FFT algorithm only works when the length of the input is even." })

            var (evens, odds) = Pair(emptyArray<Complex>(), emptyArray<Complex>())
            for (i in a.indices)
                if (i % 2 == 0) evens += a[i]
                else odds += a[i]
            evens = _fft(evens, direction, scalar)
            odds = _fft(odds, direction, scalar)

            val pairs = (0 until n / 2).map {
                val offset = (direction * (java.lang.Math.PI * it / n)).exp * odds[it] / scalar
                val base = evens[it] / scalar
                Pair(base + offset, base - offset)
            }
            var (left, right) = Pair(emptyArray<Complex>(), emptyArray<Complex>())
            for ((l, r) in pairs) { left += l; right += r }
            left + right
        }
}

class NewPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_page)

        //Back button
        val buttonClick = findViewById<Button>(R.id.backPageButton)
        buttonClick.setOnClickListener {
            onBackPressed()
        }

        //Primim valorile pasate din pagina precedenta
        var sampleuriFloats2 = FloatArray(32000)

        sampleuriFloats2 = intent.getSerializableExtra("key") as FloatArray

        println("Am primit valorile")
        var contor1 : Int = 0
        while(contor1 < 10)
        {
            println(sampleuriFloats2[contor1])
            contor1++
        }
        println("Gata sampleurile")

        var valoriDupaFFT = FloatArray(32000)

        ////////////////////////////////////////////////////////////////////////////////////////

        //Exemplu de fft
        val dataInputFFT = arrayOf(Complex(1.0, 0.0), Complex(1.0, 0.0), Complex(1.0, 0.0), Complex(1.0, 0.0),
            Complex(0.0, 0.0), Complex(0.0, 2.0), Complex(0.0, 0.0), Complex(0.0, 0.0))

       // println(dataInputFFT::class.java.typeName)
//
//        val rezFFT = FFT.fft(dataInputFFT)
//
//        println("Exemplu FFT")
//        var j=0
//        while(j<rezFFT.size){
//            println(rezFFT[j])
//            j++
//        }


        //TODO: esantioane -> FFT

        var vecInputFFT : Array<Complex> = arrayOf()
//
//        vecInputFFT += Complex (1.0,0.0)
//        vecInputFFT += Complex (1.0,0.0)
//        vecInputFFT += Complex (1.0,0.0)
//        vecInputFFT += Complex (1.0,0.0)
//        vecInputFFT += Complex (0.0,0.0)
//        vecInputFFT += Complex (0.0,2.0)
//        vecInputFFT += Complex (0.0,0.0)
//        vecInputFFT += Complex (0.0,0.0)

       // println(vecInputFFT::class.java.typeName)

       // println(vecInputFFT)

//        val rezFFT = FFT.fft(vecInputFFT)


        var contor : Int = 0
        while(contor < sampleuriFloats2.size) // adaugare din esantioane in lista de tip Complex
        {
//            println(sampleuriFloats2[contor])

            vecInputFFT += Complex(sampleuriFloats2[contor].toDouble(), 0.0)

            contor++
        }
        println("S-a terminat adaugarea esantioanelor in lista de tip <Complex>")

        //println(vecInputFFT.size)




//        val rezFFT = FFT.fft(vecInputFFT.slice(0..63).toTypedArray())
//
//        //println(rezFFT.size)
//
//        println("Output FFT")
//        var j=0
//        while(j<10){
//            println(rezFFT[j])
//            j++
//        }


        //TODO Canvas Spectrogram ----------------------------------------------------------------

        //variabilele pentru canvas si bitmap
        val imageSP = findViewById<View>(R.id.imageSpectrogram)

        val bitmapSP: Bitmap = Bitmap.createBitmap(500, 64, Bitmap.Config.ARGB_8888)
        val canvasSP: Canvas = Canvas(bitmapSP)

        val drawingListSP = mutableListOf<Float>()

        //TODO Desenam dreptunghiul care delimiteaza canvasul

        // first line starting point x y
        drawingListSP.add(0F) // x
        drawingListSP.add(0F) // y
        // first line ending point x y
        drawingListSP.add(canvasSP.width + 0F)
        drawingListSP.add(0F)

        drawingListSP.add(0F) // x
        drawingListSP.add(0F) // y
        drawingListSP.add(0F)
        drawingListSP.add(canvasSP.height + 0F)

        drawingListSP.add(0F) // x
        drawingListSP.add(canvasSP.height + 0F) // y
        drawingListSP.add(canvasSP.width + 0F)
        drawingListSP.add(canvasSP.height + 0F)

        drawingListSP.add(canvasSP.width + 0F) // x
        drawingListSP.add(0F) // y
        drawingListSP.add(canvasSP.width + 0F)
        drawingListSP.add(canvasSP.height + 0F)

        var paint = Paint().apply {
            color = Color.parseColor("#545AA7")
            strokeWidth = 5F
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.BUTT
            strokeMiter = 2F
        }

        canvasSP.drawLines(drawingListSP.toFloatArray(),paint)

        //Atributele de culoare/grosime ale punctelor ce vor fi reprezentate
        val paintSP = Paint().apply {
            color = Color.parseColor("#03fc35")
            strokeWidth = 1F
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.BUTT
            strokeMiter = 2F
        }

//        paintSP.color = Color.rgb(255,255,0)
//        canvasSP.drawPoint(120F, 32F,paintSP)
//
//        paintSP.color = Color.rgb(255,0,0)
//        canvasSP.drawPoint(121F, 32F,paintSP)


        //TODO de la FFT la Spectrograma
        //limitele stanga si dreapta pentru ferestre
        var sliceLeft : Int = 0
        var sliceRight : Int = 63
        var modulComplex : Float = 0F
        var nrReal : Float = 0F
        var procent : Float = 0f
        contor = 0
        var contorFFT : Int = 0
        var valoareGreen : Int = 0

        println("Am intrat in parcurgerea ferestrelor---------")
        while(contor<100) //TODO parcurgem toate cele 500 de ferestre a cate 64 esantioane
        {
            val rezFFT = FFT.fft(vecInputFFT.slice(sliceLeft..sliceRight).toTypedArray())

            //modulul numarului complex
            //modul = sqrt(real^2+im^2)
            //deoarece nu avem deloc parte imaginara, modulul maxim va fi sqrt(real^2)
            //rgb -> 255 -> 0, 0 -> 255


            contorFFT=0
            while(contorFFT<rezFFT.size)
            {
                nrReal = rezFFT[contorFFT].re.toFloat()
                modulComplex = kotlin.math.sqrt((nrReal * nrReal)) // in interval [0,1]

                //println(modulComplex)

                //normalizare culoare
//                procent = modulComplex*100 //cat la suta din 1 reprezinta modulul numarului complex
//
//                println(procent)
//
//                procent=100-procent
                valoareGreen = (modulComplex*255*25).toInt()

//                println(valoareGreen)

                paintSP.color = Color.rgb(255,valoareGreen,0)

                canvasSP.drawPoint(contor.toFloat(), contorFFT.toFloat(),paintSP)

                contorFFT++
            }


            //trecem la urmatoarea fereastra
            sliceLeft += 64
            sliceRight +=64
            contor++
        }

        //TODO Actualizam canvas
        imageSP.background = BitmapDrawable(getResources(), bitmapSP)
    }
}