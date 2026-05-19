package com.mysuccu.app.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

/**
 * 🚀 MySuccu 云端核心：Supabase 客户端单例
 * 托管爱尔兰数据中心，无缝连接 PostgreSQL 数据库与账号鉴权系统
 */
object SupabaseModule {

    // 🔒 成功对接：你的专属爱尔兰节点凭证
    private const val SUPABASE_URL = "https://azasvsbezkvjjotmykax.supabase.co"
    private const val SUPABASE_ANON_KEY = "sb_publishable_fg6gQCHDmhE6iCyyM6jagw_AknoWffW"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        // 1. 注入 Postgrest 插件：处理多肉主表、分类表的数据 CURD
        install(Postgrest)

        // 2. 注入 Auth 插件：接通标准云端账号登录鉴权体系
        // 🚀 核心修复：新版默认开启 Token 持久化，无需任何块级配置！
        install(Auth)
    }
}