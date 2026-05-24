import React, { useMemo, useState } from "react";
import {
  Pressable,
  SafeAreaView,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
  useColorScheme,
} from "react-native";
import { AndroidWidgets } from "glancekit";

const DEFAULT_WIDGET_ID = "progress-demo";
const DEFAULT_DEEP_LINK = "glancekit://progress/progress-demo";

export default function App() {
  const isDarkMode = useColorScheme() === "dark";
  const [title, setTitle] = useState("Worker is coming");
  const [subtitle, setSubtitle] = useState("Arriving in 12 min");
  const [progress, setProgress] = useState("72");
  const [deepLink, setDeepLink] = useState(DEFAULT_DEEP_LINK);
  const [status, setStatus] = useState(null);
  const [error, setError] = useState(null);

  const backgroundColor = isDarkMode ? "#0f172a" : "#f8fafc";
  const surfaceColor = isDarkMode ? "#111827" : "#ffffff";
  const textColor = isDarkMode ? "#f8fafc" : "#0f172a";
  const mutedColor = isDarkMode ? "#94a3b8" : "#475569";
  const borderColor = isDarkMode ? "#1f2937" : "#cbd5e1";

  const helperText = useMemo(() => {
    return `Widget target: ${DEFAULT_WIDGET_ID}`;
  }, []);

  const handleUpdateWidget = async () => {
    setStatus(null);
    setError(null);

    const parsedProgress = Number(progress);
    if (!Number.isInteger(parsedProgress)) {
      setError("Progress must be an integer.");
      return;
    }

    try {
      await AndroidWidgets.updateWidget(DEFAULT_WIDGET_ID, {
        title,
        subtitle,
        progress: parsedProgress,
        deepLink: deepLink.trim() || undefined,
      });
      setStatus(
        `Widget update sent for ${DEFAULT_WIDGET_ID} with progress ${parsedProgress}%.`,
      );
    } catch (updateError) {
      const message =
        updateError instanceof Error
          ? updateError.message
          : "Widget update failed.";
      setError(message);
    }
  };

  return (
    <SafeAreaView style={[styles.safeArea, { backgroundColor }]}>
      <ScrollView contentContainerStyle={styles.scrollContent}>
        <View style={[styles.card, { backgroundColor: surfaceColor }]}>
          <Text style={[styles.eyebrow, { color: mutedColor }]}>
            GlanceKit Expo Example
          </Text>
          <Text style={[styles.heading, { color: textColor }]}>
            Update ProgressCardWidget
          </Text>
          <Text style={[styles.body, { color: mutedColor }]}>
            This screen calls the native GlanceKit module from an Expo
            development build after Android prebuild wiring has been generated
            by the config plugin.
          </Text>
          <Text style={[styles.helper, { color: mutedColor }]}>
            {helperText}
          </Text>

          <Field
            label="Title"
            value={title}
            onChangeText={setTitle}
            placeholder="Worker is coming"
            textColor={textColor}
            borderColor={borderColor}
          />
          <Field
            label="Subtitle"
            value={subtitle}
            onChangeText={setSubtitle}
            placeholder="Arriving in 12 min"
            textColor={textColor}
            borderColor={borderColor}
          />
          <Field
            label="Progress"
            value={progress}
            onChangeText={setProgress}
            placeholder="72"
            keyboardType="number-pad"
            textColor={textColor}
            borderColor={borderColor}
          />
          <Field
            label="Deep Link"
            value={deepLink}
            onChangeText={setDeepLink}
            placeholder={DEFAULT_DEEP_LINK}
            autoCapitalize="none"
            textColor={textColor}
            borderColor={borderColor}
          />

          <Pressable onPress={handleUpdateWidget} style={styles.button}>
            <Text style={styles.buttonText}>Update Widget</Text>
          </Pressable>

          {status ? (
            <View style={styles.successBox}>
              <Text style={styles.successText}>{status}</Text>
            </View>
          ) : null}

          {error ? (
            <View style={styles.errorBox}>
              <Text style={styles.errorText}>{error}</Text>
            </View>
          ) : null}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

function Field({
  label,
  value,
  onChangeText,
  placeholder,
  textColor,
  borderColor,
  keyboardType = "default",
  autoCapitalize = "sentences",
}) {
  return (
    <View style={styles.fieldGroup}>
      <Text style={[styles.fieldLabel, { color: textColor }]}>{label}</Text>
      <TextInput
        autoCapitalize={autoCapitalize}
        keyboardType={keyboardType}
        onChangeText={onChangeText}
        placeholder={placeholder}
        placeholderTextColor="#94a3b8"
        style={[
          styles.input,
          {
            color: textColor,
            borderColor,
          },
        ]}
        value={value}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
  },
  scrollContent: {
    padding: 20,
  },
  card: {
    borderRadius: 20,
    padding: 20,
    gap: 12,
    shadowColor: "#0f172a",
    shadowOpacity: 0.08,
    shadowRadius: 12,
    shadowOffset: { width: 0, height: 6 },
    elevation: 4,
  },
  eyebrow: {
    fontSize: 12,
    fontWeight: "700",
    letterSpacing: 1,
    textTransform: "uppercase",
  },
  heading: {
    fontSize: 28,
    fontWeight: "700",
  },
  helper: {
    fontSize: 13,
    fontWeight: "600",
  },
  body: {
    fontSize: 15,
    lineHeight: 22,
  },
  fieldGroup: {
    gap: 8,
  },
  fieldLabel: {
    fontSize: 14,
    fontWeight: "600",
  },
  input: {
    borderWidth: 1,
    borderRadius: 12,
    paddingHorizontal: 14,
    paddingVertical: 12,
    fontSize: 16,
  },
  button: {
    marginTop: 8,
    alignItems: "center",
    borderRadius: 14,
    backgroundColor: "#2563eb",
    paddingVertical: 14,
  },
  buttonText: {
    color: "#ffffff",
    fontSize: 16,
    fontWeight: "700",
  },
  successBox: {
    borderRadius: 12,
    backgroundColor: "#dcfce7",
    padding: 12,
  },
  successText: {
    color: "#166534",
    fontSize: 14,
    fontWeight: "600",
  },
  errorBox: {
    borderRadius: 12,
    backgroundColor: "#fee2e2",
    padding: 12,
  },
  errorText: {
    color: "#991b1b",
    fontSize: 14,
    fontWeight: "600",
  },
});
