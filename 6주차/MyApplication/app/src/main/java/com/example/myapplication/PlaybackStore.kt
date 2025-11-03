package com.example.myapplication

import android.content.Context

data class PlaybackState(
    val songId: Int,
    val posMs: Int,
    val isPlaying: Boolean
)

object PlaybackStore {
    private const val PREF = "playback_state"
    private const val K_ID = "id"
    private const val K_POS = "pos"
    private const val K_PLAY = "play"

    fun save(ctx: Context, state: PlaybackState) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
            .putInt(K_ID, state.songId)
            .putInt(K_POS, state.posMs)
            .putBoolean(K_PLAY, state.isPlaying)
            .apply()
    }

    fun read(ctx: Context): PlaybackState? {
        val sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        if (!sp.contains(K_ID)) return null
        val id = sp.getInt(K_ID, 0)
        val pos = sp.getInt(K_POS, 0)
        val playing = sp.getBoolean(K_PLAY, false)
        return PlaybackState(id, pos, playing)
    }
}
