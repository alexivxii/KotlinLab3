package com.example.kotlintensorflowex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.util.Log
import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import org.tensorflow.lite.task.audio.classifier.AudioClassifier

class MainActivity : AppCompatActivity() {

    // TODO 2.1: defines the model to be used
    var modelPath = "lite-model_yamnet_classification_tflite_1.tflite"

    // TODO 2.2: defining the minimum threshold
    var probabilityThreshold: Float = 0.3f

    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 100)

        }
        else
        {
            println("PERMISSION ALREADY GRANTED!")
        }


        textView = findViewById<TextView>(R.id.output)
        val recorderSpecsTextView = findViewById<TextView>(R.id.textViewAudioRecorderSpecs)

        // TODO 2.3: Loading the model from the assets folder
        val classifier = AudioClassifier.createFromFile(this, modelPath)

        // TODO 3.1: Creating an audio recorder
        val tensor = classifier.createInputTensorAudio()

        // TODO 3.2: showing the audio recorder specification
        val format = classifier.requiredTensorAudioFormat
        //println("Classifier buffer size: " + classifier.requiredInputBufferSize) //15600 este output-ul (number of floats)
        val recorderSpecs = "Number Of Channels: ${format.channels}\n" + "Sample Rate: ${format.sampleRate}"
        recorderSpecsTextView.text = recorderSpecs
        //output: 1 channel, 16000 sample rate

        // TODO 3.3: Creating
        val record = classifier.createAudioRecord()
        record.startRecording()

        //TODO incercare accesare sampleuri : initializare var
        var recorded : Int = 0

        //var bufferSizeInBytes2 = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        var bufferSizeInFloats2 = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT)

        var sampleuriFloats = FloatArray(15600)
       // var sampleuriByte = ByteArray(bufferSizeInBytes2)
        var lengthAudioRecFloat : Int = 0
//        var lengthAudioRecBytes : Int = 0

        println("Buffer size in Floats:")
        println(bufferSizeInFloats2) //2560
        //println("Buffer size in Bytes:" + bufferSizeInBytes2) //1280
//        val record2AudioRecordBytes = AudioRecord(MediaRecorder.AudioSource.MIC,16000,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,bufferSizeInBytes2)
//        record2AudioRecordBytes.startRecording()

        val record2AudioRecordFloats = AudioRecord(MediaRecorder.AudioSource.MIC,16000,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_FLOAT,bufferSizeInFloats2)
        record2AudioRecordFloats.startRecording()

        //TODO Tensorflow classifier
        var sampleuriClassifierRecording = FloatArray(15600)
        var length : Int = 0


        Timer().scheduleAtFixedRate(1, 500) {
            //TODO Important: la 1000ms 15600 floats, la 500ms cam 8000 floats, la 900ms cam 14400 floats, la 250ms cam 4160 floats


            //TODO incercare accesare sampleuri : stocare cu functia read
            if(recorded==5)
            {


                lengthAudioRecFloat = record2AudioRecordFloats.read(sampleuriFloats,0,15600,AudioRecord.READ_NON_BLOCKING)
//                lengthAudioRecBytes = record2AudioRecordBytes.read(sampleuriByte,0,bufferSizeInBytes2)
                println("Length2 Floats Record Read")
                println(lengthAudioRecFloat)
//                println("Length3 Bytes Record Read")
//                println(lengthAudioRecBytes)

                length = record.read(sampleuriClassifierRecording,0,15600,AudioRecord.READ_NON_BLOCKING)
                println("Length Classifier Record Read")
                println(length)

//                var contor : Int = 0
//                while(contor < 200)
//                {
//                    println(sampleuriClassifierRecording[contor])
//                    contor++
//                }
//                println("Gata sampleurile")

//                record2AudioRecordBytes.stop()
//                record2AudioRecordBytes.release()
                record2AudioRecordFloats.stop()
                record2AudioRecordFloats.release()

                recorded++
            }
            else
            {
                recorded++
            }




            // TODO 4.1: Classifing audio data
            val numberOfSamples = tensor.load(record)
            val output = classifier.classify(tensor)

            // TODO 4.2: Filtering out classifications with low probability
             val filteredModelOutput = output[0].categories.filter {
                 it.score > probabilityThreshold
             }

            // TODO 4.3: Creating a multiline string with the filtered results
            val outputStr =
                filteredModelOutput.sortedBy { -it.score }
                    .joinToString(separator = "\n") { "${it.label} -> ${it.score} " }

            // TODO 4.4: Updating the UI
            if (outputStr.isNotEmpty())
            runOnUiThread {
                textView.text = outputStr
            }
        }



    }
}