const fs = require("fs/promises");
const path = require("path");
const configPlugins = require(
  require.resolve("@expo/config-plugins", {
    paths: [process.cwd(), __dirname],
  }),
);
const {
  AndroidConfig,
  createRunOncePlugin,
  withAndroidManifest,
  withDangerousMod,
  withStringsXml,
} = configPlugins;

const pkg = require("../../package.json");

const RECEIVER_CLASS =
  "dev.glancekit.reactnative.android.widget.ProgressCardWidgetReceiver";
const PROVIDER_XML_NAME = "progress_card_widget_info";
const PROVIDER_XML_RESOURCE = "@xml/progress_card_widget_info";
const WIDGET_NAME_STRING = "progress_card_widget_name";
const WIDGET_DESCRIPTION_STRING = "progress_card_widget_description";
const DEFAULT_SCHEME = "glancekit";
const DEFAULT_HOST = "progress";

function withGlanceKit(config, props = {}) {
  const resolved = resolveOptions(config, props);

  config = applyScheme(config, resolved.deepLinkScheme);
  config = withWidgetStrings(config, resolved);
  config = withWidgetManifest(config, resolved);
  config = withWidgetResources(config);

  return config;
}

function resolveOptions(config, props) {
  return {
    deepLinkScheme: resolveScheme(config, props),
    deepLinkHost: sanitizeString(props.deepLinkHost) || DEFAULT_HOST,
    widgetName:
      sanitizeString(props.widgetName) || "Progress Card Widget",
    widgetDescription:
      sanitizeString(props.widgetDescription) ||
      "A GlanceKit progress widget powered by Jetpack Glance.",
  };
}

function resolveScheme(config, props) {
  const explicit = sanitizeString(props.deepLinkScheme);
  if (explicit) {
    return explicit;
  }

  if (Array.isArray(config.scheme)) {
    const fromArray = config.scheme.find(value => sanitizeString(value));
    return sanitizeString(fromArray) || DEFAULT_SCHEME;
  }

  return sanitizeString(config.scheme) || DEFAULT_SCHEME;
}

function applyScheme(config, scheme) {
  if (!config.scheme) {
    config.scheme = scheme;
    return config;
  }

  if (Array.isArray(config.scheme)) {
    if (!config.scheme.includes(scheme)) {
      config.scheme = [scheme, ...config.scheme];
    }
    return config;
  }

  if (config.scheme !== scheme) {
    config.scheme = [config.scheme, scheme];
  }

  return config;
}

function withWidgetStrings(config, props) {
  return withStringsXml(config, modConfig => {
    modConfig.modResults = AndroidConfig.Strings.setStringItem(
      [
        {
          $: { name: WIDGET_NAME_STRING, translatable: "false" },
          _: props.widgetName,
        },
        {
          $: { name: WIDGET_DESCRIPTION_STRING, translatable: "false" },
          _: props.widgetDescription,
        },
      ],
      modConfig.modResults,
    );

    return modConfig;
  });
}

function withWidgetManifest(config, props) {
  return withAndroidManifest(config, modConfig => {
    const manifest = modConfig.modResults;
    const mainApplication = AndroidConfig.Manifest.getMainApplicationOrThrow(
      manifest,
    );

    ensureWidgetReceiver(mainApplication);
    ensureDeepLinkIntentFilter(mainApplication, props);

    return modConfig;
  });
}

function ensureWidgetReceiver(mainApplication) {
  if (!mainApplication.receiver) {
    mainApplication.receiver = [];
  }

  let receiver = mainApplication.receiver.find(
    candidate => candidate?.$?.["android:name"] === RECEIVER_CLASS,
  );

  if (!receiver) {
    receiver = { $: {} };
    mainApplication.receiver.push(receiver);
  }

  receiver.$["android:name"] = RECEIVER_CLASS;
  receiver.$["android:exported"] = "true";
  receiver.$["android:label"] = `@string/${WIDGET_NAME_STRING}`;
  receiver["intent-filter"] = [
    {
      action: [
        {
          $: {
            "android:name": "android.appwidget.action.APPWIDGET_UPDATE",
          },
        },
      ],
    },
  ];
  receiver["meta-data"] = [
    {
      $: {
        "android:name": "android.appwidget.provider",
        "android:resource": PROVIDER_XML_RESOURCE,
      },
    },
  ];
}

function ensureDeepLinkIntentFilter(mainApplication, props) {
  const mainActivity =
    findLauncherActivity(mainApplication.activity || []) ||
    (mainApplication.activity || [])[0];

  if (!mainActivity) {
    throw new Error("GlanceKit Expo plugin could not find the main Android activity.");
  }

  if (!mainActivity["intent-filter"]) {
    mainActivity["intent-filter"] = [];
  }

  const hasExistingFilter = mainActivity["intent-filter"].some(filter => {
    const hasView = (filter.action || []).some(
      action => action?.$?.["android:name"] === "android.intent.action.VIEW",
    );
    const hasBrowsable = (filter.category || []).some(
      category =>
        category?.$?.["android:name"] === "android.intent.category.BROWSABLE",
    );
    const hasDefault = (filter.category || []).some(
      category =>
        category?.$?.["android:name"] === "android.intent.category.DEFAULT",
    );
    const hasData = (filter.data || []).some(data => {
      const attributes = data?.$ || {};
      return (
        attributes["android:scheme"] === props.deepLinkScheme &&
        attributes["android:host"] === props.deepLinkHost
      );
    });

    return hasView && hasBrowsable && hasDefault && hasData;
  });

  if (!hasExistingFilter) {
    mainActivity["intent-filter"].push({
      action: [
        {
          $: {
            "android:name": "android.intent.action.VIEW",
          },
        },
      ],
      category: [
        {
          $: {
            "android:name": "android.intent.category.DEFAULT",
          },
        },
        {
          $: {
            "android:name": "android.intent.category.BROWSABLE",
          },
        },
      ],
      data: [
        {
          $: {
            "android:scheme": props.deepLinkScheme,
            "android:host": props.deepLinkHost,
          },
        },
      ],
    });
  }
}

function findLauncherActivity(activities) {
  return activities.find(activity =>
    (activity["intent-filter"] || []).some(filter => {
      const hasMain = (filter.action || []).some(
        action => action?.$?.["android:name"] === "android.intent.action.MAIN",
      );
      const hasLauncher = (filter.category || []).some(
        category =>
          category?.$?.["android:name"] ===
          "android.intent.category.LAUNCHER",
      );
      return hasMain && hasLauncher;
    }),
  );
}

function withWidgetResources(config) {
  return withDangerousMod(config, [
    "android",
    async modConfig => {
      const resRoot = path.join(
        modConfig.modRequest.platformProjectRoot,
        "app",
        "src",
        "main",
        "res",
      );

      await writeResourceFile(
        path.join(resRoot, "xml", `${PROVIDER_XML_NAME}.xml`),
        getProviderXmlContents(),
      );
      await writeResourceFile(
        path.join(resRoot, "layout", "glancekit_widget_loading_layout.xml"),
        getLoadingLayoutContents(),
      );
      await writeResourceFile(
        path.join(
          resRoot,
          "drawable",
          "glancekit_progress_card_widget_preview.xml",
        ),
        getPreviewDrawableContents(),
      );

      return modConfig;
    },
  ]);
}

async function writeResourceFile(filePath, contents) {
  await fs.mkdir(path.dirname(filePath), { recursive: true });
  await fs.writeFile(filePath, contents, "utf8");
}

function getProviderXmlContents() {
  return `<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:description="@string/${WIDGET_DESCRIPTION_STRING}"
    android:initialLayout="@layout/glancekit_widget_loading_layout"
    android:minWidth="180dp"
    android:minHeight="96dp"
    android:previewImage="@drawable/glancekit_progress_card_widget_preview"
    android:resizeMode="horizontal|vertical"
    android:targetCellWidth="3"
    android:targetCellHeight="2"
    android:updatePeriodMillis="0"
    android:widgetCategory="home_screen" />
`;
}

function getLoadingLayoutContents() {
  return `<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#111827">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:text="Loading widget..."
        android:textColor="#FFFFFF" />
</FrameLayout>
`;
}

function getPreviewDrawableContents() {
  return `<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="#111827" />
    <corners android:radius="16dp" />
    <padding
        android:left="16dp"
        android:top="16dp"
        android:right="16dp"
        android:bottom="16dp" />
</shape>
`;
}

function sanitizeString(value) {
  return typeof value === "string" && value.trim().length > 0
    ? value.trim()
    : "";
}

const plugin = createRunOncePlugin(withGlanceKit, pkg.name, pkg.version);

module.exports = plugin;
module.exports.withGlanceKit = withGlanceKit;
