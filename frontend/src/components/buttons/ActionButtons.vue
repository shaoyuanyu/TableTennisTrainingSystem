<template>
  <ActionButtonsImpl v-bind="$props" v-on="$attrs" />
</template>

<script>
import ActionButtonsImpl from '../ActionButtons.vue'
export default {
  name: 'ActionButtons',
  components: { ActionButtonsImpl },
  inheritAttrs: false,
  props: { ...ActionButtonsImpl.props },
  emits: ActionButtonsImpl.emits,
}
</script>
