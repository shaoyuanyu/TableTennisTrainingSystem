<template>
  <BaseButton color="success" v-bind="$props" v-on="$attrs">
    <slot />
  </BaseButton>
</template>

<script>
import BaseButton from './BaseButton.vue'
export default {
  name: 'SuccessButton',
  components: { BaseButton },
  inheritAttrs: false,
  props: { ...BaseButton.props },
}
</script>
