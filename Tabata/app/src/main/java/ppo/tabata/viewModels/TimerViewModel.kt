package ppo.tabata.viewModels

import android.app.Application
import android.content.res.Resources
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ppo.tabata.R
import ppo.tabata.data.TabataEntity

class TimerViewModel(application: Application): AndroidViewModel(application) {
    private lateinit var tabata: TabataEntity
    lateinit var res: Resources

    var currentText = MutableLiveData<String>("Warm-up")

    var prevText = MutableLiveData<String>("")
    var nextText = MutableLiveData<String>("Work")

    var timeRemainingText = MutableLiveData<String>("00:00")
    var timePercentRemaining = MutableLiveData<Int>(100)
    var isFinished = MutableLiveData<Boolean>(false)

    var countDownTimer: CountDownTimer? = null
    private var timeRemaining: Long = 0
    private var timeRemainingStatic = timeRemaining
    var currIndex = 0
    private var stagesCount = 0
    private val interval: Long = 1000
    var isRunning: Boolean = false

    var sequenceText = arrayListOf<String>()
    var sequenceTime = arrayListOf<Int>()

    private val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
    private val soundPool = SoundPool.Builder().setAudioAttributes(audioAttributes).build()

    fun setTabata(tabata: TabataEntity) {
        this.tabata = tabata
        res = getApplication<Application>().resources
        res.updateConfiguration(res.configuration, res.displayMetrics)
        setInitValues()
        soundPool.load(getApplication<Application>().applicationContext, R.raw.drink, 1)
        createSequence()
    }

    private fun setInitValues() {
        stagesCount = tabata.cycles * (tabata.repeats * 2 + 1)
        timeRemaining = (tabata.warm_up * 1000).toLong()
        timeRemainingStatic = timeRemaining
        timeRemainingText.value = EditTabataViewModel.getTime(tabata.warm_up)
        currentText.value = res.getString(R.string.warm_up)
        nextText.value = res.getString(R.string.work_short)
        currIndex = 2
    }

    private fun createSequence() {
        sequenceText.add("")
        sequenceTime.add(tabata.warm_up)

        sequenceText.add(res.getString(R.string.warm_up))
        sequenceTime.add(tabata.warm_up)

        for (i in 0 until tabata.cycles) {
            for (j in 0 until tabata.repeats) {
                sequenceText.add(res.getString(R.string.work_short))
                sequenceText.add(res.getString(R.string.rest))
                sequenceTime.add(tabata.work)
                sequenceTime.add(tabata.rest)
            }

            sequenceText.add(res.getString(R.string.cooldown))
            sequenceTime.add(tabata.cooldown)
        }
        sequenceText.add("")
        sequenceText.add("")
    }

    fun pause() {
        isRunning = false
        countDownTimer?.cancel()
    }

    private fun getTimeRemainingText(time: Long) = EditTabataViewModel.getTime(time.toInt() / 1000)

    fun startTimer() {
        isRunning = true
        countDownTimer = object : CountDownTimer(timeRemaining, interval) {
            override fun onFinish() {
                soundPool.play(1, 1F, 1F, 1, 0, 1F)
                timePercentRemaining.value = 100

                if (currIndex < 1) currIndex = 1
                if (currIndex - 2 > stagesCount) return

                if (currIndex - 2 == stagesCount) {
                    currentText.value = res.getString(R.string.complete)
                    prevText.value = ""
                    currIndex += 1
                }
                else {
                    prevText.value = sequenceText[currIndex-1]
                    currentText.value = sequenceText[currIndex]
                    nextText.value = sequenceText[currIndex+1]

                    timeRemaining = sequenceTime[currIndex].toLong() * 1000
                    timeRemainingStatic = timeRemaining
                    timeRemainingText.value = getTimeRemainingText(timeRemaining)
                    startTimer()
                    currIndex += 1
                }
            }
            override fun onTick(millisUntilFinished: Long) {
                timeRemainingText.value = getTimeRemainingText(timeRemaining)
                timePercentRemaining.value = ((timeRemaining * 100) / timeRemainingStatic).toInt()
                timeRemaining -= interval
            }
        }.start()
    }

    fun prev(){
        if (countDownTimer != null) {
            currIndex -= 2
            countDownTimer?.cancel()
            countDownTimer?.onFinish()
        }
    }
    fun next(){
        countDownTimer?.cancel()
        countDownTimer?.onFinish()
    }
}