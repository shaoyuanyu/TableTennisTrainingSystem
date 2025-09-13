<template>
  <BaseButton color="danger" v-bind="$props" v-on="$attrs">
    <slot />
  </BaseButton>
</template>

<script>
import BaseButton from './BaseButton.vue'
export default {
  name: 'DangerButton',
  components: { BaseButton },
  inheritAttrs: false,
  props: { ...BaseButton.props },
}
</script>
