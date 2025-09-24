<template>
  <ButtonsModernButton v-bind="$props" v-on="$attrs">
    <template v-if="$slots.default" #default>
      <slot />
    </template>
    <template v-if="$slots['icon-left']" #icon-left>
      <slot name="icon-left" />
    </template>
    <template v-if="$slots['icon-right']" #icon-right>
      <slot name="icon-right" />
    </template>
  </ButtonsModernButton>
</template>

<script>
import ButtonsModernButton from './buttons/ModernButton.vue'

export default {
  name: 'ModernButton',
  components: { ButtonsModernButton },
  inheritAttrs: false,
  props: {
    text: { type: String, default: '' },
    type: { type: String, default: 'primary' },
    size: { type: String, default: 'medium' },
    variant: { type: String, default: 'solid' },
    iconLeft: { type: String, default: '' },
    iconRight: { type: String, default: '' },
    disabled: { type: Boolean, default: false },
    loading: { type: Boolean, default: false },
    block: { type: Boolean, default: false },
    extraClasses: { type: String, default: '' },
  },
}
</script>

<style scoped></style>
