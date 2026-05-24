import type { ConfigPlugin } from "@expo/config-plugins";

export type GlanceKitPluginProps = {
  deepLinkScheme?: string;
  deepLinkHost?: string;
  widgetName?: string;
  widgetDescription?: string;
};

declare const withGlanceKit: ConfigPlugin<GlanceKitPluginProps | void>;

export default withGlanceKit;
export { withGlanceKit };
