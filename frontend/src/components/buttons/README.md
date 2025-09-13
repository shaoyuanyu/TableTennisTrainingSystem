# 按钮组件体系（buttons）

- 统一目录：`src/components/buttons`
- 基座：`BaseButton.vue`（语义属性：color/variant/size/loading/disabled/block/round/circle，支持 icon-left/icon-right 插槽与 a/button 切换）
- 变体：Primary/Secondary/Success/Warning/Danger/Info/Outline/Ghost/IconButton
- 组合：ActionButtons（批量操作按钮容器），同时从此目录导出

快速上手：

```vue
<script setup>
import { PrimaryButton, OutlineButton, ActionButtons } from '@/components/buttons'
</script>

<template>
  <PrimaryButton label="提交" size="sm" />
  <OutlineButton label="取消" color="info" size="sm" />
  <ActionButtons
    :actions="[
      { text: '保存', type: 'primary' },
      { text: '删除', type: 'danger' },
    ]"
  />
</template>
```

迁移指引：

- 旧 `components/ModernButton.vue` 已变为薄封装，内部使用新 `buttons/ModernButton.vue`，可逐步替换为 `PrimaryButton` 等。
- `ActionButtons.vue` 已用 `BaseButton` 重写，视觉风格与项目一致；也可直接从 `buttons` 目录导入。

配色更新：

- 主色切换为登录页按钮同款紫色梯度（#667eea → #764ba2），已同步到 `styles/design-system.css` 与 `styles/variables.scss`。
