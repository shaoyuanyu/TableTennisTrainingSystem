<template>
  <GlassCardBase :title="title" :subtitle="subtitle" :icon="icon" :description="description" :tags="tags"
    :variant="computedVariant" :size="size" :hoverable="hoverable" :show-decoration="showDecoration"
    :extra-classes="extraClasses" :width="width" :height="height">
    <!-- 头部操作区域 -->
    <template v-if="$slots.actions" #headerActions>
      <slot name="actions"></slot>
    </template>

    <!-- 主要内容 -->
    <slot></slot>

    <!-- 底部区域 -->
    <template v-if="$slots.footer" #footer>
      <slot name="footer"></slot>
    </template>
  </GlassCardBase>
</template>

<script>
import GlassCardBase from '../base/GlassCardBase.vue'

export default {
  name: 'GlassDisplayCard',
  components: {
    GlassCardBase
  },
  props: {
    // 卡片标题
    title: {
      type: String,
      required: true
    },
    // 副标题（通常是CSS类名）
    subtitle: {
      type: String,
      default: ''
    },
    // 图标
    icon: {
      type: String,
      default: '🔧'
    },
    // 描述文字
    description: {
      type: String,
      default: ''
    },
    // 标签数组 [{ text: '标签名', type: 'primary|secondary|success|warning|info' }]
    tags: {
      type: Array,
      default: () => []
    },
    // 卡片类型（兼容旧API）
    type: {
      type: String,
      default: 'glass', // glass, content, glass-enhanced
      validator: value => ['glass', 'content', 'glass-enhanced'].includes(value)
    },
    // 新的变体系统
    variant: {
      type: String,
      default: '',
      validator: value => value === '' || ['base', 'display', 'content', 'enhanced', 'minimal'].includes(value)
    },
    // 尺寸
    size: {
      type: String,
      default: 'medium',
      validator: value => ['small', 'medium', 'large'].includes(value)
    },
    // 是否可悬浮
    hoverable: {
      type: Boolean,
      default: true
    },
    // 是否显示装饰线
    showDecoration: {
      type: Boolean,
      default: true
    },
    // 额外的CSS类
    extraClasses: {
      type: String,
      default: ''
    },
    // 自定义尺寸
    width: {
      type: String,
      default: ''
    },
    height: {
      type: String,
      default: ''
    }
  },
  computed: {
    computedVariant() {
      // 优先使用新的 variant，否则根据旧的 type 映射
      if (this.variant) {
        return this.variant;
      }

      const typeMap = {
        'glass': 'display',
        'content': 'content',
        'glass-enhanced': 'enhanced'
      };

      return typeMap[this.type] || 'display';
    }
  }
}
</script>
