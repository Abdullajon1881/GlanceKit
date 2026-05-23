module.exports = {
  expo: {
    name: "GlanceKitExpoExample",
    slug: "glancekit-expo-app",
    version: "1.0.0",
    scheme: "glancekit",
    newArchEnabled: false,
    orientation: "portrait",
    splash: {
      backgroundColor: "#111827",
    },
    android: {
      package: "dev.glancekit.expoexample",
    },
    plugins: [
      [
        "expo-build-properties",
        {
          android: {
            minSdkVersion: 26,
            compileSdkVersion: 34,
            targetSdkVersion: 34,
            buildToolsVersion: "34.0.0",
            kotlinVersion: "1.9.24",
          },
        },
      ],
      [
        "@glancekit/expo-plugin",
        {
          deepLinkScheme: "glancekit",
          deepLinkHost: "progress",
          widgetName: "Progress Card Widget",
          widgetDescription:
            "A GlanceKit progress widget powered by Jetpack Glance.",
        },
      ],
    ],
  },
};
