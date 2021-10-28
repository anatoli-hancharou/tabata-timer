package ppo.tabata.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity
import ppo.tabata.data.TabataEntity
import ppo.tabata.databinding.ActivityTimerBinding
import ppo.tabata.viewModels.TimerViewModel

class TimerActivity : LocaleAwareCompatActivity() {

    private val binding: ActivityTimerBinding by lazy {
        ActivityTimerBinding.inflate(layoutInflater) }
    private val viewModel: TimerViewModel by lazy {
        ViewModelProvider(this).get(TimerViewModel::class.java) }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.setTabata(intent.getSerializableExtra("tabata") as TabataEntity)

        binding.runStop.setOnClickListener{
            if (viewModel.isRunning) {
                viewModel.pause()
            }
            else {
                viewModel.startTimer()
            }
        }
        viewModel.currentText.observe(this, Observer<String>{
            binding.currText.text = it
        })
        viewModel.prevText.observe(this, Observer<String>{
            binding.prevText.text = it
        })
        viewModel.nextText.observe(this, Observer<String>{
            binding.nextText.text = it
        })
        viewModel.timeRemainingText.observe(this, Observer<String>{
            binding.time.text = it
        })
        viewModel.isFinished.observe(this, Observer<Boolean>{
        })
        binding.next.setOnClickListener{
            viewModel.next()
        }
        binding.prev.setOnClickListener{
            viewModel.prev()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (viewModel.countDownTimer != null) viewModel.pause()
        finish()
    }
}