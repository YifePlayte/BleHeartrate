# BleHeartrate

一个通过蓝牙获取并通过悬浮窗或画中画显示实时心率数据的安卓软件

## 功能特性

- ❤️ **实时心率监测**: 通过蓝牙低功耗(BLE)连接心率监测设备
- 🎨 **Material You 设计**: 使用 Jetpack Compose 和 Material 3，主题色跟随系统
- 📱 **多种显示模式**: 
  - 悬浮窗显示
  - 画中画(PiP)模式
- 🔗 **智能蓝牙管理**:
  - 设备配对和取消配对
  - 自动重连功能
  - 手动连接/断开
  - 设备记忆功能
- ⚙️ **设置即首页**: 所有功能集成在主界面

## 技术栈

- **Kotlin**: 现代化 Android 开发语言
- **Jetpack Compose**: 声明式 UI 框架
- **Material You (Material 3)**: 动态主题系统
- **Bluetooth Low Energy (BLE)**: 心率数据获取
- **DataStore**: 本地数据持久化
- **Coroutines & Flow**: 异步编程和数据流管理
- **MVVM 架构**: 清晰的代码架构

## 系统要求

- Android 8.0 (API 26) 及以上
- 支持蓝牙低功耗(BLE)的设备
- 心率监测设备需支持标准 BLE Heart Rate Service (UUID: 0x180D)

## 权限说明

应用需要以下权限：

- **蓝牙权限**: 用于连接和读取心率设备数据
- **悬浮窗权限**: 用于显示悬浮窗
- **前台服务权限**: 保持心率监测持续运行

## 构建说明

### 前置要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17 或更高版本
- Android SDK (API 34)

### 构建步骤

1. 克隆仓库:
```bash
git clone https://github.com/YifePlayte/BleHeartrate.git
cd BleHeartrate
```

2. 使用 Android Studio 打开项目

3. 等待 Gradle 同步完成

4. 连接 Android 设备或启动模拟器

5. 点击 Run 按钮或执行:
```bash
./gradlew assembleDebug
```

## 使用说明

1. **启动应用**: 打开应用后进入设置界面
2. **授予权限**: 首次使用需要授予蓝牙和悬浮窗权限
3. **选择设备**: 点击"蓝牙设备"按钮，从已配对设备列表中选择心率监测设备
4. **连接设备**: 选择设备后点击"连接"按钮
5. **开始监测**: 点击"启动"按钮开始心率监测
6. **切换显示模式**: 在"显示模式"中选择悬浮窗或画中画模式
7. **停止监测**: 点击"停止"按钮结束监测

## 项目结构

```
app/src/main/java/com/yifeplayte/bleheartrate/
├── model/              # 数据模型
│   ├── BluetoothDeviceInfo.kt
│   ├── ConnectionState.kt
│   ├── DisplayMode.kt
│   └── HeartRateData.kt
├── repository/         # 数据仓库
│   └── PreferencesRepository.kt
├── service/           # 后台服务
│   ├── HeartRateService.kt
│   └── FloatingWindowService.kt
├── ui/                # 界面组件
│   ├── MainScreen.kt
│   ├── MainViewModel.kt
│   └── theme/
│       ├── Theme.kt
│       └── Type.kt
└── MainActivity.kt    # 主活动
```

## 支持的心率设备

应用支持所有遵循标准蓝牙心率服务规范的设备，包括：

- 心率带
- 智能手表
- 运动手环
- 医疗级心率监测设备

设备需要支持 BLE Heart Rate Service (0x180D) 和 Heart Rate Measurement Characteristic (0x2A37)。

## 已知问题

- 某些设备可能需要在系统蓝牙设置中先配对
- 画中画模式需要 Android 8.0 (API 26) 及以上
- 悬浮窗在 Android 6.0+ 需要手动授权

## 贡献

欢迎提交 Issue 和 Pull Request！

## 许可证

本项目采用 MIT 许可证 - 详见 LICENSE 文件

## 作者

YifePlayte

## 致谢

- Android 开源社区
- Jetpack Compose 团队
- Material Design 团队
