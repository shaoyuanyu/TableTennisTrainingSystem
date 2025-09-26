<template>
  <BaseButton variant="glass" v-bind="$props" v-on="$attrs">
    <slot />
  </BaseButton>
</template>

<script>
import BaseButton from './BaseButton.vue'

export default {
  name: 'GhostButton',
  components: { BaseButton },
  inheritAttrs: false,
  props: { ...BaseButton.props },
}
</script>
