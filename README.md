# 📚 CampusAI — 智慧校园学习助手

> 成都理工大学 · 物联网工程 · 大三 · Android 应用开发技术期末作业

一个集**课程表、待办事项、考试提醒、AI 学习助手**于一体的 Android 校园效率应用。

<p align="center">
  <img src="https://img.shields.io/badge/Android-Java-brightgreen?style=flat-square&logo=android" />
  <img src="https://img.shields.io/badge/Database-Room-orange?style=flat-square" />
  <img src="https://img.shields.io/badge/Network-Retrofit-blue?style=flat-square" />
  <img src="https://img.shields.io/badge/AI-DeepSeek-purple?style=flat-square" />
  <img src="https://img.shields.io/badge/Theme-Material%20Design%203-FF6F00?style=flat-square" />
</p>

## ✨ 核心功能

| 模块 | 功能 |
|------|------|
| 📚 **智能课程表** | 按星期筛选、节次可视化、同时间段冲突检测 |
| ✅ **待办管理** | 日期选择器、优先级/分类标签、完成勾选自动划线 |
| 📝 **考试提醒** | 倒计时天数、系统通知提醒、支持编辑 |
| 🤖 **AI 学习助手** | DeepSeek API · SSE 流式输出 · 多轮对话上下文 · Markdown 渲染 |
| 🌙 **暗色模式** | 深棕暖调暗色主题 · 一键切换 · 切换后状态保持 |
| 🔑 **用户隔离** | 每用户独立 API Key · 课程/待办/考试/聊天按用户隔离 |

## 🏗️ 技术架构

```
表现层 (8 Activity + 5 Fragment)
  ↕
适配器层 (5 RecyclerView Adapter)
  ↕
模型层 (6 Room Entity + 1 UI Model)
  ↕
数据访问层 (6 Room DAO + Database 单例)  ←→  API 层 (Retrofit + OkHttp + SSE)
  ↕
工具层 (Session / Theme / Notification)
```

| 技术 | 用途 |
|------|------|
| **Java 8** | Android 原生开发 |
| **Room** | SQLite ORM · 6 张表 · 5 次 Migration |
| **Retrofit 2** | HTTP 客户端 · `@Streaming` SSE |
| **DeepSeek API** | AI 对话 · `deepseek-chat` 模型 |
| **Material Components** | CardView · BottomNavigation · FAB · Switch |
| **Markwon** | Markdown 渲染 |

## 🚀 快速开始

1. **Clone**
   ```bash
   git clone https://github.com/Kobe-tech-space/Android-Final-Work.git
   ```

2. **Android Studio** → Open → 选择项目目录

3. **Sync Gradle** → **Run** ▶️

4. 登录页可填入自己的 **DeepSeek API Key**（可选，不填则 AI 功能不可用）

## 📁 项目结构

```
app/src/main/java/com/example/finalwork/
├── activity/      # 8 个 Activity（登录/注册/主页/编辑页）
├── fragment/      # 5 个 Fragment（首页/课程/待办/AI/个人）
├── adapter/       # 5 个 RecyclerView Adapter
├── entity/        # 6 个 Room Entity
├── dao/           # 6 个 Room DAO
├── database/      # AppDatabase 单例 + Migration
├── api/           # DeepSeek Retrofit 接口
├── model/         # UI 模型
└── utils/         # SessionManager / ThemeManager / NotificationHelper
```

## 🌐 项目介绍页

GSAP 动画驱动的项目展示页面，滚动浏览全部功能：

👉 **[项目介绍页](https://kobe-tech-space.github.io/Android-Final-Work/)**

> 本地查看：`docs/index.html`，浏览器直接打开即可。

## 🎨 设计

暖橙配色（`#E76F51`）+ 奶油白背景（`#FFF9F0`）+ 大圆角卡片。
