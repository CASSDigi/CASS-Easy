package com.example.domain.parser

import android.net.Uri
import com.example.domain.model.ParsedQrContent
import com.example.domain.model.QrContentType
import com.example.domain.security.QrSecurityEngine
import java.util.Locale

object QrContentParser {

    fun parse(
        raw: String,
        barcodeFormat: String = "QR Code",
        timestamp: Long = System.currentTimeMillis()
    ): ParsedQrContent {
        val trimmed = raw.trim()
        val security = QrSecurityEngine.analyze(trimmed)

        // 1. Wi-Fi Configuration (WIFI:T:WPA;S:Network;P:Secret;H:false;;)
        if (trimmed.startsWith("WIFI:", ignoreCase = true)) {
            val details = mutableMapOf<String, String>()
            val body = trimmed.removePrefix("WIFI:").removePrefix("wifi:")
            val tokens = body.split(";")
            for (token in tokens) {
                if (token.startsWith("S:", ignoreCase = true)) {
                    details["SSID"] = token.substring(2)
                } else if (token.startsWith("P:", ignoreCase = true)) {
                    details["Password"] = token.substring(2)
                } else if (token.startsWith("T:", ignoreCase = true)) {
                    details["Security"] = token.substring(2)
                } else if (token.startsWith("H:", ignoreCase = true)) {
                    details["Hidden"] = if (token.substring(2).equals("true", ignoreCase = true)) "Yes" else "No"
                }
            }
            val ssid = details["SSID"] ?: "Unknown Wi-Fi"
            val securityType = details["Security"] ?: "WPA/WPA2"
            return ParsedQrContent(
                rawText = trimmed,
                type = QrContentType.WIFI,
                title = ssid,
                subtitle = "Wi-Fi Network ($securityType)",
                details = details,
                actionUrl = null,
                isSecure = true,
                securityResult = security,
                barcodeFormat = barcodeFormat,
                timestamp = timestamp
            )
        }

        // 2. vCard Contact (BEGIN:VCARD ... END:VCARD)
        if (trimmed.contains("BEGIN:VCARD", ignoreCase = true)) {
            val details = mutableMapOf<String, String>()
            val lines = trimmed.lines()
            var name = "Contact Card"
            for (line in lines) {
                val cleanLine = line.trim()
                if (cleanLine.startsWith("FN:", ignoreCase = true)) {
                    name = cleanLine.substring(3).trim()
                    details["Full Name"] = name
                } else if (cleanLine.startsWith("N:", ignoreCase = true) && !details.containsKey("Full Name")) {
                    val nParts = cleanLine.substring(2).split(";")
                    val formatted = nParts.reversed().filter { it.isNotBlank() }.joinToString(" ")
                    if (formatted.isNotBlank()) {
                        name = formatted
                        details["Full Name"] = formatted
                    }
                } else if (cleanLine.startsWith("TEL", ignoreCase = true)) {
                    val phone = cleanLine.substringAfter(":")
                    details["Phone"] = phone
                } else if (cleanLine.startsWith("EMAIL", ignoreCase = true)) {
                    val email = cleanLine.substringAfter(":")
                    details["Email"] = email
                } else if (cleanLine.startsWith("ORG:", ignoreCase = true)) {
                    details["Organization"] = cleanLine.substring(4)
                } else if (cleanLine.startsWith("TITLE:", ignoreCase = true)) {
                    details["Title"] = cleanLine.substring(6)
                } else if (cleanLine.startsWith("URL:", ignoreCase = true)) {
                    details["Website"] = cleanLine.substring(4)
                } else if (cleanLine.startsWith("ADR", ignoreCase = true)) {
                    val adr = cleanLine.substringAfter(":").replace(";", ", ").trim(',', ' ')
                    if (adr.isNotBlank()) details["Address"] = adr
                } else if (cleanLine.startsWith("NOTE:", ignoreCase = true)) {
                    details["Notes"] = cleanLine.substring(5)
                }
            }
            return ParsedQrContent(
                rawText = trimmed,
                type = QrContentType.CONTACT_VCARD,
                title = name,
                subtitle = details["Organization"] ?: details["Phone"] ?: "vCard Contact",
                details = details,
                actionUrl = details["Phone"]?.let { "tel:$it" },
                isSecure = true,
                securityResult = security,
                barcodeFormat = barcodeFormat,
                timestamp = timestamp
            )
        }

        // 3. MeCard Format (MECARD:N:Smith,John;TEL:12345;EMAIL:a@b.com;;)
        if (trimmed.startsWith("MECARD:", ignoreCase = true)) {
            val details = mutableMapOf<String, String>()
            val body = trimmed.removePrefix("MECARD:").removePrefix("mecard:")
            val parts = body.split(";")
            for (p in parts) {
                if (p.startsWith("N:", ignoreCase = true)) details["Full Name"] = p.substring(2).replace(",", " ")
                if (p.startsWith("TEL:", ignoreCase = true)) details["Phone"] = p.substring(4)
                if (p.startsWith("EMAIL:", ignoreCase = true)) details["Email"] = p.substring(6)
                if (p.startsWith("ADR:", ignoreCase = true)) details["Address"] = p.substring(4)
            }
            val name = details["Full Name"] ?: "Contact"
            return ParsedQrContent(
                rawText = trimmed,
                type = QrContentType.CONTACT_VCARD,
                title = name,
                subtitle = details["Phone"] ?: "MeCard Contact",
                details = details,
                actionUrl = details["Phone"]?.let { "tel:$it" },
                isSecure = true,
                securityResult = security,
                barcodeFormat = barcodeFormat,
                timestamp = timestamp
            )
        }

        // 4. Phone Number (tel:+123456789)
        if (trimmed.startsWith("tel:", ignoreCase = true)) {
            val phone = trimmed.removePrefix("tel:").removePrefix("TEL:")
            return ParsedQrContent(
                rawText = trimmed,
                type = QrContentType.PHONE,
                title = phone,
                subtitle = "Phone Number",
                details = mapOf("Phone" to phone),
                actionUrl = trimmed,
                isSecure = true,
                securityResult = security,
                barcodeFormat = barcodeFormat,
                timestamp = timestamp
            )
        }

        // 5. SMS Message (sms:+12345?body=hello or smsto:+12345:hello)
        if (trimmed.startsWith("sms:", ignoreCase = true) || trimmed.startsWith("smsto:", ignoreCase = true)) {
            val clean = trimmed.substringAfter(":")
            val phone = clean.substringBefore("?").substringBefore(":")
            val body = if (clean.contains("?body=")) clean.substringAfter("?body=")
            else if (clean.contains(":")) clean.substringAfter(":") else ""
            val details = mutableMapOf("Recipient" to phone)
            if (body.isNotBlank()) details["Message"] = body
            return ParsedQrContent(
                rawText = trimmed,
                type = QrContentType.SMS,
                title = phone,
                subtitle = if (body.isNotBlank()) body.take(30) + "..." else "SMS Message",
                details = details,
                actionUrl = "sms:$phone",
                isSecure = true,
                securityResult = security,
                barcodeFormat = barcodeFormat,
                timestamp = timestamp
            )
        }

        // 6. Email (mailto:user@domain.com?subject=Hello)
        if (trimmed.startsWith("mailto:", ignoreCase = true)) {
            val clean = trimmed.removePrefix("mailto:").removePrefix("MAILTO:")
            val email = clean.substringBefore("?")
            val uri = try { Uri.parse(trimmed) } catch (e: Exception) { null }
            val subject = uri?.getQueryParameter("subject") ?: ""
            val body = uri?.getQueryParameter("body") ?: ""
            val details = mutableMapOf("Email" to email)
            if (subject.isNotBlank()) details["Subject"] = subject
            if (body.isNotBlank()) details["Body"] = body
            return ParsedQrContent(
                rawText = trimmed,
                type = QrContentType.EMAIL,
                title = email,
                subtitle = if (subject.isNotBlank()) subject else "Email Message",
                details = details,
                actionUrl = trimmed,
                isSecure = true,
                securityResult = security,
                barcodeFormat = barcodeFormat,
                timestamp = timestamp
            )
        }

        // 7. Map Location (geo:37.7749,-122.4194)
        if (trimmed.startsWith("geo:", ignoreCase = true)) {
            val coords = trimmed.removePrefix("geo:").removePrefix("GEO:").substringBefore("?")
            return ParsedQrContent(
                rawText = trimmed,
                type = QrContentType.GEO_LOCATION,
                title = coords,
                subtitle = "Geographic Coordinates",
                details = mapOf("Coordinates" to coords),
                actionUrl = "https://maps.google.com/?q=$coords",
                isSecure = true,
                securityResult = security,
                barcodeFormat = barcodeFormat,
                timestamp = timestamp
            )
        }

        // 8. Calendar Event (BEGIN:VEVENT)
        if (trimmed.contains("BEGIN:VEVENT", ignoreCase = true)) {
            val details = mutableMapOf<String, String>()
            var summary = "Calendar Event"
            for (line in trimmed.lines()) {
                val clean = line.trim()
                if (clean.startsWith("SUMMARY:", ignoreCase = true)) {
                    summary = clean.substring(8)
                    details["Event Title"] = summary
                } else if (clean.startsWith("LOCATION:", ignoreCase = true)) {
                    details["Location"] = clean.substring(9)
                } else if (clean.startsWith("DTSTART:", ignoreCase = true)) {
                    details["Start Date"] = clean.substring(8)
                } else if (clean.startsWith("DTEND:", ignoreCase = true)) {
                    details["End Date"] = clean.substring(6)
                } else if (clean.startsWith("DESCRIPTION:", ignoreCase = true)) {
                    details["Description"] = clean.substring(12)
                }
            }
            return ParsedQrContent(
                rawText = trimmed,
                type = QrContentType.CALENDAR_EVENT,
                title = summary,
                subtitle = details["Location"] ?: "Scheduled Event",
                details = details,
                actionUrl = null,
                isSecure = true,
                securityResult = security,
                barcodeFormat = barcodeFormat,
                timestamp = timestamp
            )
        }

        // 9. Crypto & Payments (bitcoin:, ethereum:, upi://)
        if (trimmed.startsWith("bitcoin:", ignoreCase = true) ||
            trimmed.startsWith("ethereum:", ignoreCase = true) ||
            trimmed.startsWith("upi://", ignoreCase = true) ||
            trimmed.contains("paypal.me", ignoreCase = true) ||
            trimmed.startsWith("solana:", ignoreCase = true)
        ) {
            val network = when {
                trimmed.startsWith("bitcoin:", ignoreCase = true) -> "Bitcoin (BTC)"
                trimmed.startsWith("ethereum:", ignoreCase = true) -> "Ethereum (ETH)"
                trimmed.startsWith("solana:", ignoreCase = true) -> "Solana (SOL)"
                trimmed.startsWith("upi://", ignoreCase = true) -> "UPI Digital Payment"
                else -> "Payment Link"
            }
            val address = trimmed.substringAfter("://").substringAfter(":").substringBefore("?")
            return ParsedQrContent(
                rawText = trimmed,
                type = QrContentType.PAYMENT_CRYPTO,
                title = network,
                subtitle = address.take(16) + "...",
                details = mapOf("Network" to network, "Destination" to address, "Raw URI" to trimmed),
                actionUrl = trimmed,
                isSecure = true,
                securityResult = security,
                barcodeFormat = barcodeFormat,
                timestamp = timestamp
            )
        }

        // 10. Social Media Links
        val socialDomains = mapOf(
            "instagram.com" to "Instagram Profile",
            "twitter.com" to "X / Twitter",
            "x.com" to "X / Twitter",
            "linkedin.com" to "LinkedIn Profile",
            "youtube.com" to "YouTube Channel",
            "youtu.be" to "YouTube Video",
            "tiktok.com" to "TikTok Profile",
            "wa.me" to "WhatsApp Chat",
            "t.me" to "Telegram Link",
            "facebook.com" to "Facebook Profile",
            "github.com" to "GitHub Repository"
        )
        for ((domain, label) in socialDomains) {
            if (trimmed.contains(domain, ignoreCase = true)) {
                return ParsedQrContent(
                    rawText = trimmed,
                    type = QrContentType.SOCIAL_LINK,
                    title = label,
                    subtitle = trimmed.substringAfter("https://").substringAfter("http://"),
                    details = mapOf("Platform" to label, "Link" to trimmed),
                    actionUrl = if (!trimmed.startsWith("http")) "https://$trimmed" else trimmed,
                    isSecure = security.score >= 70,
                    securityResult = security,
                    barcodeFormat = barcodeFormat,
                    timestamp = timestamp
                )
            }
        }

        // 11. App Links / Play Store / App Store
        if (trimmed.contains("play.google.com/store/apps", ignoreCase = true) ||
            trimmed.contains("apps.apple.com", ignoreCase = true)
        ) {
            return ParsedQrContent(
                rawText = trimmed,
                type = QrContentType.APP_LINK,
                title = "App Download",
                subtitle = if (trimmed.contains("play.google")) "Google Play Store" else "Apple App Store",
                details = mapOf("Store Link" to trimmed),
                actionUrl = trimmed,
                isSecure = true,
                securityResult = security,
                barcodeFormat = barcodeFormat,
                timestamp = timestamp
            )
        }

        // 12. Standard Website URL (http:// or https://)
        if (trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true) ||
            (trimmed.contains(".") && !trimmed.contains(" ") && (trimmed.startsWith("www.") || trimmed.endsWith(".com") || trimmed.endsWith(".org") || trimmed.endsWith(".io") || trimmed.endsWith(".ai")))
        ) {
            val url = if (!trimmed.startsWith("http://", ignoreCase = true) && !trimmed.startsWith("https://", ignoreCase = true)) {
                "https://$trimmed"
            } else trimmed
            val host = try { Uri.parse(url).host ?: trimmed } catch (e: Exception) { trimmed }
            return ParsedQrContent(
                rawText = url,
                type = QrContentType.URL,
                title = host,
                subtitle = url,
                details = mapOf("Host Domain" to host, "Full URL" to url, "Protocol" to if (url.startsWith("https")) "HTTPS (Encrypted)" else "HTTP (Unencrypted)"),
                actionUrl = url,
                isSecure = security.score >= 70,
                securityResult = security,
                barcodeFormat = barcodeFormat,
                timestamp = timestamp
            )
        }

        // 13. Barcode (Numeric strings or standard 1D / 2D formats)
        if (barcodeFormat != "QR Code" || (trimmed.length in 8..14 && trimmed.all { it.isDigit() })) {
            val label = if (barcodeFormat != "QR Code") barcodeFormat else "Product Barcode"
            return ParsedQrContent(
                rawText = trimmed,
                type = QrContentType.BARCODE_PRODUCT,
                title = "$label ($trimmed)",
                subtitle = "$label Standard Barcode",
                details = mapOf("Barcode Number" to trimmed, "Symbology" to barcodeFormat),
                actionUrl = "https://www.google.com/search?q=$trimmed",
                isSecure = true,
                securityResult = security,
                barcodeFormat = barcodeFormat,
                timestamp = timestamp
            )
        }

        // 14. Plain Text / Document Note
        return ParsedQrContent(
            rawText = trimmed,
            type = QrContentType.PLAIN_TEXT,
            title = if (trimmed.length > 32) trimmed.take(32) + "..." else trimmed,
            subtitle = "${trimmed.length} characters",
            details = mapOf("Text" to trimmed, "Character Count" to "${trimmed.length}"),
            actionUrl = null,
            isSecure = true,
            securityResult = security,
            barcodeFormat = barcodeFormat,
            timestamp = timestamp
        )
    }
}
