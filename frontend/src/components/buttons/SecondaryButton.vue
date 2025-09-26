<template>
  <BaseButton color="secondary" v-bind="$props" v-on="$attrs">
    <slot />
  </BaseButton>
</template>

<script>
import BaseButton from './BaseButton.vue'

export default {
  name: 'SecondaryButton',
  components: { BaseButton },
  inheritAttrs: false,
  props: { ...BaseButton.props },
}
</script>
