import { NativeModules, Platform } from "react-native";

export type ProgressCardWidgetData = {
  title: string;
  subtitle: string;
  progress: number;
  deepLink?: string;
};

type GlanceKitNativeModule = {
  updateWidget(widgetId: string, data: ProgressCardWidgetData): Promise<void>;
};

const nativeModule = NativeModules.GlanceKitModule as
  | GlanceKitNativeModule
  | undefined;

export const AndroidWidgets = {
  updateWidget(
    widgetId: string,
    data: ProgressCardWidgetData
  ): Promise<void> {
    if (Platform.OS !== "android") {
      return Promise.reject(
        new Error("AndroidWidgets.updateWidget is only available on Android.")
      );
    }

    if (!nativeModule?.updateWidget) {
      return Promise.reject(
        new Error(
          "GlanceKit native module is not linked. Make sure the Android package is installed and registered."
        )
      );
    }

    return nativeModule.updateWidget(widgetId, data);
  },
};

export default {
  AndroidWidgets,
};
