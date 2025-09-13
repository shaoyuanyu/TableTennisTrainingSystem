<template>
  <BaseButton color="warning" v-bind="$props" v-on="$attrs">
    <slot />
  </BaseButton>
</template>

<script>
import BaseButton from './BaseButton.vue'
export default {
  name: 'WarningButton',
  components: { BaseButton },
  inheritAttrs: false,
  props: { ...BaseButton.props },
}
</script>
