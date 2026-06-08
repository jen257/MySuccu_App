package com.mysuccu.app.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseModule {
    private const val SUPABASE_URL = "https://azasvsbezkvjjotmykax.supabase.co"
    private const val SUPABASE_ANON_KEY = "sb_publishable_fg6gQCHDmhE6iCyyM6jagw_AknoWffW"

    val client = createSupabaseClient(SUPABASE_URL, SUPABASE_ANON_KEY) {
        install(Postgrest)
        install(Auth) // 新版 SDK 默认开启 Token 持久化
    }
}