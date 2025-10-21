# 开发指南

## 项目结构

```
BleHeartrate/
├── app/
│   ├── build.gradle.kts          # 应用模块构建配置
│   ├── proguard-rules.pro        # ProGuard 混淆规则
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/yifeplayte/bleheartrate/
│           │   ├── MainActivity.kt
│           │   ├── model/              # 数据模型
│           │   ├── repository/         # 数据仓库
│           │   ├── service/            # 后台服务
│           │   └── ui/                 # UI 组件
│           └── res/                    # 资源文件
├── build.gradle.kts                    # 项目根构建配置
├── settings.gradle.kts                 # Gradle 设置
└── gradle.properties                   # Gradle 属性
```

## 开发环境设置

### 要求
- Android Studio Hedgehog (2023.1.1) 或更高
- JDK 17+
- Android SDK API 34
- Kotlin 1.9.20+

### 配置步骤

1. **安装 Android Studio**
   - 下载并安装最新版本
   - 安装必要的 SDK 组件

2. **克隆项目**
   ```bash
   git clone https://github.com/YifePlayte/BleHeartrate.git
   cd BleHeartrate
   ```

3. **打开项目**
   - 使用 Android Studio 打开项目
   - 等待 Gradle 同步完成

4. **配置模拟器或设备**
   - 创建 Android 虚拟设备 (AVD)
   - 或连接物理设备

## 代码规范

### Kotlin 编码风格
- 遵循 [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- 使用 4 空格缩进
- 行长度限制 120 字符
- 使用有意义的变量名

### 命名约定
- **类名**: PascalCase (例: `MainActivity`, `HeartRateService`)
- **函数名**: camelCase (例: `connectToDevice`, `loadPairedDevices`)
- **变量名**: camelCase (例: `heartRateData`, `isServiceRunning`)
- **常量**: UPPER_SNAKE_CASE (例: `NOTIFICATION_ID`, `CHANNEL_ID`)

### 文件组织
- 每个类一个文件
- 相关的类放在同一包下
- UI 组件放在 `ui` 包
- 数据模型放在 `model` 包
- 服务放在 `service` 包

## 关键组件说明

### MainActivity
- 应用主入口
- 处理权限请求
- 管理 UI 状态
- 控制服务生命周期

### MainViewModel
- 管理应用状态
- 处理业务逻辑
- 与服务交互
- 数据持久化

### HeartRateService
- 前台服务
- BLE 连接管理
- 心率数据采集
- 自动重连逻辑

### FloatingWindowService
- 悬浮窗显示
- 心率数据更新
- 窗口拖动处理

### PreferencesRepository
- 设置数据存储
- 使用 DataStore
- 异步数据访问

## 构建和测试

### Debug 构建
```bash
./gradlew assembleDebug
```

### Release 构建
```bash
./gradlew assembleRelease
```

### 安装到设备
```bash
./gradlew installDebug
```

### 清理项目
```bash
./gradlew clean
```

## 调试技巧

### 日志输出
使用 Android Log 输出调试信息：
```kotlin
Log.d(TAG, "Debug message")
Log.i(TAG, "Info message")
Log.w(TAG, "Warning message")
Log.e(TAG, "Error message")
```

### 蓝牙调试
- 使用 `adb shell dumpsys bluetooth_manager` 查看蓝牙状态
- 使用蓝牙 HCI 日志捕获数据包

### UI 调试
- 启用 Layout Inspector
- 使用 Compose Preview
- 开启 Show Layout Bounds

## 常见问题

### 蓝牙连接失败
- 检查设备是否已配对
- 确认设备支持 BLE Heart Rate Service
- 查看 Logcat 中的错误信息

### 悬浮窗不显示
- 确认已授予悬浮窗权限
- Android 6.0+ 需要手动授权

### 编译错误
- 清理项目: `./gradlew clean`
- 同步 Gradle
- 检查 SDK 版本

## 贡献指南

### 提交代码
1. Fork 项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

### 代码审查
- 确保代码遵循规范
- 添加必要的注释
- 测试新功能
- 更新文档

### Issue 报告
- 清晰描述问题
- 提供复现步骤
- 包含设备信息
- 附加日志信息

## 依赖管理

### 主要依赖
- Jetpack Compose BOM 2023.10.01
- AndroidX Core KTX 1.12.0
- Material 3
- DataStore Preferences 1.0.0
- Coroutines 1.7.3

### 更新依赖
```bash
# 检查依赖更新
./gradlew dependencyUpdates
```

## 性能优化

### 建议
- 使用 ProGuard 混淆代码
- 优化图片资源
- 减少过度绘制
- 使用 Android Profiler

### 电池优化
- 合理使用前台服务
- 优化 BLE 扫描间隔
- 减少唤醒锁使用

## 安全性

### 最佳实践
- 不在代码中硬编码敏感信息
- 使用安全的通信协议
- 验证用户输入
- 遵循最小权限原则

### 权限处理
- 运行时请求权限
- 解释权限用途
- 优雅处理权限拒绝

## 资源

### 官方文档
- [Android Developer Docs](https://developer.android.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Docs](https://kotlinlang.org/docs/home.html)
- [Bluetooth Low Energy](https://developer.android.com/guide/topics/connectivity/bluetooth/ble-overview)

### 社区
- [Stack Overflow](https://stackoverflow.com/questions/tagged/android)
- [Kotlin Slack](https://kotlinlang.slack.com/)
- [Android Developers on Reddit](https://www.reddit.com/r/androiddev/)

## 许可证

MIT License - 详见 LICENSE 文件
