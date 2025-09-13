<template>
  <GlassCardBase :title="title" :icon="icon" :variant="computedVariant" :size="size" :hoverable="hoverable"
    :show-decoration="showDecoration" :extra-classes="extraClasses" :width="width" :height="height">
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
import GlassCardBase from './GlassCardBase.vue'

export default {
  name: 'GlassCardWithHeader',
  components: {
    GlassCardBase
  },
  props: {
    // 卡片标题
    title: {
      type: String,
      required: true
    },
    // 卡片图标
    icon: {
      type: String,
      default: '🔧'
    },
    // 卡片类型（兼容旧API）
    type: {
      type: String,
      default: 'default',
      validator: value => ['default', 'primary', 'success', 'warning', 'danger', 'info'].includes(value)
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
        'default': 'base',
        'primary': 'enhanced',
        'success': 'enhanced',
        'warning': 'enhanced',
        'danger': 'enhanced',
        'info': 'display'
      };

      return typeMap[this.type] || 'base';
    }
  }
}
</script>
