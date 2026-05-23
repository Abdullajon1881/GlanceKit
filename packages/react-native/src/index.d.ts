export type ProgressCardWidgetData = {
  title: string;
  subtitle: string;
  progress: number;
  deepLink?: string;
};

export declare const AndroidWidgets: {
  updateWidget(
    widgetId: string,
    data: ProgressCardWidgetData,
  ): Promise<void>;
};

declare const _default: {
  AndroidWidgets: typeof AndroidWidgets;
};

export default _default;
