package com.example.logiroute.data.remote.provider

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {

    val client = createSupabaseClient(
        supabaseUrl = "https://aynippbeuvtkejoixnap.supabase.co",
        supabaseKey = "sb_publishable_a3hkr9a4AhDB6RTweImFVA_bUMhF8fS"
    ) {
        install(Postgrest)
    }
}