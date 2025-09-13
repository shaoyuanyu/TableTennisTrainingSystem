<template>
  <BaseButton
    :label="text"
    :color="type"
    :size="size"
    :variant="variant"
    :icon-left="iconLeft"
    :icon-right="iconRight"
    :disabled="disabled"
    :loading="loading"
    :block="block"
    :extra-classes="extraClasses"
    v-bind="$attrs"
    @click="$emit('click', $event)"
  >
    <template v-if="$slots.default" #default>
      <slot />
    </template>
    <template v-if="$slots['icon-left']" #icon-left>
      <slot name="icon-left" />
    </template>
    <template v-if="$slots['icon-right']" #icon-right>
      <slot name="icon-right" />
    </template>
  </BaseButton>
</template>

<script>
import BaseButton from './BaseButton.vue'
export default {
  name: 'ModernButton',
  components: { BaseButton },
  inheritAttrs: false,
  props: {
    text: { type: String, default: '' },
    type: {
      type: String,
      default: 'primary',
      validator: (v) =>
        ['primary', 'secondary', 'success', 'warning', 'danger', 'info'].includes(v),
    },
    size: {
      type: String,
      default: 'medium',
      validator: (v) => ['small', 'medium', 'large', 'sm', 'md', 'lg'].includes(v),
    },
    variant: {
      type: String,
      default: 'solid',
      validator: (v) => ['solid', 'outline', 'glass'].includes(v),
    },
    iconLeft: { type: String, default: '' },
    iconRight: { type: String, default: '' },
    disabled: { type: Boolean, default: false },
    loading: { type: Boolean, default: false },
    block: { type: Boolean, default: false },
    extraClasses: { type: String, default: '' },
  },
  emits: ['click'],
}
</script>
