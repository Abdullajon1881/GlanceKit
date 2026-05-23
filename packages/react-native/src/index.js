const {NativeModules, Platform} = require('react-native');

const nativeModule = NativeModules.GlanceKitModule;

const AndroidWidgets = {
  updateWidget(widgetId, data) {
    if (Platform.OS !== 'android') {
      return Promise.reject(
        new Error('AndroidWidgets.updateWidget is only available on Android.'),
      );
    }

    if (!nativeModule || typeof nativeModule.updateWidget !== 'function') {
      return Promise.reject(
        new Error(
          'GlanceKit native module is not linked. Make sure the Android package is installed and registered.',
        ),
      );
    }

    return nativeModule.updateWidget(widgetId, data);
  },
};

exports.AndroidWidgets = AndroidWidgets;
exports.default = {
  AndroidWidgets,
};
