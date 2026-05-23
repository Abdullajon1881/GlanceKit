const path = require('path');
const {getDefaultConfig, mergeConfig} = require('@react-native/metro-config');

/**
 * Metro configuration
 * https://reactnative.dev/docs/metro
 *
 * @type {import('metro-config').MetroConfig}
 */
const projectRoot = __dirname;
const workspaceRoot = path.resolve(projectRoot, '../..');
const glanceKitReactNativeRoot = path.resolve(
  workspaceRoot,
  'packages/react-native',
);

const config = {
  watchFolders: [workspaceRoot, glanceKitReactNativeRoot],
  resolver: {
    unstable_enableSymlinks: true,
    nodeModulesPaths: [
      path.resolve(projectRoot, 'node_modules'),
      path.resolve(workspaceRoot, 'node_modules'),
    ],
    extraNodeModules: {
      '@glancekit/react-native': glanceKitReactNativeRoot,
    },
  },
};

module.exports = mergeConfig(getDefaultConfig(__dirname), config);
