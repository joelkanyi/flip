package com.joelkanyi.flip.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joelkanyi.flip.audio.SoundManager
import com.joelkanyi.flip.data.model.CoinSide
import com.joelkanyi.flip.data.model.FlipStats
import com.joelkanyi.flip.data.repository.FlipRepository
import com.joelkanyi.flip.domain.model.CoinState
import com.joelkanyi.flip.haptic.HapticManager
import com.joelkanyi.flip.presentation.animation.CoinAnimationSpec
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CoinFlipViewModel(
    private val repository: FlipRepository,
    private val hapticManager: HapticManager,
    private val soundManager: SoundManager
) : ViewModel() {

    private val _coinState = MutableStateFlow<CoinState>(CoinState.Idle(CoinSide.HEADS))
    val coinState: StateFlow<CoinState> = _coinState.asStateFlow()

    val stats: StateFlow<FlipStats> = repository.stats
        .stateIn(viewModelScope, SharingStarted.Eagerly, FlipStats())

    val lastResult: StateFlow<CoinSide?> = repository.lastResult
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private var isFlipping = false

    init {
        // Initialize with last result if available
        viewModelScope.launch {
            repository.lastResult.collect { lastSide ->
                if (_coinState.value is CoinState.Idle && lastSide != null) {
                    _coinState.value = CoinState.Idle(lastSide)
                }
            }
        }
    }

    fun flip() {
        if (isFlipping) return

        viewModelScope.launch {
            isFlipping = true

            val currentSide = _coinState.value.displayedSide
            val targetSide = CoinSide.random()

            // Phase 1: Anticipation
            _coinState.value = CoinState.Anticipating(currentSide)
            delay(CoinAnimationSpec.ANTICIPATION_DURATION.toLong())

            // Phase 2: Spinning - play spin sound
            _coinState.value = CoinState.Spinning(
                startSide = currentSide,
                targetSide = targetSide,
                rotations = CoinAnimationSpec.SPIN_ROTATIONS
            )
            soundManager.playCoinSpin()
            delay(CoinAnimationSpec.SPIN_DURATION.toLong())

            // Phase 3: Decelerating
            _coinState.value = CoinState.Decelerating(targetSide)
            delay(CoinAnimationSpec.DECELERATION_DURATION.toLong())

            // Phase 4: Wobbling
            _coinState.value = CoinState.Wobbling(targetSide)
            delay(CoinAnimationSpec.WOBBLE_DURATION.toLong())

            // Phase 5: Settled - trigger landing feedback
            _coinState.value = CoinState.Settled(targetSide)
            hapticManager.vibrateOnLand()
            soundManager.playCoinLand()

            // Record the result
            repository.recordFlip(targetSide)

            isFlipping = false
        }
    }

    fun resetStats() {
        viewModelScope.launch {
            repository.resetStats()
            _coinState.value = CoinState.Idle(CoinSide.HEADS)
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
