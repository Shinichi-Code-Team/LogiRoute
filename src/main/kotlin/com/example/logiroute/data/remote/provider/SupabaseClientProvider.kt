package com.example.logiroute.data.remote.provider

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {

    val client = createSupabaseClient(
        supabaseUrl = "https://aynippbeuvtkejoixnap.supabase.co",
        supabaseKey = "YOUR_PUBLISHABLE_KEY"
    ) {
        install(Postgrest)
    }
}