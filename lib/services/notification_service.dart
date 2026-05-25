import 'package:flutter/services.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';

class NotificationService {
  static const String _prefs_key = 'webhook_url';
  static const String _default_url = 'http://10.0.10.125:8082/log';
  static const platform = MethodChannel('gr.ornette.notification_app/config');

  static Future<void> initialize() async {
    // Load saved webhook URL and initialize it in Kotlin side
    final prefs = await SharedPreferences.getInstance();
    final savedUrl = prefs.getString(_prefs_key) ?? _default_url;
    await _setWebhookUrl(savedUrl);
  }

  static Future<void> _setWebhookUrl(String url) async {
    try {
      await platform.invokeMethod('setWebhookUrl', {'url': url});
    } catch (e) {
      print('Error setting webhook URL in native: $e');
    }
  }

  static Future<void> startListening(String webhookUrl) async {
    // Save to SharedPreferences
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_prefs_key, webhookUrl);
    
    // Pass to native Kotlin side
    await _setWebhookUrl(webhookUrl);
  }

  static Future<void> stopListening() async {
    // Just clear the stored value (don't remove, keep default)
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_prefs_key, _default_url);
    await _setWebhookUrl(_default_url);
  }

  static Future<void> sendNotification({
    required String packageName,
    required String title,
    required String text,
    required String? subText,
    String? tag,
    int? priority,
  }) async {
    // Not used by NotificationListenerService, but kept for compatibility
  }
}
