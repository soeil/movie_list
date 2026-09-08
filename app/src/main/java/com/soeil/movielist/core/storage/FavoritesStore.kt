package com.soeil.movielist.core.storage

import android.content.Context

// Liked movie ids as a SharedPreferences string set — one id per movie,
// survives process death, no extra dependencies.
object FavoritesStore {
    private const val PREFS_NAME = "favorites"
    private const val KEY_IDS = "movie_ids"

    fun getIds(context: Context): Set<String> =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getStringSet(KEY_IDS, emptySet())
            ?: emptySet()

    /** Toggles [id]; returns true when the id is now liked, false when removed. */
    fun toggle(context: Context, id: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // getStringSet hands back a mutable live set — copy before editing
        val ids = HashSet(prefs.getStringSet(KEY_IDS, emptySet()) ?: emptySet())
        val nowLiked = if (id in ids) {
            ids.remove(id)
            false
        } else {
            ids.add(id)
            true
        }
        prefs.edit().putStringSet(KEY_IDS, ids).apply()
        return nowLiked
    }
}
