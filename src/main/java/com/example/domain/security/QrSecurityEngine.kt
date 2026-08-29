package com.example.domain.security

import android.net.Uri
import com.example.domain.model.SecurityAnalysisResult
import com.example.domain.model.SecurityRating
import java.util.Locale

object QrSecurityEngine {

    private val suspiciousTlds = setOf(
        "tk", "ml", "ga", "cf", "gq", "top", "work", "loan", "men", "click",
        "link", "fit", "rest", "zip", "mov", "surf", "buzz", "xyz", "monster"
    )

    private val sensitiveKeywords = listOf(
        "login", "signin", "account", "verify", "secure", "banking",
        "update-password", "wallet", "recovery", "auth", "claim-reward"
    )

    fun analyze(rawContent: String): SecurityAnalysisResult {
        val trimmed = rawContent.trim()
        val riskFactors = mutableListOf<String>()
        var score = 100

        // Check if it's a URL
        if (trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true)) {
            val isHttps = trimmed.startsWith("https://", ignoreCase = true)
            val uri = try { Uri.parse(trimmed) } catch (e: Exception) { null }
            val host = uri?.host?.lowercase(Locale.ROOT) ?: ""

            if (!isHttps) {
                riskFactors.add("Unencrypted HTTP connection (vulnerable to interception)")
                score -= 30
            }

            // Check for raw IP address
            val ipRegex = Regex("""^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$""")
            if (ipRegex.matches(host)) {
                riskFactors.add("Raw IP address host instead of registered domain name")
                score -= 40
            }

            // Check suspicious TLD
            val tld = host.substringAfterLast(".", "")
            if (tld in suspiciousTlds) {
                riskFactors.add("Domain uses high-risk / spam-prevalent TLD (.$tld)")
                score -= 25
            }

            // Check for deceptive multi-subdomain spoofing e.g. paypal.com.attacker.com
            val parts = host.split(".")
            if (parts.size > 3) {
                riskFactors.add("Excessive subdomains detected (possible brand spoofing)")
                score -= 15
            }

            // Check sensitive keywords in non-standard domain
            for (keyword in sensitiveKeywords) {
                if (host.contains(keyword) && !host.endsWith("apple.com") && !host.endsWith("google.com") && !host.endsWith("microsoft.com")) {
                    riskFactors.add("Domain contains sensitive keyword '$keyword'")
                    score -= 20
                    break
                }
            }

            // Check encoded or obfuscated characters
            if (trimmed.contains("%20") || trimmed.contains("@") || trimmed.contains("%00")) {
                riskFactors.add("Obfuscated or deceptive URL character encoding")
                score -= 20
            }

            val finalScore = score.coerceIn(0, 100)
            val rating = when {
                finalScore >= 85 && isHttps -> SecurityRating.VERIFIED_SAFE
                finalScore >= 70 -> SecurityRating.SAFE
                finalScore >= 45 -> SecurityRating.CAUTION
                else -> SecurityRating.SUSPICIOUS
            }

            val explanation = when (rating) {
                SecurityRating.VERIFIED_SAFE -> "Domain uses secure HTTPS with clean reputation and valid structural identity."
                SecurityRating.SAFE -> "No immediate threats found. Standard safety protocols apply."
                SecurityRating.CAUTION -> "Potentially unfamiliar or unencrypted link. Verify the destination before entering personal credentials."
                SecurityRating.SUSPICIOUS -> "High security risk detected! Phishing, malicious redirect, or spoofed hostname suspected."
            }

            return SecurityAnalysisResult(
                rating = rating,
                score = finalScore,
                riskFactors = riskFactors,
                domain = host.ifEmpty { null },
                isHttps = isHttps,
                explanation = explanation
            )
        }

        // Non-URL types (Wi-Fi, vCard, Text) are generally local & safe
        return SecurityAnalysisResult(
            rating = SecurityRating.VERIFIED_SAFE,
            score = 100,
            riskFactors = emptyList(),
            domain = null,
            isHttps = true,
            explanation = "Local data payload. No external network request required."
        )
    }
}
