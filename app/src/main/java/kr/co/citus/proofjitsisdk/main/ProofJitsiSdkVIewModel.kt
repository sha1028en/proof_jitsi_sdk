package kr.co.citus.proofjitsisdk.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProofJitsiSdkVIewModel : ViewModel() {
    private val isAudioMute by lazy { MutableLiveData(false) }


}